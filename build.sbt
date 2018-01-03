import sbt.librarymanagement.{ Developer, ScmInfo }
import xerial.sbt.Sonatype.SonatypeKeys.sonatypeProfileName

organization in ThisBuild := "org.hammerlab.sbt"
version in ThisBuild := "1.0.0-SNAPSHOT"

val plugin = sbtPlugin := true

lazy val gh = (project in file("github")).settings(
  name := "github",
  plugin
)

lazy val lib = project.settings(
  libraryDependencies += "org.scala-sbt" % "sbt" % "1.0.4" % "provided"
)

lazy val maven = project.settings(
  plugin,
  addSbtPlugin("org.xerial.sbt" % "sbt-sonatype" % "2.0")
)

lazy val parent = project.settings(
  plugin,
  addSbtPlugin("com.eed3si9n"    % "sbt-assembly"    % "0.14.6"),
  addSbtPlugin("org.scoverage"   % "sbt-scoverage"   % "1.5.1"),
  addSbtPlugin("org.hammerlab"   % "sbt-coveralls"   % "1.2.3"),
  addSbtPlugin("com.jsuereth"    % "sbt-pgp"         % "1.1.0"),
  addSbtPlugin("org.scalariform" % "sbt-scalariform" % "1.8.2"),
  addSbtPlugin("io.get-coursier" % "sbt-coursier"    % "1.0.0"),
  addSbtPlugin("org.xerial.sbt"  % "sbt-sonatype"    % "2.0")
).dependsOn(
  gh,
  lib,
  maven
)

lazy val base = project.settings(
  plugin,
).dependsOn(
  parent
)

developers in ThisBuild += Developer(
  id    = "HammerLab",
  name  = "Hammer Lab",
  email = "info@hammerlab.org",
  url   = new URL("https://github.com/hammerlab")
)

sonatypeProfileName := (
  if (organization.value.startsWith("org.hammerlab"))
    "org.hammerlab"
  else
    sonatypeProfileName.value
)

github.user("hammerlab")
github.repo("sbt-parent")
apache2

/*
publishTo in ThisBuild := {
  val nexus = "https://oss.sonatype.org/"
  if (isSnapshot.value)
    Some("snapshots" at nexus + "content/repositories/snapshots")
  else
    Some("releases" at nexus + "service/local/staging/deploy/maven2")
}

publishMavenStyle in ThisBuild := true
publishArtifact in (ThisBuild, Test) := false
pomIncludeRepository in ThisBuild := { _ => false }

licenses in ThisBuild += ("Apache 2.0", new URL("https://www.apache.org/licenses/LICENSE-2.0"))

val githubUser = "hammerlab"
val repo = "sbt-parent"
val connection = s"scm:git:git@github.com:$githubUser/$repo.git"
scmInfo in ThisBuild := Some(
  ScmInfo(
    new URL(s"https://github.com/$githubUser/$repo"),
    connection,
    connection
  )
)

pomExtra in ThisBuild := <url>https://github.com/{githubUser}/{repo}</url>
*/
