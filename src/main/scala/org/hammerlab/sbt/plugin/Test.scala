package org.hammerlab.sbt.plugin

import org.hammerlab.sbt.deps.Group._
import org.hammerlab.sbt.plugin.Deps.autoImport.testDeps
import org.hammerlab.sbt.plugin.Versions.autoImport.versions
import sbt.Keys.{ publishArtifact, testFrameworks, testOptions }
import sbt.TestFrameworks.ScalaTest
import sbt.{ Def, Tests, settingKey }

object Test
  extends Plugin(Deps) {

  object autoImport {
    val scalatestVersion = settingKey[String]("Version of scalatest test-dep to use")

    val publishTestJar = (publishArtifact in sbt.Test := true)

    val scalatest = "org.scalatest" ^^ "scalatest"
  }

  import autoImport._

  override def projectSettings: Seq[Def.Setting[_]] =
    Seq(
      versions += scalatest → scalatestVersion.value,

      scalatestVersion := "3.0.0",

      testOptions in sbt.Test += Tests.Argument(ScalaTest, "-oF"),

      // Only use ScalaTest by default; without this, other frameworks get instantiated and can inadvertently mangle
      // test-command-lines/args/classpaths.
      testFrameworks := Seq(ScalaTest),

      // Add hammerlab:test-utils and scalatest as test-deps by default.
      testDeps := Seq(
        scalatest
      )
    )
}
