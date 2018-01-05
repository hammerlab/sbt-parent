import sbt._
import Keys._
import sbt.librarymanagement.Developer
import xerial.sbt.Sonatype
import xerial.sbt.Sonatype.SonatypeKeys._

object Defaults
  extends AutoPlugin {
  override def trigger = allRequirements
  override def requires = super.requires && Sonatype

  val githubUser = "hammerlab"
  val repo = "sbt-parent"
  val connection = s"scm:git:git@github.com:$githubUser/$repo.git"

  object autoImport {
    val plugin = sbtPlugin := true
  }

  override def projectSettings: Seq[Def.Setting[_]] =
    Seq(
      organization := "org.hammerlab.sbt",
      version := "1.0.0-M2",

      sonatypeProfileName := "org.hammerlab",

      publishTo := {
        val nexus = "https://oss.sonatype.org/"
        if (isSnapshot.value)
          Some("snapshots" at nexus + "content/repositories/snapshots")
        else
          Some("releases" at nexus + "service/local/staging/deploy/maven2")
      },

      publishMavenStyle := true,
      publishArtifact in (ThisBuild, Test) := false,
      pomIncludeRepository := { _ => false },

      licenses += ("Apache 2.0", new URL("https://www.apache.org/licenses/LICENSE-2.0")),

      scmInfo := Some(
        ScmInfo(
          new URL(s"https://github.com/$githubUser/$repo"),
          connection,
          connection
        )
      ),

      developers += Developer(
        id    = "HammerLab",
        name  = "Hammer Lab",
        email = "info@hammerlab.org",
        url   = new URL("https://github.com/hammerlab")
      ),

      pomExtra := <url>https://github.com/{githubUser}/{repo}</url>
    )
}
