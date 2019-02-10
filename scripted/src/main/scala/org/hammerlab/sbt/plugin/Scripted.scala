package org.hammerlab.sbt.plugin

import com.github.daniel.shuy.sbt.scripted.scalatest.SbtScriptedScalaTest.autoImport.scriptedScalaTestSpec
import com.github.daniel.shuy.sbt.scripted.scalatest.{ SbtScriptedScalaTest, ScriptedScalaTestSuiteMixin }
import org.scalatest.{ FunSuite, Matchers, Suite }
import sbt.Keys._
import sbt._

object Scripted
extends Plugin(
  SbtScriptedScalaTest
) {
  object autoImport {
    lazy val spec: TaskKey[Suite with ScriptedScalaTestSuiteMixin] = TaskKey("scripted-scalatest-spec-wrapper", "The ScalaTest Spec.")
    abstract class ScriptedSuite(val sbtState: State)
      extends FunSuite
         with Matchers
         with ScriptedScalaTestSuiteMixin
  }
  import autoImport._
  override def globalSettings =
    super.globalSettings ++ Seq(
      spec := null,
      scriptedScalaTestSpec := Option(spec.value)
    )

  override def projectSettings =
    super.projectSettings ++ Seq(
      libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.5"
    )
}
