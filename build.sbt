
val defaults = Seq(
  organization := "org.hammerlab.sbt"
)

val plugin = defaults ++ Seq(
  sbtPlugin := true,
  crossSbtVersions := Seq("0.13.16", "1.0.4"),
  libraryDependencies += Defaults.sbtPluginExtra(
    "com.dwijnand" % "sbt-compat" % "1.1.1-SNAPSHOT",
    (sbtBinaryVersion in pluginCrossBuild).value,
    (scalaBinaryVersion in update).value
  )
)

lazy val lib = project.settings(
  plugin,
  version := "1.0.0-SNAPSHOT"
)

lazy val maven = project.settings(
  plugin,
  version := "1.0.0-SNAPSHOT",
  addSbtPlugin("org.xerial.sbt" % "sbt-sonatype" % "2.0")
).dependsOn(
  lib
)

lazy val all = project.settings(
  plugin,
  version := "1.0.0-SNAPSHOT"
)

enablePlugins(GitVersioning)

git.formattedShaVersion := git.gitHeadCommit.value map { sha => s"${sha.substring(0, 8)}" }

publishTo := {
  val nexus = "https://oss.sonatype.org/"
  if (isSnapshot.value)
    Some("snapshots" at nexus + "content/repositories/snapshots")
  else
    Some("releases" at nexus + "service/local/staging/deploy/maven2")
}

publishMavenStyle := true
publishArtifact in Test := false
pomIncludeRepository := { _ => false }

pomExtra :=
  <url>
    https://github.com/hammerlab/${name}
  </url>
    <licenses>
      <license>
        <name>Apache License</name>
        <url>https://raw.github.com/hammerlab/${name}/master/LICENSE</url>
        <distribution>repo</distribution>
      </license>
    </licenses>
    <scm>
      <url>git@github.com:hammerlab/${name}.git</url>
      <connection>scm:git:git@github.com:hammerlab/${name}.git</connection>
      <developerConnection>scm:git:git@github.com:hammerlab/${name}.git</developerConnection>
    </scm>
    <developers>
      <developer>
        <id>hammerlab</id>
        <name>Hammer Lab</name>
        <url>https://github.com/hammerlab</url>
      </developer>
    </developers>

//addSbtPlugin("org.scoverage" % "sbt-scoverage" % "1.5.1")
//addSbtPlugin("org.hammerlab" % "sbt-coveralls" % "1.2.3")
//addSbtPlugin("com.jsuereth" % "sbt-pgp" % "1.1.0")
//addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "0.14.6")
//addSbtPlugin("org.scalariform" % "sbt-scalariform" % "1.8.2")
//addSbtPlugin("io.get-coursier" % "sbt-coursier" % "1.0.0")
