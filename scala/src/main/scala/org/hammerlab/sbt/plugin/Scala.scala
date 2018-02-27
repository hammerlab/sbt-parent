package org.hammerlab.sbt.plugin

import org.hammerlab.sbt.deps.Group._
import org.hammerlab.sbt.plugin.Deps.autoImport.deps
import org.hammerlab.sbt.plugin.Versions.autoImport.versions
import sbt.Keys._
import sbt._

object Scala
  extends Plugin(
    Deps,
    Versions
  ) {

  object autoImport {
    val isScala210 = settingKey[Boolean]("True iff the Scala binary version is 2.10")
    val isScala211 = settingKey[Boolean]("True iff the Scala binary version is 2.11")
    val isScala212 = settingKey[Boolean]("True iff the Scala binary version is 2.12")

    val noCrossPublishing =
      Seq(
        crossScalaVersions := Nil,
        crossPaths := false
      )

    val scala210Version = settingKey[String]("Patch version of Scala 2.10.x line to use")
    val scala211Version = settingKey[String]("Patch version of Scala 2.11.x line to use")
    val scala212Version = settingKey[String]("Patch version of Scala 2.12.x line to use")

    val addScala210 = crossScalaVersions += scala210Version.value
    val addScala211 = crossScalaVersions += scala211Version.value
    val addScala212 = crossScalaVersions += scala212Version.value

    val omitScala210 = crossScalaVersions -= scala210Version.value

    val scala210Only =
      Seq(
             scalaVersion  :=     scala210Version.value,
        crossScalaVersions := Seq(scala210Version.value)
      )

    val scala211Only =
      Seq(
             scalaVersion  :=     scala211Version.value,
        crossScalaVersions := Seq(scala211Version.value)
      )

    val scala212Only =
      Seq(
        scalaVersion := scala212Version.value,
        crossScalaVersions := Seq(scala212Version.value)
      )

    val scala_lang    = "org.scala-lang" ^ "scala-library"
    val scala_reflect = "org.scala-lang" ^ "scala-reflect"

    val enableMacroParadise =
      Seq(
        addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.0" cross CrossVersion.full),
        deps += scala_reflect % scalaVersion.value
      )

    val debugMacros = scalacOptions += "-Ymacro-debug-lite"

    // Macros and doc-generation have many rough edges, so this is frequently useful
    val skipDoc = publishArtifact in (sbt.Compile, packageDoc) := false
    val emptyDocJar = sources in (sbt.Compile, doc) := Seq()

    val scalameta = Seq(
      addCompilerPlugin("org.scalameta" % "paradise" % "3.0.0-M10" cross CrossVersion.full),
      scalacOptions += "-Xplugin-require:macroparadise",
      libraryDependencies += "org.scalameta" %% "scalameta" % "1.8.0" % sbt.Provided,
      scalacOptions in (sbt.Compile, console) ~= (_ filterNot (_ contains "paradise")) // macroparadise plugin doesn't work in repl yet.
    )

    val consolePkgs = settingKey[Seq[String]]("Wildcard-imports to add to console startup commands")
    val consoleImports = settingKey[Seq[String]]("Imports to add to console startup commands")

    object consoleImport {
      def apply(imports: String*) = (consoleImports ++= imports)
    }

    object consolePkg {
      def apply(pkgs: String*) = (consolePkgs ++= pkgs)
    }
  }

  import autoImport._

  override def globalSettings =
    Seq(
      // Build for Scala 2.11 by default
      scalaVersion := scala212Version.value,

      // Only build for Scala 2.11, by default
      crossScalaVersions :=
        Seq(
          scala211Version.value,
          scala212Version.value
        ),

      scala210Version  := "2.10.7",
      scala211Version  := "2.11.12",
      scala212Version  := "2.12.4",

      versions ++= Seq(
        scala_lang    → scalaVersion.value,
        scala_reflect → scalaVersion.value
      )
    )

  override def projectSettings =
    Seq(
      isScala210 := (scalaBinaryVersion.value == "2.10"),
      isScala211 := (scalaBinaryVersion.value == "2.11"),
      isScala212 := (scalaBinaryVersion.value == "2.12"),

      scalacOptions ++= Seq(
        "-feature",
        "-language:existentials",
        "-language:implicitConversions",
        "-language:postfixOps",
        "-language:higherKinds",
        "-language:reflectiveCalls"
      ),

      consolePkgs := Nil,
      initialCommands += consolePkgs.value.map(pkg ⇒ s"import $pkg._").mkString("", "\n", "\n"),

      consoleImports := Nil,
      initialCommands += consoleImports.value.map(i ⇒ s"import $i").mkString("", "\n", "\n")
    )
}
