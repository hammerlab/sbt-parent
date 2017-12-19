package org.hammerlab.sbt.plugin

import sbt.Keys._
import sbt._
import Resolver.{sonatypeRepo, mavenLocal}
import xerial.sbt.Sonatype

object Maven
  extends Plugin(Sonatype) {

  object autoImport {
    object github {
      val user = settingKey[String]("Github user/org to point to")
      val name = settingKey[String]("Github repository basename")
    }

    val apache2License = ("Apache 2.0", new URL("https://www.apache.org/licenses/LICENSE-2.0"))
    val apache2 = licenses += apache2License

    val mavenSettings =
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

        github.name := name.value,

        pomExtra := {
          val user = github.user.value
          val name = github.name.value
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

  import autoImport._

  override def projectSettings: Seq[Def.Setting[_]] = mavenSettings
}
