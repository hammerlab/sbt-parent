package org.hammerlab.sbt.plugin

import org.hammerlab.sbt.Lib
import org.hammerlab.sbt.deps.Group._
import org.hammerlab.sbt.plugin.Deps.autoImport.testDeps
import org.hammerlab.sbt.plugin.Versions.autoImport.fixed
import org.hammerlab.sbt.plugin.Versions.noopSettings
import sbt.Keys._
import sbt.TestFrameworks.ScalaTest
import sbt._

object Test
  extends Plugin(
    Deps,
    Versions
  ) {

  object autoImport {
    val publishTestJar =
        publishArtifact in sbt.Test := publishArtifact.value

    val test_? = TaskKey[Boolean]("test_q", "Set to false to disable tests")
    val disableTests = test_? := false

    object scalatest extends Lib("org.scalatest" ^^ "scalatest" ^ "3.0.4")

    // "Hidden" test-resources (resources whose basenames start with ".") are not moved into target/ dirs (and therefore
    // not present on tests' classpath) by default; this setting overrides that behavior to include them
    val includeHiddenTestResources = excludeFilter in sbt.Test := NothingFilter
  }

  import autoImport._

  noopSettings += disableTests

  override def globalSettings =
    Seq(
      // Output full stack-traces
      testOptions in sbt.Test += Tests.Argument(ScalaTest, "-oF"),

      // Use only ScalaTest by default; without this, other frameworks get instantiated and can inadvertently mangle
      // test-command-lines/args/classpaths.
      testFrameworks := Seq(ScalaTest),

      // Add scalatest as a test-dep by default.
      testDeps += scalatest,
      test_? := (
        if (fixed.value)
          false
        else
          true
      )
    ) ++
    scalatest.global

  override def projectSettings =
    scalatest.project ++
    Seq(
      test in sbt.Test :=
        Def.taskDyn[Unit] {
          val default = (test in sbt.Test).taskValue
          if (test_?.value)
            Def.task(default.value)
          else
            Def.task(())
        }
        .value
    )
}
