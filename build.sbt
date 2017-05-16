sbtPlugin := true

name := "sbt-parent"

organization := "org.hammerlab"

version := "2.0.0-SNAPSHOT"

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

crossPaths := false

addSbtPlugin("net.virtual-void" % "sbt-dependency-graph" % "0.8.2")
addSbtPlugin("org.xerial.sbt" % "sbt-sonatype" % "1.1")
addSbtPlugin("org.scoverage" % "sbt-scoverage" % "1.5.0")
addSbtPlugin("org.scoverage" % "sbt-coveralls" % "1.1.0")
addSbtPlugin("com.typesafe.sbt" % "sbt-pgp" % "0.8")
addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "0.14.4")
addSbtPlugin("com.typesafe.sbt" % "sbt-scalariform" % "1.3.0")

test in assembly := {}
