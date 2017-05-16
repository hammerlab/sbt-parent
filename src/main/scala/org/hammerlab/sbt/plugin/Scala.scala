package org.hammerlab.sbt.plugin

import sbt.Keys._
import sbt._

object Scala
  extends Plugin {

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

    val addScala212 = (crossScalaVersions += scala212Version.value)
    val omitScala210 = (crossScalaVersions -= scala210Version.value)

    val scala210Only =
      Seq(
        scalaVersion := scala210Version.value,
        crossScalaVersions := Seq(scala210Version.value)
      )

    val scala211Only =
      Seq(
        scalaVersion := scala211Version.value,
        crossScalaVersions := Seq(scala211Version.value)
      )

    val scala212Only =
      Seq(
        scalaVersion := scala212Version.value,
        crossScalaVersions := Seq(scala212Version.value)
      )

    val enableMacroParadise =
      addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.0" cross CrossVersion.full)
  }

  import autoImport._

  override def projectSettings: Seq[Def.Setting[_]] =
    Seq(
      // Build for Scala 2.11 by default
      scalaVersion := scala211Version.value,

      // Only build for Scala 2.11, by default
      crossScalaVersions := Seq(scala211Version.value),

      scala210Version := "2.10.6",
      scala211Version := "2.11.8",
      scala212Version := "2.12.1",

      isScala210 := (scalaBinaryVersion.value == "2.10"),
      isScala211 := (scalaBinaryVersion.value == "2.11"),
      isScala212 := (scalaBinaryVersion.value == "2.12")
    )
}
