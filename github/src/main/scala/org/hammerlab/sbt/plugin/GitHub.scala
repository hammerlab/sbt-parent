package org.hammerlab.sbt.plugin

import java.net.URL

import hammerlab.sbt._
import sbt.Keys._
import sbt.PluginTrigger.AllRequirements
import sbt._
import sbt.librarymanagement.ScmInfo

import scala.xml.NodeSeq.Empty

object GitHub
  extends AutoPlugin {
  override def trigger = AllRequirements

  case class GitHub(org: String, name: String)
  object GitHub {
    implicit def apply(t: (String, String)): Option[GitHub] = Some(GitHub(t._1, t._2))
  }

  object autoImport {

    object github {
      def apply(user: String, repo: String = null) =
        Seq(
          this.user.*(user),
        ) ++
        Option(repo).map { this.repo.* }.toList

      val user = SettingKey[Option[String]]("github-user", "Github user/org")
      val repo = SettingKey[Option[String]]("github-repo", "Github repository basename")
      val  org = user
    }

    val apache2License = ("Apache 2.0", new URL("https://www.apache.org/licenses/LICENSE-2.0"))
    val apache2 = licenses := Seq(apache2License)

    implicit def liftURL(url: String): URL = new URL(url)
  }

  import autoImport._

  override def globalSettings =
    Seq(
      github.user := None,
      github.repo := None
    )

  override def projectSettings =
    Seq(
      scmInfo :=
        github
          .user
          .value
          .map {
            user ⇒
              val repo = github.repo.value.getOrElse(name.value)
              val url = s"https://github.com/$user/$repo"
              val connection = s"scm:git:git@github.com:$user/$repo.git"
              ScmInfo(
                url,
                connection,
                connection
              )
          },

      pomExtra :=
        scmInfo
          .value
          .map {
            scm ⇒
              <url>{scm.browseUrl}</url>
          }
          .getOrElse(Empty)
    )
}
