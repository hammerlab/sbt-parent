import sbt._
import sbt.Keys._
import sbt.PluginTrigger.AllRequirements
import ScriptedPlugin.autoImport._

object Scripted extends AutoPlugin {
  override def trigger = AllRequirements
  override def requires = super.requires && ScriptedPlugin
  override def projectSettings: Seq[Def.Setting[_]] =
    Seq(
      scriptedLaunchOpts := { scriptedLaunchOpts.value ++
        Seq("-Xmx1024M", "-Dplugin.version=" + version.value)
      },
      scriptedBufferLog := false
    )
}
