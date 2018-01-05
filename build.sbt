
lazy val lib = project.settings(
  libraryDependencies += "org.scala-sbt" % "sbt" % "1.0.4" % "provided"
)

lazy val github = project.settings(plugin)

lazy val maven = project.settings(
  plugin,
  addSbtPlugin("org.xerial.sbt" % "sbt-sonatype" % "2.0")
)

// Plugin exposing all non-hammerlab-specific functionality
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
  github,
  lib,
  maven
)

// All-purpose hammerlab-specific plugin
lazy val base = project.settings(
  plugin,
).dependsOn(
  parent
)

lazy val root = project in file(".") settings(
  publish := {},
  publishLocal := {},
  publishM2 := {},
  publishArtifact := false
) aggregate(
  github,
  lib,
  maven,
  parent,
  base
)
