package org.hammerlab.sbt.plugin

import org.hammerlab.sbt.deps.VersionOps._
import sbt.Keys._
import sbt._
import Resolver.{ mavenLocal, sonatypeRepo }
import xerial.sbt.Sonatype

object Maven
  extends AutoPlugin {
  override def trigger = allRequirements
  override def requires = Sonatype
  object autoImport {
    val mavenLocal = TaskKey[Unit]("maven-local", "Wrapper for publishM2 which skips non-SNAPSHOT modules")
  }
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
      ),

      autoImport.mavenLocal := Def.taskDyn[Unit] {
        if (version.value.isSnapshot) {
          streams.value.log.info(s"publishing: ${version.value}")
          publishM2
        } else {
          streams.value.log.info(s"skipping publishing: ${version.value}")
          Def.task {}
        }
      }
      .value
    )
}
