
// external plugin short-hands
val sbtAssembly = addSbtPlugin("com.eed3si9n"    % "sbt-assembly"    % "0.14.6")
val    sonatype = addSbtPlugin("org.xerial.sbt"  % "sbt-sonatype"    % "2.0")
val   scoverage = addSbtPlugin("org.scoverage"   % "sbt-scoverage"   % "1.5.1")
val   coveralls = addSbtPlugin("org.hammerlab"   % "sbt-coveralls"   % "1.2.3")
val         pgp = addSbtPlugin("com.jsuereth"    % "sbt-pgp"         % "1.1.0")
val    coursier = addSbtPlugin("io.get-coursier" % "sbt-coursier"    % "1.0.0")

lazy val lib = project.settings(
  libraryDependencies += "org.scala-sbt" % "sbt" % "1.0.4" % "provided"
)

lazy val assembly = project.settings(
  plugin,
  sbtAssembly
).dependsOn(
  deps,
  lib,
  scala,
  versions
)

lazy val deps = project.settings(plugin).dependsOn(lib, versions)

lazy val github = project.settings(plugin)

lazy val maven = project.settings(plugin, sonatype)

lazy val root = project.settings(
  plugin,
  scoverage
).dependsOn(
  versions
)

lazy val scala = project.settings(plugin).dependsOn(deps, lib, versions)

lazy val spark = project.settings(plugin).dependsOn(deps, lib, test, versions)

lazy val test = project.settings(plugin).dependsOn(deps, lib, versions)

lazy val travis = project.settings(
  plugin,
  scoverage,
  coveralls
).dependsOn(
  root,
  scala
)


lazy val versions = project.settings(
  plugin,
  pgp
).dependsOn(
  lib
)

// Plugin exposing all non-hammerlab-specific functionality
lazy val parent = project.settings(
  plugin,
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
lazy val base = project.settings(
  plugin,
).dependsOn(
  parent
)

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
