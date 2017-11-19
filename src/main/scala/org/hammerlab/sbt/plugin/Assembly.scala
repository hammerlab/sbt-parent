package org.hammerlab.sbt.plugin

import org.hammerlab.sbt.deps.Dep
import org.hammerlab.sbt.plugin.Deps.autoImport.deps
import org.hammerlab.sbt.plugin.Scala.autoImport.appendCrossVersion
import org.hammerlab.sbt.plugin.Versions.versionsMap
import sbt.Keys._
import sbt._
import sbtassembly.AssemblyKeys.assemblyOption
import sbtassembly.AssemblyPlugin.autoImport._
import sbtassembly.{ AssemblyPlugin, PathList }

object Assembly
  extends Plugin(
    AssemblyPlugin,
    Deps,
    Scala
  ) {

  object autoImport {
    val shadedDeps = settingKey[Seq[Dep]]("When set, the main JAR produced will include these libraries shaded")
    val shadeRenames = settingKey[Seq[(String, String)]]("Shading renames to perform")

    val assemblyIncludeScala = settingKey[Boolean]("When set, include Scala libraries in the assembled JAR")

    val takeFirstLog4JProperties =
      assemblyMergeStrategy in assembly := {
        // Two org.bdgenomics deps include the same log4j.properties.
        case PathList("log4j.properties") ⇒ MergeStrategy.first
        case x ⇒ (assemblyMergeStrategy in assembly).value(x)
      }

    val assemblyExcludeLib =
      assemblyExcludedJars in assembly ++= {
        (fullClasspath in assembly).value.filter {
          _.data.getParent.endsWith("/lib")
        }
      }

    // Evaluate these settings to build a "thin" assembly JAR instead of the default and publish it in place of the
    // usual (unshaded) JAR.
    val publishThinShadedJar: SettingsDefinition =
      Seq(
        assemblyExcludedJars in assembly := {
          val log = streams.value.log

          val cp = (fullClasspath in assembly).value

          // Build best-guesses of basenames of JARs corresponding to the deps we want to shade: s"$name-$version.jar".
          val shadedDepJars =
            shadedDeps
              .value
              .flatMap(
                _
                  .withVersion(versionsMap.value)
                  .toModuleIDs(appendCrossVersion.value)
              )
              .map {
                dep ⇒
                  val crossFn =
                    CrossVersion(
                      dep.crossVersion,
                      scalaVersion.value,
                      scalaBinaryVersion.value
                    )
                    .getOrElse((x: String) ⇒ x)

                  val name = crossFn(dep.name)
                  s"$name-${dep.revision}.jar"
              }
              .toSet

          log.debug(s"Looking for jars to shade:\n${shadedDepJars.mkString("\t", "\n\t", "")}")

          // Scan the classpath flagging JARs *to exclude*: all JARs whose basenames don't match our JARs-to-shade list
          // from above.
          cp filter {
            path ⇒
              val name = path.data.getName

              val exclude = !shadedDepJars(name)

              if (exclude)
                log.debug(s"Skipping JAR: $name")
              else
                log.debug(s"Shading classes jar: $name")

              exclude
          }
        },

        assemblyJarName in assembly := {
          val newName = s"${name.value}_${scalaBinaryVersion.value}-${version.value}.jar"
          streams.value.log.debug(s"overwriting assemblyJarName: ${assemblyJarName in assembly value} -> $newName")
          newName
        },

        // Add a classifier to the default (unshaded) JAR.
        artifactClassifier in (Compile, packageBin) := Some("unshaded"),

        // Don't add "unshaded" classifier to -tests JAR.
        artifactClassifier in (sbt.Test, packageBin) := None,

        artifact in (Compile, assembly) := {
          // Make the assembly JAR the unclassified artifact.
          (artifact in (Compile, assembly)).value.copy(classifier = None)
        },

        packagedArtifacts := {
          // Don't publish the unshaded JAR.
          val newArtifacts = packagedArtifacts.value.filterKeys(_.classifier != Some("unshaded"))
          streams.value.log.debug(
            s"packagedArtifacts, after removing unshaded JAR:\n${newArtifacts.mkString("\t", "\n\t", "")}"
          )
          newArtifacts
        }
      ) ++
        addArtifact(artifact in (Compile, assembly), assembly)  // Publish the assembly JAR.

    val publishAssemblyJar =
      Seq(
        artifact in (Compile, assembly) := {
          val art = (artifact in (Compile, assembly)).value
          art.copy(`classifier` = Some("assembly"))
        }
      ) ++
        addArtifact(artifact in (Compile, assembly), assembly)

    val main = settingKey[String]("Main class; non-Option wrapper for `mainClass`")
  }

  import autoImport._

  override def projectSettings: Seq[Def.Setting[_]] =
    Seq(
      shadeRenames := Nil,
      shadedDeps := Nil,

      // If any shadeRenames are specified, apply them.
      assemblyShadeRules in assembly ++= Seq(
        ShadeRule.rename(
          shadeRenames.value: _*
        ).inAll
      ),

      // Don't run tests when building assembly JAR, by default.
      test in assembly := {},

      // Don't include scala in the assembly JAR, by default; if it is used with Spark downstream, the runtime will
      // include the Scala libraries.
      assemblyIncludeScala := false,

      // If the user overrides the above by setting assemblyIncludeScala to true, pick that up here.
      assemblyOption in assembly := (assemblyOption in assembly).value.copy(includeScala = assemblyIncludeScala.value),

      deps ++= shadedDeps.value,

      main := "",
      mainClass := (
        if (main.value.isEmpty)
          None
        else
          Some(main.value)
        )
    )
}
