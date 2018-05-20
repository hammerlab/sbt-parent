package org.hammerlab.sbt.plugin

import org.hammerlab.sbt.plugin.Deps.DepArg
import org.hammerlab.sbt.plugin.Deps.autoImport._
import org.hammerlab.sbt.plugin.JS.autoImport.scalajs.{ css, diode, react }
import org.hammerlab.sbt.plugin.Versions.autoImport._
import org.scalajs.sbtplugin.ScalaJSPlugin
import org.scalajs.sbtplugin.ScalaJSPlugin.autoImport._
import org.scalajs.sbtplugin.cross._
import sbt.Keys._
import sbt._

import scalajsbundler.sbtplugin.ScalaJSBundlerPlugin.autoImport.npmDependencies

object JS
  extends Plugin(
    Deps,
    ScalaJSPlugin,
    Versions
  ) {

  override def trigger = noTrigger

  object autoImport {
    val scalacss = css.core
    implicit def cssSetting(c: css.type): DepArg = css.core
    implicit def reactDep(r: react.type): DepArg = react.core
    implicit def diodeDepArg(d: diode.type): DepArg = d.core
    implicit def reactSettings(r: react.type): Seq[Setting[_]] =
      Seq(
        dep(
          css,
          react
        ),
        react.npm
      ) ++
        scalajs

    object scalajs {

      object css {
        val version = SettingKey[String]("scalaCSSVersion", "Version of scalacss")
        val group = "com.github.japgolly.scalacss"
        val  core = group ^^ "core"
        val react = group ^^ "ext-react"
        val defaults =
          Seq(
            version := "0.5.3",
            versions ++= Seq(
               core → version.value,
              react → version.value
            )
          )
      }

      val dom = "org.scala-js" ^^ "scalajs-dom"

      object react {
        val group = "com.github.japgolly.scalajs-react"
        val  core = group ^^  "core"
        val extra = group ^^ "extra"
        val version = SettingKey[String]("reactVersion", "Version of scalajs-react")
        val jsVersion = SettingKey[String]("reactJSVersion", "Version of react JS libraries")
        val webjars =
          jsDependencies ++= Seq(
            "org.webjars.bower" % "react" % jsVersion.value / "react-with-addons.js" minified "react-with-addons.min.js" commonJSName "React",
            "org.webjars.bower" % "react" % jsVersion.value / "react-dom.js" minified "react-dom.min.js" dependsOn "react-with-addons.js" commonJSName "ReactDOM"
          )

        val defaults =
          Seq(
            version := "1.2.0",
            jsVersion := "16.2.0",
            versions ++= Seq(
               core → version.value,
              extra → version.value
            )
          )
        val npm =
          npmDependencies in Compile ++= Seq(
            "react"     → jsVersion.value,
            "react-dom" → jsVersion.value
          )
      }

      object diode {
        val version = SettingKey[String]("diodeVersion", "Version of diode")
        val core = "io.suzaku" ^^ "diode"
        val react = "io.suzaku" ^^ "diode-react"
        val defaults =
          Seq(
            version := "1.1.2",
            versions ++=
              Seq(
                 core → version.value,
                react → version.value
              )
          )
      }

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
      versions(
        scalajs.dom → "0.9.3"
      )
    ) ++
    scalajs.react.defaults ++
    scalajs.diode.defaults ++
    scalajs.css.defaults
}
