package org.hammerlab.sbt.plugin

import sbt.Keys._
import sbt._
import xerial.sbt.Sonatype
import xerial.sbt.Sonatype.SonatypeKeys.sonatypeProfileName

object Maven
  extends Plugin(Sonatype) {

  object autoImport {
    val githubUser = settingKey[String]("Github user/org to point to")
  }

  import autoImport._

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
      pomIncludeRepository := { _ => false },

      pomExtra := {
        val n = name.value
        val user = githubUser.value
        <url>
          https://github.com/{user}/{n}
        </url>
          <licenses>
            <license>
              <name>Apache License</name>
              <url>https://raw.github.com/{user}/{n}/master/LICENSE</url>
              <distribution>repo</distribution>
            </license>
          </licenses>
          <scm>
            <url>git@github.com:{user}/{n}.git</url>
            <connection>scm:git:git@github.com:{user}/{n}.git</connection>
            <developerConnection>scm:git:git@github.com:{user}/{n}.git</developerConnection>
          </scm>
          <developers>
            <developer>
              <id>hammerlab</id>
              <name>Hammer Lab</name>
              <url>https://github.com/{user}</url>
            </developer>
          </developers>
      },

      organization := "org.hammerlab",
      githubUser := "hammerlab",

      // All org.hammerlab* repos are published with this Sonatype profile.
      sonatypeProfileName := (
        if (organization.value.startsWith("org.hammerlab"))
          "org.hammerlab"
        else
          sonatypeProfileName.value
        ),

      resolvers += Resolver.sonatypeRepo("releases"),
      resolvers += Resolver.sonatypeRepo("snapshots")
    )
}
