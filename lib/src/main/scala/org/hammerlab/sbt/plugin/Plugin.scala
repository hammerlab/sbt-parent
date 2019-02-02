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

  override def  globalSettings: Seq[Def.Setting[_]] = {
    println(s"adding global settings for $getClass")
    super. globalSettings ++ globals.flatten
  }
  override def projectSettings: Seq[Def.Setting[_]] = {
    println(s"adding project settings for $getClass")
    super.projectSettings ++ projects.flatten
  }
}
