package org.hammerlab.sbt

import sbt._
import Keys._

object ParentPlugin extends AutoPlugin {

  override def trigger: PluginTrigger = allRequirements

  override def projectSettings: Seq[_root_.sbt.Def.Setting[_]] = Seq(
    organization := "org.hammerlab",
    parallelExecution in Test := false,
    publishTo := {
      val nexus = "https://oss.sonatype.org/"
      if (isSnapshot.value)
        Some("snapshots" at nexus + "content/repositories/snapshots")
      else
        Some("releases" at nexus + "service/local/staging/deploy/maven2")
    },

    publishMavenStyle := true,
    publishArtifact in Test := false,
    pomIncludeRepository := { _ => false },

    pomExtra := {
      val n = name.value
      <url>
        https://github.com/hammerlab/{n}
      </url>
        <licenses>
          <license>
            <name>Apache License</name>
            <url>https://raw.github.com/hammerlab/{n}/master/LICENSE</url>
            <distribution>repo</distribution>
          </license>
        </licenses>
        <scm>
          <url>git@github.com:hammerlab/{n}.git</url>
          <connection>scm:git:git@github.com:hammerlab/{n}.git</connection>
          <developerConnection>scm:git:git@github.com:hammerlab/{n}.git</developerConnection>
        </scm>
        <developers>
          <developer>
            <id>hammerlab</id>
            <name>Hammer Lab</name>
            <url>https://github.com/hammerlab</url>
          </developer>
        </developers>
    },

    crossScalaVersions := Seq("2.10.6", "2.11.8"),

    scalaVersion := "2.11.8"
  )
}
