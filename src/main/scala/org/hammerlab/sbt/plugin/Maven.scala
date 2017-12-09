package org.hammerlab.sbt.plugin

import sbt.Keys._
import sbt._
import xerial.sbt.Sonatype

object Maven
  extends Plugin(Sonatype) {

  case class Developer(id: String, name: String, url: String)
  object Developer {
    implicit def fromTuple(t: (String, String, String)): Developer = Developer(t._1, t._2, t._3)
  }

  object autoImport {
    val githubUser = settingKey[String]("Github user/org to point to")
    val githubName = settingKey[String]("Github repository basename")
    val developers = settingKey[Seq[Developer]]("Entries for the <developers> POM tag")
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

      githubName := name.value,

      pomExtra := {
        val user = githubUser.value
        val name = githubName.value
        <url>
          https://github.com/{user}/{name}
        </url>
          <licenses>
            <license>
              <name>Apache License</name>
              <url>https://raw.github.com/{user}/{name}/master/LICENSE</url>
              <distribution>repo</distribution>
            </license>
          </licenses>
          <scm>
            <url>git@github.com:{user}/{name}.git</url>
            <connection>scm:git:git@github.com:{user}/{name}.git</connection>
            <developerConnection>scm:git:git@github.com:{user}/{name}.git</developerConnection>
          </scm>
          <developers>
            {
              developers.value.map {
                case Developer(id, name, url) =>
                  <developer>
                    <id>{id}</id>
                    <name>{name}</name>
                    <url>{url}</url>
                  </developer>
              }
            }
          </developers>
      },

      resolvers += Resolver.sonatypeRepo("releases"),
      resolvers += Resolver.sonatypeRepo("snapshots")
    )
}
