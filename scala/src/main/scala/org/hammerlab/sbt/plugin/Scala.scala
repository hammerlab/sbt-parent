package org.hammerlab.sbt.plugin

import hammerlab.bytes
import hammerlab.bytes._
import hammerlab.show._
import org.hammerlab.sbt.{ ContainerPlugin, Lib }
import org.hammerlab.sbt.deps.Group._
import org.hammerlab.sbt.deps.IsScalaJS
import org.hammerlab.sbt.plugin.Deps.deps
import org.hammerlab.sbt.plugin.Versions.autoImport.versions
import sbt.Keys._
import sbt.plugins.SbtPlugin
import sbt.{ Show ⇒ _, _ }
import sourcecode.Name

object Scala
  extends ContainerPlugin(
    Deps,
    Versions
  )
{

  object autoImport
    extends bytes.syntax {

    val scala_lang    = Lib("org.scala-lang" ^ "scala-library" ^ "")
    val scala_reflect = Lib("org.scala-lang" ^ "scala-reflect" ^ "")

    def plugin(implicit name: Name): Project =
      Project(
                 name.value,
        new File(name.value)
      )
      .enablePlugins(
        SbtPlugin
      )

    val enableMacroParadise =
      Seq(
        addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.0" cross CrossVersion.full),
        deps += scala_reflect
      )

    val debugMacros = scalacOptions += "-Ymacro-debug-lite"

    // Macros and doc-generation have many rough edges, so this is frequently useful
    val skipDoc = publishArtifact in (sbt.Compile, packageDoc) := false
    val emptyDocJar = sources in (sbt.Compile, doc) := Seq()

    val partialUnification = scalacOptions += "-Ypartial-unification"

    object kindProjector extends Lib(
      "org.spire-math" ^^ "kind-projector" ^ "0.9.8"
    ) {
      override val settings =
        _base.toModuleIDs(IsScalaJS.no) match {
          case Seq(dep) ⇒ addCompilerPlugin(dep)
        }
    }

    val logImplicits = scalacOptions += "-Xlog-implicits"

    // "Hidden" test-resources (resources whose basenames start with ".") are not moved into target/ dirs (and
    // therefore not present on tests' classpath) by default; this setting overrides that behavior to include them
    val includeHiddenTestResources = excludeFilter in sbt.Test := NothingFilter

    val scalameta: SettingsDefinition = Seq(
      addCompilerPlugin("org.scalameta" % "paradise" % "3.0.0-M10" cross CrossVersion.full),
      scalacOptions += "-Xplugin-require:macroparadise",
      libraryDependencies += "org.scalameta" %% "scalameta" % "1.8.0" % sbt.Provided,
      scalacOptions in (sbt.Compile, console) ~= (_ filterNot (_ contains "paradise")),  // macroparadise plugin doesn't work in repl yet.
      `2.12`.version := "2.12.4"
    )

    val consolePkgs = settingKey[Seq[String]]("Wildcard-imports to add to console startup commands")
    val consoleImports = settingKey[Seq[String]]("Imports to add to console startup commands")

    object consoleImport {
      def apply(imports: String*) = consoleImports ++= imports
    }

    object consolePkg {
      def apply(pkgs: String*) = consolePkgs ++= pkgs
    }

    sealed abstract class ScalaMajorVersion(v: String, default: String) {
      val version = SettingKey[ String](   s"scala-$v-version", s"Default scalaVersion to use for major version $v")
      val       ? = SettingKey[Boolean](s"is-scala-$v-version", s"True iff the scalaBinaryVersion is $v")

      lazy val only =
        Seq(
          ScalaVersion.default := this,
          scalaVersions := Seq(this)
        )
      lazy val  add = scalaVersions +=     this
      def unary_+() = add

      val defaults =
        Seq(
          version := default,
          ? := scalaBinaryVersion.value == v
        )
    }
    object ScalaMajorVersion {
      implicit def toSettings(s: ScalaMajorVersion): SettingsDefinition = s.only
    }
    case object `2.10` extends ScalaMajorVersion("2.10", "2.10.7")
    case object `2.11` extends ScalaMajorVersion("2.11", "2.11.12")
    case object `2.12` extends ScalaMajorVersion("2.12", "2.12.8")
    case object `2.13` extends ScalaMajorVersion("2.13", "2.13.8-M5")

    object scalac {
      implicit val showBytes: Show[Bytes] = { _.toString.filter(_ != 'B').toLowerCase }
      def xms(bytes: Bytes) = scalacOptions += show"-J-Xms$bytes"
      def xmx(bytes: Bytes) = scalacOptions += show"-J-Xmx$bytes"
    }

    object ScalaVersion {
      val default = SettingKey[ScalaMajorVersion]("defaultScalaVersion", "Default scala major version; wrapper for scalaVersion")
      val defaults: SettingsDefinition =
        `2.10`.defaults ++
        `2.11`.defaults ++
        `2.12`.defaults ++
        `2.13`.defaults
    }

    val scalaVersions = settingKey[Seq[ScalaMajorVersion]]("Wrapper for crossScalaVersions")
  }

  import autoImport._

  globals(
    // Primary Build is for Scala 2.12 by default
    ScalaVersion.default := `2.12`,

    // Build for Scala 2.12 only, by default
    scalaVersions :=
      Seq(
        `2.12`
      ),
    ScalaVersion.defaults  // TODO: register this as a non-dep-related settings-provider
  )

  projects(
    scalaVersion  := (
      ScalaVersion.default.value match {
        case `2.10` ⇒ `2.10`.version.value
        case `2.11` ⇒ `2.11`.version.value
        case `2.12` ⇒ `2.12`.version.value
      }
    ),
    crossScalaVersions := {
      scalaVersions.value.map {
        case `2.10` ⇒ `2.10`.version.value
        case `2.11` ⇒ `2.11`.version.value
        case `2.12` ⇒ `2.12`.version.value
      }
    },

    scalacOptions ++= Seq(
      "-feature",
      "-language:existentials",
      "-language:implicitConversions",
      "-language:postfixOps",
      "-language:higherKinds",
      "-language:reflectiveCalls"
    ),

    versions ++= Seq(
      scala_lang    → scalaVersion.value,
      scala_reflect → scalaVersion.value
    ),

    consolePkgs := Nil,
    initialCommands += consolePkgs.value.map(pkg ⇒ s"import $pkg._").mkString("", "\n", "\n"),

    consoleImports := Nil,
    initialCommands += consoleImports.value.map(i ⇒ s"import $i").mkString("", "\n", "\n")
  )
}
