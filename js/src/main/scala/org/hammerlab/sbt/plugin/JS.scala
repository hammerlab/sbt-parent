package org.hammerlab.sbt.plugin

import org.hammerlab.sbt.Libs.replace
import org.hammerlab.sbt.plugin.Deps.autoImport._
import org.hammerlab.sbt.plugin.JS.autoImport.scalajs.css
import org.hammerlab.sbt.{ ContainerPlugin, Lib, Libs }
import org.scalajs.sbtplugin.ScalaJSPlugin
import org.scalajs.sbtplugin.ScalaJSPlugin.autoImport._
import sbt._
import sbtcrossproject.{ CrossClasspathDependency, CrossProject }
import scalajsbundler.sbtplugin.ScalaJSBundlerPlugin.autoImport.npmDependencies
import scalajscrossproject.ScalaJSCrossPlugin

object JS
  extends ContainerPlugin(
    Deps,
    ScalaJSPlugin,
    ScalaJSCrossPlugin,
    Versions
  )
{

  override def trigger = noTrigger

  object autoImport {
    val scalacss = css.core

    object scalajs {

      object css
        extends Libs(
          "com.github.japgolly.scalacss" ^^ "core" ^ "0.5.3",
          replace
        ) {
        val  core = lib
        val react = lib("ext-react")
      }

      val dom = Lib("org.scala-js" ^^ "scalajs-dom" ^ "0.9.6")

      object react
        extends
          Libs(
            "com.github.japgolly.scalajs-react" ^^ "core" ^ "1.3.1",
            replace
          ) {
        val  core = lib
        val extra = lib
        val jsVersion = SettingKey[String]("reactJSVersion", "Version of react JS libraries")
        val webjars =
          jsDependencies ++= Seq(
            "org.webjars.npm" % "react" % react.jsVersion.value
              /        "umd/react.development.js"
              minified "umd/react.production.min.js"
              commonJSName "React",

            "org.webjars.npm" % "react-dom" % react.jsVersion.value
              /         "umd/react-dom.development.js"
              minified  "umd/react-dom.production.min.js"
              dependsOn "umd/react.development.js"
              commonJSName "ReactDOM",

            "org.webjars.npm" % "react-dom" % react.jsVersion.value
              /         "umd/react-dom-server.browser.development.js"
              minified  "umd/react-dom-server.browser.production.min.js"
              dependsOn "umd/react-dom.development.js"
              commonJSName "ReactDOMServer"
          )

        override val global: SettingsDefinition =
          super.global ++
          Seq(
            jsVersion := "16.5.1",
          )

        override def settings: SettingsDefinition =
          Seq(
            Deps.dep(
              css,
              react
            ),
            react.npm
          ) ++
          scalajs

        val npm =
          npmDependencies in Compile ++= Seq(
            "react"     → jsVersion.value,
            "react-dom" → jsVersion.value
          )
      }

      object diode extends Libs("io.suzaku" ^^ "diode" ^ "1.1.4") {
        val  core = lib
        val react = lib ^ "1.1.4.131"
      }

      val stubs = Lib("org.scala-js" ^^ "scalajs-stubs" ^ scalaJSVersion ^ "provided")

      val time = Lib("io.github.cquiroz" ^^ "scala-java-time" ^ "2.0.0-M13")
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
      def forTests: CrossClasspathDependency = new CrossClasspathDependency(p, Some("test->compile"))
    }
  }
}
