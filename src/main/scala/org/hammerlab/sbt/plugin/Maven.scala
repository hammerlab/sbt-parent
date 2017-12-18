package org.hammerlab.sbt.plugin

import sbt.Keys._
import sbt._
import Resolver.{sonatypeRepo, mavenLocal}
import xerial.sbt.Sonatype

object Maven
  extends Plugin(Sonatype) {

  object autoImport {
    val githubUser = settingKey[String]("Github user/org to point to")
    val githubName = settingKey[String]("Github repository basename")

    val apache2License = ("Apache 2.0", new URL("https://www.apache.org/licenses/LICENSE-2.0"))
    val apache2 = licenses += apache2License
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
      },

      resolvers ++= Seq(
        sonatypeRepo("releases"),
        sonatypeRepo("snapshots"),
        mavenLocal
      )
    )
}
