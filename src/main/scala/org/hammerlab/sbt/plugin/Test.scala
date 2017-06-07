package org.hammerlab.sbt.plugin

import org.hammerlab.sbt.plugin.Deps.autoImport.testDeps
import sbt.Keys.{ publishArtifact, testFrameworks, testOptions }
import sbt.TestFrameworks.ScalaTest
import sbt.{ Def, TestFrameworks, Tests, settingKey, _ }

object Test
  extends Plugin(Deps) {

  object autoImport {
    val scalatest = settingKey[ModuleID]("Scalatest dependency")
    val testUtils = settingKey[ModuleID]("org.hammerlab:test-utils dependency")
    val scalatestVersion = settingKey[String]("Version of scalatest test-dep to use")
    val testUtilsVersion = settingKey[String]("Version of org.hammerlab:test_utils test-dep to use")

    val publishTestJar = (publishArtifact in sbt.Test := true)
  }

  import autoImport._

  override def projectSettings: Seq[Def.Setting[_]] =
    Seq(
      scalatestVersion := "3.0.0",
      testUtilsVersion := "1.2.3-SNAPSHOT",

      scalatest := "org.scalatest" %% "scalatest" % scalatestVersion.value,
      testUtils := "org.hammerlab" %% "test-utils" % testUtilsVersion.value,

      testOptions in sbt.Test += Tests.Argument(TestFrameworks.ScalaTest, "-oF"),

      // Only use ScalaTest by default; without this, other frameworks get instantiated and can inadvertently mangle
      // test-command-lines/args/classpaths.
      testFrameworks := Seq(ScalaTest),

      // Add hammerlab:test-utils and scalatest as test-deps by default.
      testDeps := Seq(
        testUtils.value,
        scalatest.value
      )
    )
}
