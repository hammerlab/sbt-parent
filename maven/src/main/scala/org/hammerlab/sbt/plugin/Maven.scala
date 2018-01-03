package org.hammerlab.sbt.plugin

import sbt.Keys._
import sbt._
import Resolver.{ mavenLocal, sonatypeRepo }
import xerial.sbt.Sonatype

object Maven
  extends AutoPlugin {
  override def trigger = allRequirements
  override def requires = Sonatype
  override def projectSettings: Seq[Def.Setting[_]] =
    Seq(
      publishTo := {
        val nexus = "https://oss.sonatype.org/"
        if (isSnapshot.value)
          Some("snapshots" at nexus + "content/repositories/snapshots")
        else
          Some("releases" at nexus + "service/local/staging/deploy/maven2")
      },

      publishMavenStyle := true,
      publishArtifact in sbt.Test := false,
      pomIncludeRepository := { _ ⇒ false },

      resolvers ++= Seq(
        sonatypeRepo("releases"),
        sonatypeRepo("snapshots"),
        mavenLocal
      )
    )
}
