package org.hammerlab.sbt.plugin

import org.hammerlab.sbt.deps.Dep
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
    val publishTestJar = publishArtifact in sbt.Test := publishArtifact.value

    val test_? = SettingKey[Boolean]("test_q", "Set to false to disable tests")
    val disableTests = test_? := false

    object tests {
      val framework = SettingKey[Option[autoImport.framework]]("test-framework", "Framework to use for testing")
      val disable = disableTests
      val enabled = test_?
      // "Hidden" test-resources (resources whose basenames start with ".") are not moved into target/ dirs (and
      // therefore not present on tests' classpath) by default; this setting overrides that behavior to include them
      val hiddenResources = excludeFilter in sbt.Test := NothingFilter
    }

    trait framework {
      self: Lib ⇒
      def dep: Dep
      def framework: TestFramework
      def options: Seq[TestOption] = Nil
      def add: SettingsDefinition =
        Seq(
          testDeps += dep,
          testFrameworks += framework
        )

      override def settings = tests.framework := Some(this)
    }

    object scalatest
      extends Lib("org.scalatest" ^^ "scalatest" ^ "3.0.5" tests)
        with framework {
      val framework = ScalaTest
      override val options = Seq(
        // Output full stack-traces
        Tests.Argument(ScalaTest, "-oF")
      )
    }

    object utest
      extends Lib("com.lihaoyi" ^^ "utest" ^ "0.6.6" tests)
        with framework {
      val framework = new TestFramework("utest.runner.Framework")
    }
  }

  import autoImport._

  noopSettings += disableTests

  // Use scalatest by default
  globals(scalatest.settings)

  // Force initialization/registration of scalatest. It is registered to be lazily add global settings by default (see
  // previous statement), but if this plugin's projectSettings are accessed before globalSettings, then this plugin's
  // Settings/Container machinery will be marekd as "finalized" before the scalatest object is ever
  // initialized/registered.
  // This should only be necessary in cases like this (where a plugin wants to register an object by default that may
  // not be constructed before the plugin has begun being accessed), and not in user projects/code.
  scalatest !

  globals(
    test_? := (
      if (fixed.value)
        false
      else
        true
    )
  )

  projects(
    testFrameworks :=
      tests
        .framework
        .value
        .map { _.framework }
        .toList,

    testDeps ++=
      tests
        .framework
        .value
        .map { _.dep }
        .fold {
          List[Dep]()
        } {
          List(_)
        },

    testOptions in sbt.Test ++=
      tests
        .framework
        .value
        .map { _.options }
        .getOrElse { Nil },

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
