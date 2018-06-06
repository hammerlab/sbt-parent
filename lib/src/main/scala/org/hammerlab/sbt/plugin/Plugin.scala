package org.hammerlab.sbt.plugin

import sbt.{ AutoPlugin, PluginTrigger, Plugins }

class Plugin(deps: AutoPlugin*)
  extends AutoPlugin {
  override def trigger: PluginTrigger = allRequirements

  override def requires: Plugins =
    deps
      .foldLeft(
        super.requires
      )(
        _ && _
      )
}
