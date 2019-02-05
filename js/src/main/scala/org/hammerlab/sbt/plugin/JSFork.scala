package org.hammerlab.sbt.plugin

import org.scalajs.sbtplugin.ScalaJSPlugin.AutoImport.isScalaJSProject
import sbt.Keys.fork
import sbt.plugins.JvmPlugin
import sbt.settingKey

/**
 * Set [[sbt.Keys.fork]] to `true` by default, but work-around a ScalaJS requirement that it always be set to `false`
 *
 * This plugin always triggers, and does not depend on [[org.scalajs.sbtplugin.ScalaJSPlugin]], but references its
 * [[org.scalajs.sbtplugin.ScalaJSPlugin.AutoImport.isScalaJSProject]] settings, which seems generally dicey but seems
 * to work!
 */
object JSFork
  extends Plugin(
    JvmPlugin
  ) {
  object autoImport {
    val forkJVM = settingKey[Boolean]("Wrapper for `fork`, which scala-js requires to always be set to `false`")
  }
  import autoImport._
  globals(
    forkJVM := true
  )

  projects(
    /*
     * [[fork]] must be [[false]] in ScalaJS projects, even though they always fork!
     * cf. https://github.com/scala-js/scala-js/issues/1590#issuecomment-370243830
     */
    fork := (
      if (isScalaJSProject.value)
        false
      else
        forkJVM.value
      )
  )
}
