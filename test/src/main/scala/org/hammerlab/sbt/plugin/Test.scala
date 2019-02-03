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

    object testing {
      val framework = SettingKey[Option[autoImport.framework]]("test-framework", "Framework to use for testing")
      val disable = disableTests
      val enabled = test_?
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

      override def settings = {
        println(s"Trying to add scalatest: $this")
        testing.framework := Some(this)
      }
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

    // "Hidden" test-resources (resources whose basenames start with ".") are not moved into target/ dirs (and therefore
    // not present on tests' classpath) by default; this setting overrides that behavior to include them
    val includeHiddenTestResources = excludeFilter in sbt.Test := NothingFilter
  }

  import autoImport._

  noopSettings += disableTests

  globals +=
    (
      test_? := (
        if (fixed.value)
          false
        else
          true
      )
    )

  println("Test… scalatest/autoImport:")
  println(scalatest)
//  println(autoImport)
  println("printed")
//  globals += scalatest

  projects ++=
    Seq(
      testFrameworks := testing.framework.value.map(_.framework).toList,
      testDeps ++= testing.framework.value.map(_.dep).toList,
      testOptions in sbt.Test ++= testing.framework.value.toList.flatMap(_.options),
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

  // Use only ScalaTest by default; without this, other frameworks get instantiated and can inadvertently mangle
  // test-command-lines/args/classpaths.
  // This needs to be added "lazily" into globalSettings (as opposed to eagerly into globals) because scalatest.settings
  // isn't initialized yet in [[framework]] while the scalatest object is still being constructed
  override def globalSettings = super.globalSettings ++ scalatest.settings
//  {
//    val spr = super.globalSettings ++ scalatest.settings
//    println(s"Test globalSettings: ${spr.mkString(",")}")
//    spr
//  }

//  override def projectSettings: Seq[Def.Setting[_]] = {
//    val spr = super.projectSettings
//    println(s"Test projectSettings: ${spr.mkString(",")}")
//    spr
//  }

}
