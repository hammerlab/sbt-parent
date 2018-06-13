package org.hammerlab.sbt.plugin

import org.hammerlab.sbt.deps.Group._
import org.hammerlab.sbt.dsl.Lib
import org.hammerlab.sbt.plugin.Deps.autoImport.testDeps
import sbt.Keys._
import sbt.TestFrameworks.ScalaTest
import sbt._

object Test
  extends Plugin(Deps) {

  object autoImport {
    val publishTestJar =
        publishArtifact in sbt.Test := publishArtifact.value

    object scalatest extends Lib("org.scalatest" ^^ "scalatest" ^ "3.0.4")
  }

  import autoImport._

  override def globalSettings =
    Seq(
      // Output full stack-traces
      testOptions in sbt.Test += Tests.Argument(ScalaTest, "-oF"),

      // Use only ScalaTest by default; without this, other frameworks get instantiated and can inadvertently mangle
      // test-command-lines/args/classpaths.
      testFrameworks := Seq(ScalaTest),

      // Add scalatest as a test-dep by default.
      testDeps += scalatest
    ) ++
    scalatest.global

  override def projectSettings =
    scalatest.project
}
