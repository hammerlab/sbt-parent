
// external plugin short-hands
val sbtAssembly = addSbtPlugin("com.eed3si9n"    % "sbt-assembly"    % "0.14.6")
val    sonatype = addSbtPlugin("org.xerial.sbt"  % "sbt-sonatype"    % "2.0")
val   scoverage = addSbtPlugin("org.scoverage"   % "sbt-scoverage"   % "1.5.1")
val   coveralls = addSbtPlugin("org.hammerlab"   % "sbt-coveralls"   % "1.2.3")
val         pgp = addSbtPlugin("com.jsuereth"    % "sbt-pgp"         % "1.1.0")
val    coursier = addSbtPlugin("io.get-coursier" % "sbt-coursier"    % "1.0.0")

lazy val lib = project.settings(
  sbtPlugin := false,
  libraryDependencies += "org.scala-sbt" % "sbt" % sbtVersion.value % "provided"
)

lazy val assembly = project.settings(
  sbtAssembly
).dependsOn(
  deps,
  lib,
  scala,
  versions
)

lazy val deps = project.dependsOn(lib, versions)

lazy val github = project

lazy val maven = project.settings(sonatype).dependsOn(lib)

lazy val root = project.settings(scoverage).dependsOn(maven)

lazy val scala = project.dependsOn(deps, lib, versions)

lazy val spark = project.dependsOn(deps, lib, test, versions)

lazy val test = project.dependsOn(deps, lib, versions)

lazy val travis = project.settings(
  scoverage,
  coveralls
).dependsOn(
  root,
  scala
)


lazy val versions = project.settings(pgp).dependsOn(lib)

// Plugin exposing all non-hammerlab-specific functionality
lazy val parent = project.settings(
  coursier
).dependsOn(
  assembly,
  deps,
  github,
  lib,
  maven,
  root,
  scala,
  spark,
  test,
  travis,
  versions
)

// All-purpose hammerlab-specific plugin
lazy val base = project.dependsOn(parent)

lazy val sbt_parent = Project("sbt-parent", file(".")) settings(
  publish := {},
  publishLocal := {},
  publishM2 := {},
  publishArtifact := false
) aggregate(
  assembly,
  base,
  deps,
  github,
  lib,
  maven,
  parent,
  root,
  scala,
  spark,
  test,
  travis,
  versions
)
