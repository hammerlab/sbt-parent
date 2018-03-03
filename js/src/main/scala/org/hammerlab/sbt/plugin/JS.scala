package org.hammerlab.sbt.plugin

import org.hammerlab.sbt.deps.Group._
import org.scalajs.sbtplugin.ScalaJSPlugin
import org.scalajs.sbtplugin.ScalaJSPlugin.autoImport._
import sbt._
import Deps.autoImport.deps
import org.hammerlab.sbt.plugin.Versions.autoImport.defaultVersions
import org.scalajs.sbtplugin.cross.{ CrossClasspathDependency, CrossProject }
import sbt.Keys.libraryDependencies

import scalajsbundler.sbtplugin.ScalaJSBundlerPlugin
import ScalaJSBundlerPlugin.autoImport.npmDependencies

object JS
  extends Plugin(
    Deps,
    ScalaJSPlugin,
    ScalaJSBundlerPlugin,
    Versions
  ) {

  override def trigger = noTrigger

  object autoImport {
    val scalacss = "com.github.japgolly.scalacss" ^^ "core"
    object scalajs {

      val       dom = "org.scala-js" ^^ "scalajs-dom"
      val react_dep = "com.github.japgolly.scalajs-react" ^^ "core"

      val react: Seq[Setting[_]] =
        Seq(
          deps ++= Seq(
            scalacss,
            scalajs.react_dep
          ),
          npmDependencies in Compile ++= Seq(
            "react"     → "15.6.1",
            "react-dom" → "15.6.1"
          )
        ) ++ scalajs

      val stubs = libraryDependencies += "org.scala-js" %% "scalajs-stubs" % scalaJSVersion % "provided"
    }

    implicit def makeScalaJSProject(s: scalajs.type): Seq[Setting[_]] =
      Seq(
        deps += scalajs.dom,
        scalaJSUseMainModuleInitializer := true
      )

    implicit class CrossProjectConfigOps(val p: CrossProject) extends AnyVal {
      def  andTest: CrossClasspathDependency = new CrossClasspathDependency(p, Some("compile->compile;test->test"))
      def testtest: CrossClasspathDependency = new CrossClasspathDependency(p, Some("test->test"))
      def     test: CrossClasspathDependency = new CrossClasspathDependency(p, Some("compile->test"))
    }
  }

  import autoImport._

  override def projectSettings: Seq[Def.Setting[_]] =
    Seq(
      defaultVersions ++= Seq(
        scalacss              → "0.5.3",
        scalajs.dom           → "0.9.2",
        scalajs.react_dep     → "1.1.1"
      )
    )
}
