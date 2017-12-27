package org.hammerlab.sbt.plugin

import sbt.Keys._
import sbt._
import Resolver.{ mavenLocal, sonatypeRepo }
import xerial.sbt.Sonatype
import System.getenv

import scala.xml.NodeSeq.Empty

object Maven
  extends AutoPlugin {

  override def trigger = allRequirements
  override def requires = Sonatype

  object autoImport {

    case class GitHub(org: String, name: String)
    object GitHub {
      implicit def apply(t: (String, String)): Option[GitHub] = Some(GitHub(t._1, t._2))
    }

    val github = settingKey[Option[GitHub]]("Github user/org to point to")

//    val githubUser = settingKey[String]("Github user/org to point to")
//    val githubName = settingKey[String]("Github repository basename")

    val apache2License = ("Apache 2.0", new URL("https://www.apache.org/licenses/LICENSE-2.0"))
    val apache2 = (licenses in ThisBuild) += apache2License

    def env(key: String): Option[String] = Option(getenv(key))

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

        github in Global := None,

        pomExtra := {
          github
            .value
            .map {
              case GitHub(org, name) ⇒
                <url>
                  https://github.com/{org}/{name}
                </url>
            }
            .getOrElse(Empty)
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
