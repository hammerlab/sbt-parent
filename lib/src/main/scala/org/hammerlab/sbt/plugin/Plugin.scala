package org.hammerlab.sbt.plugin

import sbt.{ Def, _ }

import scala.collection.mutable.ArrayBuffer

abstract class Plugin(deps: AutoPlugin*)
  extends AutoPlugin {
  override def trigger: PluginTrigger = allRequirements

  override def requires: Plugins =
    deps
      .foldLeft(
        super.requires
      )(
        _ && _
      )

  val  globals = ArrayBuffer[SettingsDefinition]()
  val projects = ArrayBuffer[SettingsDefinition]()

  override def  globalSettings = super. globalSettings ++  globals.flatten
  override def projectSettings = super.projectSettings ++ projects.flatten
}
