// upstream setting conflicts with module name in this project
import org.hammerlab.sbt.plugin.GitHub.autoImport.{ github ⇒ gh }

default(
  scala212Only,
  group("org.hammerlab.sbt"),
  clearTestDeps,
  sbtPlugin := true,
  v"4.3.1"
)

// external-plugin short-hands
val    sbtAssembly = addSbtPlugin("com.eed3si9n"    % "sbt-assembly"        % "0.14.6")
val       sonatype = addSbtPlugin("org.xerial.sbt"  % "sbt-sonatype"        % "2.0")
val      scoverage = addSbtPlugin("org.scoverage"   % "sbt-scoverage"       % "1.5.1")
val      coveralls = addSbtPlugin("org.hammerlab"   % "sbt-coveralls"       % "1.2.3")
val            pgp = addSbtPlugin("com.jsuereth"    % "sbt-pgp"             % "1.1.0")
val       coursier = addSbtPlugin("io.get-coursier" % "sbt-coursier"        % "1.0.0")
val     sbtScalaJS = addSbtPlugin("org.scala-js"    % "sbt-scalajs"         % "0.6.22")
val scalaJSBundler = addSbtPlugin("ch.epfl.scala"   % "sbt-scalajs-bundler" % "0.10.0")

lazy val lib = project.settings(
  sbtPlugin := false,
  resolvers += Resolver.url("sbt-plugins", "https://dl.bintray.com/scala-js/scala-js-releases/")(Resolver.ivyStylePatterns),
  providedDeps += "org.scala-sbt" ^ "sbt" ^ sbtVersion.value,
  sbtScalaJS,
  r"4.1.0"
)

lazy val assembly = project.settings(
  sbtAssembly,
  sbtScalaJS
).dependsOn(
  deps,
  lib,
  scala,
  versions
)

lazy val deps = project.settings(
  sbtScalaJS
).dependsOn(
  lib,
  versions
)

lazy val github = project.settings(r"4.1.0")

lazy val js = project.settings(
  v"1.0.1",
  sbtScalaJS,
  scalaJSBundler
).dependsOn(
  deps,
  versions
)

lazy val maven = project.settings(
  r"4.1.0",
  sonatype
).dependsOn(
  lib
)

lazy val root = project.settings(
  scoverage
).dependsOn(
  github,
  maven,
  versions
)

lazy val scala = project.dependsOn(
  deps,
  lib,
  versions
)

lazy val spark = project.dependsOn(
  deps,
  lib,
  scala,
  test,
  versions
)

lazy val test = project.dependsOn(
  deps,
  lib,
  versions
)

lazy val travis = project.settings(
  scoverage,
  coveralls
).dependsOn(
  root,
  scala
)

lazy val versions = project.settings(
  pgp
).dependsOn(
  lib
)

// Plugin exposing all non-hammerlab-specific functionality
lazy val parent = project.settings(
  coursier
).dependsOn(
  assembly,
  deps,
  github,
  js,
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
  v"5.0.0"
).dependsOn(
  parent
)

lazy val sbt_parent = rootProject(
  "sbt-parent",
  assembly,
  base,
  deps,
  github,
  js,
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
