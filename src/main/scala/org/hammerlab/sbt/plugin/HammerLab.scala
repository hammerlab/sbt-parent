package org.hammerlab.sbt.plugin

import org.hammerlab.sbt.plugin.Maven.autoImport._
import sbt.Def
import sbt.Keys.organization
import xerial.sbt.Sonatype.SonatypeKeys.sonatypeProfileName

object HammerLab
  extends Plugin(Maven) {

  override def projectSettings: Seq[Def.Setting[_]] =
    Seq(
      organization := "org.hammerlab",
      githubUser := "hammerlab",
      // All org.hammerlab* repos are published with this Sonatype profile.
      sonatypeProfileName := (
        if (organization.value.startsWith("org.hammerlab"))
          "org.hammerlab"
        else
          sonatypeProfileName.value
        ),
      developers += ("HammerLab", "Hammer Lab", "https://github.com/hammerlab")
    )
}
