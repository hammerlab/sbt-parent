import sbt.librarymanagement.{ Developer, ScmInfo }

organization in ThisBuild := "org.hammerlab.sbt"

val plugin = sbtPlugin := true

lazy val lib = project.settings(
  plugin,
  version := "1.0.0-SNAPSHOT"
)

lazy val maven = project.settings(
  plugin,
  version := "1.0.0",
  addSbtPlugin("org.xerial.sbt" % "sbt-sonatype" % "2.0")
)

lazy val all = project.settings(
  name := "parent",
  version := "1.0.0-SNAPSHOT",
  plugin,
  addSbtPlugin("com.eed3si9n"    % "sbt-assembly"    % "0.14.6"),
  addSbtPlugin("org.scoverage"   % "sbt-scoverage"   % "1.5.1"),
  addSbtPlugin("org.hammerlab"   % "sbt-coveralls"   % "1.2.3"),
  addSbtPlugin("com.jsuereth"    % "sbt-pgp"         % "1.1.0"),
  addSbtPlugin("org.scalariform" % "sbt-scalariform" % "1.8.2"),
  addSbtPlugin("io.get-coursier" % "sbt-coursier"    % "1.0.0"),
  addSbtPlugin("org.xerial.sbt"  % "sbt-sonatype"    % "2.0")
).dependsOn(
  lib,
  maven
)

lazy val hammerlab = project.settings(
  name := "base",
  plugin,
  version := "1.0.0-SNAPSHOT"
).dependsOn(
  all
)

developers in ThisBuild += Developer(
  id    = "HammerLab",
  name  = "Hammer Lab",
  email = "info@hammerlab.org",
  url   = new URL("https://github.com/hammerlab")
)

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
