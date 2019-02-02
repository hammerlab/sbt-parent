package org.hammerlab.sbt.plugin

import org.hammerlab.sbt.{ ContainerPlugin, Lib }
import org.hammerlab.sbt.deps.Group._
import org.hammerlab.sbt.plugin.Deps.testDeps
import org.hammerlab.sbt.plugin.Versions.autoImport.fixed
import org.hammerlab.sbt.plugin.Versions.noopSettings
import sbt.Keys._
import sbt.TestFrameworks.ScalaTest
import sbt._

object Test
  extends ContainerPlugin(
    Deps,
    Versions
  )
{

  object autoImport {
    val publishTestJar =
        publishArtifact in sbt.Test := publishArtifact.value

    val test_? = TaskKey[Boolean]("test_q", "Set to false to disable tests")
    val disableTests = test_? := false

    trait framework {
      self: Lib ⇒
      def framework: TestFramework
      def extra: Seq[Setting[_]] = Seq()
      def _settings(add: Boolean): SettingsDefinition =
        Seq(
          Deps.dep(dep),
          if (add)
            testFrameworks += framework
          else
            testFrameworks := Seq(framework)
        )

      override val settings = _settings(add = false)
      val add = _settings(add = true)
    }

    object scalatest
      extends Lib("org.scalatest" ^^ "scalatest" ^ "3.0.5" tests)
        with framework {
      val framework = ScalaTest
      override val extra = Seq(
        // Output full stack-traces
        testOptions in sbt.Test += Tests.Argument(ScalaTest, "-oF"),
      )
    }

    object utest
      extends Lib("com.lihaoyi" ^^ "utest" ^ "0.6.6" tests)
        with framework {
      val framework = new TestFramework("utest.runner.Framework")
    }

    // "Hidden" test-resources (resources whose basenames start with ".") are not moved into target/ dirs (and therefore
    // not present on tests' classpath) by default; this setting overrides that behavior to include them
    val includeHiddenTestResources = excludeFilter in sbt.Test := NothingFilter
  }

  import autoImport._

  noopSettings += disableTests

  globals +=
    //super.globalSettings ++
    Seq(

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
    )

  projects +=
    //super.projectSettings ++
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
