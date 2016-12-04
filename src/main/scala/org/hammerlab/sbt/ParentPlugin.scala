package org.hammerlab.sbt

import sbt.Keys.{ testFrameworks, _ }
import sbt.TestFrameworks.ScalaTest
import sbt._
import sbtassembly.AssemblyPlugin
import sbtassembly.AssemblyPlugin.autoImport.{ ShadeRule, assembly, assemblyExcludedJars, assemblyJarName, assemblyShadeRules }
import sbtassembly.AssemblyKeys.assemblyOption

object ParentPlugin extends AutoPlugin {

  object autoImport {
    val libraries = settingKey[Map[Symbol, ModuleID]]("Some common dependencies/versions")

    val scalatestVersion = settingKey[String]("Version of scalatest test-dep to use")

    val sparkVersion = settingKey[String]("Spark version to use")
    val spark2Version = settingKey[String]("When cross-building for Spark 1.x and 2.x, this version will be used when -Dspark2 is set.")

    val hadoopVersion = settingKey[String]("Hadoop version to use")

    val githubUser = settingKey[String]("Github user/org to point to")

    val providedDeps = settingKey[Seq[ModuleID]]("Dependencies to be scoped 'provided'")
    val testDeps = settingKey[Seq[ModuleID]]("Dependencies to be scoped 'test'")

    val shadedDeps = settingKey[Seq[ModuleID]]("When set, the main JAR produced will include these libraries shaded")
    val shadeRenames = settingKey[Seq[(String, String)]]("Shading renames to perform")
  }

  import autoImport._

  // Helper for appending "_spark2" to a project's name iff the "spark2" env var is set.
  def sparkName(name: String): String =
    if (System.getProperty("spark2") != null)
      s"${name}_spark2"
    else
      name

  override def trigger: PluginTrigger = allRequirements

  override def requires: Plugins = super.requires && AssemblyPlugin

  // Evaluate these settings to publish a "thin" assembly JAR instead of the default, unshaded JAR.
  val publishThinShadedJar: SettingsDefinition =
    Seq(
      assemblyExcludedJars in assembly := {
        val log = streams.value.log

        val cp = (fullClasspath in assembly).value
        val shadedDepJars =
          shadedDeps
            .value
            .map(
              dep => {
                val crossFn =
                  CrossVersion(
                    dep.crossVersion,
                    scalaVersion.value,
                    scalaBinaryVersion.value
                  )
                  .getOrElse((x: String) => x)

                val name = crossFn(dep.name)
                s"$name-${dep.revision}.jar"
              }
            )
            .toSet

        log.debug(s"Looking for jars to shade:\n${shadedDepJars.mkString("\t", "\n\t", "")}")

        cp filter { path => {
          val name = path.data.getName

          val exclude = !shadedDepJars(name)
          if (exclude)
            log.debug(s"Skipping JAR: $name")
          else
            log.debug(s"Shading classes jar: $name")

          exclude
        }}
      },

      assemblyJarName in assembly := {
        val newName = s"${name.value}_${scalaBinaryVersion.value}-${version.value}.jar"
        streams.value.log.debug(s"overwriting assemblyJarName: ${assemblyJarName in assembly value} -> $newName")
        newName
      },

      // Add a classifier to the default (unshaded) JAR.
      artifactClassifier in packageBin := Some("unshaded"),

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
    ) ++ addArtifact(artifact in (Compile, assembly), assembly)  // Publish the assembly JAR.

  override def projectSettings: Seq[_root_.sbt.Def.Setting[_]] = {

    val mavenSettings =
      Seq(
        publishTo := {
          val nexus = "https://oss.sonatype.org/"
          if (isSnapshot.value)
            Some("snapshots" at nexus + "content/repositories/snapshots")
          else
            Some("releases" at nexus + "service/local/staging/deploy/maven2")
        },

        publishMavenStyle := true,
        publishArtifact in Test := false,
        pomIncludeRepository := { _ => false },

        pomExtra := {
          val n = name.value
          val user = githubUser.value
          <url>
            https://github.com/{user}/{n}
          </url>
            <licenses>
              <license>
                <name>Apache License</name>
                <url>https://raw.github.com/{user}/{n}/master/LICENSE</url>
                <distribution>repo</distribution>
              </license>
            </licenses>
            <scm>
              <url>git@github.com:{user}/{n}.git</url>
              <connection>scm:git:git@github.com:{user}/{n}.git</connection>
              <developerConnection>scm:git:git@github.com:{user}/{n}.git</developerConnection>
            </scm>
            <developers>
              <developer>
                <id>hammerlab</id>
                <name>Hammer Lab</name>
                <url>https://github.com/{user}</url>
              </developer>
            </developers>
        }
      )

    val testSettings =
      Seq(
        scalatestVersion := "3.0.0",
        testFrameworks := Seq(ScalaTest),

        testDeps := Seq(libraries.value('scalatest)),
        libraryDependencies ++= testDeps.value.map(_ % "test"),

        parallelExecution in Test := false
      )

    val versionSettings =
      Seq(
        sparkVersion := "1.6.3",
        spark2Version := "2.0.2",

        hadoopVersion := "2.6.0",

        libraries := {
          val sv =
            if (System.getProperty("spark2") != null)
              spark2Version.value
            else
              sparkVersion.value

          Map(
            'args4j -> "args4j" % "args4j" % "2.33",
            'args4s -> "org.hammerlab" %% "args4s" % "1.0.0",
            'bdg_formats -> "org.bdgenomics.bdg-formats" % "bdg-formats" % "0.10.0",
            'bdg_utils_cli -> "org.bdgenomics.utils" %% "utils-cli" % "0.2.10",
            'hadoop -> "org.apache.hadoop" % "hadoop-client" % hadoopVersion.value,
            'hadoop_bam -> ("org.seqdoop" % "hadoop-bam" % "7.7.1" exclude("org.apache.hadoop", "hadoop-client")),
            'kryo -> "com.esotericsoftware.kryo" % "kryo" % "2.21",
            'mllib -> "org.apache.spark" %% "spark-mllib" % sv,
            'quinine_core -> ("org.bdgenomics.quinine" %% "quinine-core" % "0.0.2" exclude("org.bdgenomics.adam", "adam-core")),
            'scalatest -> "org.scalatest" %% "scalatest" % scalatestVersion.value,
            'slf4j -> "org.clapper" %% "grizzled-slf4j" % "1.0.3",
            'spark -> "org.apache.spark" %% "spark-core" % sv,
            'spark_commands -> "org.hammerlab" %% "spark-commands" % "1.0.1",
            'spark_tests -> "org.hammerlab" %% "spark-tests" % "1.2.1",
            'spark_testing_base -> ("com.holdenkarau" %% "spark-testing-base" % s"${sv}_0.4.7" exclude("org.scalatest", s"scalatest_${scalaBinaryVersion.value}")),
            'spark_util -> "org.hammerlab" %% "spark-util" % "1.1.1",
            'spire -> "org.spire-math" %% "spire" % "0.11.0",
            'test_utils -> "org.hammerlab" %% "test-utils" % "1.0.1"
          )
        }
      )

    val assemblySettings =
      Seq(
        shadeRenames := Nil,
        shadedDeps := Nil,

        assemblyShadeRules in assembly ++= Seq(
          ShadeRule.rename(
            shadeRenames.value: _*
          ).inAll
        ),

        test in assembly := {},

        assemblyOption in assembly := (assemblyOption in assembly).value.copy(includeScala = false)
      )

    val depsSettings =
      Seq(
        providedDeps := Nil,
        shadedDeps := Nil,

        libraryDependencies ++= providedDeps.value.map(_ % "provided"),
        libraryDependencies ++= shadedDeps.value,

        excludeDependencies += SbtExclusionRule("javax.servlet", "servlet-api")
      )

    Seq(
      organization := "org.hammerlab",
      githubUser := "hammerlab",
      scalaVersion := "2.11.8",
      crossScalaVersions := Seq("2.10.6", "2.11.8")
    ) ++
      depsSettings ++
      testSettings ++
      versionSettings ++
      mavenSettings ++
      assemblySettings
  }
}
