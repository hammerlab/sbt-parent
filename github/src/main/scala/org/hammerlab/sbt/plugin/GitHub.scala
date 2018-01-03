package org.hammerlab.sbt.plugin

import java.net.URL

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
    val github = settingKey[Option[GitHub]]("Github repository")
    val githubUser = settingKey[Option[String]]("Github user/org")
    val githubRepo = settingKey[Option[String]]("Github repository basename")

    implicit class GitHubOps(val key: SettingKey[Option[GitHub]]) extends AnyVal {
      def apply(org: String, repo: String) =
        key := (org, repo)

      def user(name: String) = githubUser in ThisBuild := Some(name)
      def repo(name: String) = githubRepo in ThisBuild := Some(name)
    }

    val apache2License = ("Apache 2.0", new URL("https://www.apache.org/licenses/LICENSE-2.0"))
    val apache2 = (licenses in ThisBuild) += apache2License

    implicit def liftURL(url: String): URL = new URL(url)
  }

  import autoImport._

  override def projectSettings: Seq[Def.Setting[_]] =
    Seq(
      github in Global := (
        (githubUser.value, githubRepo.value) match {
          case (Some(user), Some(repo)) ⇒ Some(GitHub(user, repo))
          case (None, None) ⇒ None
          case (Some(user), _) ⇒ throw new Exception(s"Github-user set ($user) but not repo")
          case (_, Some(repo)) ⇒ throw new Exception(s"Github-repo set ($repo) but not user")
        }
      ),

      githubUser in Global := None,
      githubRepo in Global := None,

      scmInfo := (
        github
          .value
          .map {
            case GitHub(user, name) ⇒
              val url = s"https://github.com/$user/$name"
              val connection = s"scm:git:git@github.com:$user/$name.git"
              ScmInfo(
                url,
                connection,
                connection
              )
          }
      ),

      pomExtra := {
        github
          .value
          .map {
            case GitHub(org, name) ⇒
              <url>https://github.com/{org}/{name}</url>
          }
          .getOrElse(Empty)
      }
    )
}
