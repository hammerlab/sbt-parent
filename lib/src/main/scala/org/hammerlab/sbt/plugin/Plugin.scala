package org.hammerlab.sbt.plugin

import sbt._

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

  type Lazy[T] = () ⇒ T
  implicit def makeLazy[T](t: ⇒ T): Lazy[T] = () ⇒ t

  private val  _globals = ArrayBuffer[Lazy[SettingsDefinition]]()
  private val _projects = ArrayBuffer[Lazy[SettingsDefinition]]()

  def  globals(settings: Lazy[SettingsDefinition]*) = {  _globals ++= settings; this }
  def projects(settings: Lazy[SettingsDefinition]*) = { _projects ++= settings; this }

  override def  globalSettings = super. globalSettings ++  _globals.flatMap { _() }
  override def projectSettings = super.projectSettings ++ _projects.flatMap { _() }
}
