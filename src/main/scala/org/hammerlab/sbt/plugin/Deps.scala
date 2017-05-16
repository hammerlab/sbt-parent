package org.hammerlab.sbt.plugin

import sbt.Keys._
import sbt._

object Deps
  extends Plugin {

  object autoImport {
    val providedDeps = settingKey[Seq[ModuleID]]("Dependencies to be scoped 'provided'")

    val deps = settingKey[Seq[ModuleID]]("Short-hand for libraryDependencies")
    val testDeps = settingKey[Seq[ModuleID]]("Dependencies to be scoped 'test'")
    val testJarTestDeps = settingKey[Seq[ModuleID]]("Modules whose \"tests\"-qualified artifacts should be test-dependencies")

    val compileAndTestDeps = settingKey[Seq[ModuleID]]("Dependencies to be added as compile-scoped-compile-deps as well as test-scoped-test-deps")
  }

  import autoImport._

  override def projectSettings: Seq[Def.Setting[_]] =
    Seq(
      // Implement "deps" as a short-hand for libraryDependencies to add
      deps := Nil,
      libraryDependencies ++= deps.value,

      testDeps := Seq(),

      testJarTestDeps := Seq(),

      compileAndTestDeps := Seq(),

      // Add any other `testDeps` as test-scoped dependencies.
      libraryDependencies ++= testDeps.value.map(_ % "test"),
      libraryDependencies ++= testJarTestDeps.value.map(_ % "test" classifier("tests")),

      libraryDependencies ++= compileAndTestDeps.value,
      testJarTestDeps ++= compileAndTestDeps.value,

      providedDeps := Nil,

      libraryDependencies ++= providedDeps.value.map(_ % "provided")
    )
}
