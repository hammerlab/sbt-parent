// upstream setting conflicts with module name in this project
import org.hammerlab.sbt.plugin.GitHub.autoImport.{ github ⇒ gh }

default(
  `2.12` only,
  subgroup("sbt"),
  clearTestDeps,
  sbtPlugin := true
)

// external-plugin short-hands
val    sbtAssembly = addSbtPlugin("com.eed3si9n"    % "sbt-assembly"        % "0.14.6")
val       sonatype = addSbtPlugin("org.xerial.sbt"  % "sbt-sonatype"        % "2.0")
val      scoverage = addSbtPlugin("org.scoverage"   % "sbt-scoverage"       % "1.5.1")
val      coveralls = addSbtPlugin("org.hammerlab"   % "sbt-coveralls"       % "1.2.3")
val            pgp = addSbtPlugin("com.jsuereth"    % "sbt-pgp"             % "1.1.1")
val       coursier = addSbtPlugin("io.get-coursier" % "sbt-coursier"        % "1.0.2")
val     sbtScalaJS = addSbtPlugin("org.scala-js"    % "sbt-scalajs"         % "0.6.22")
val scalaJSBundler = addSbtPlugin("ch.epfl.scala"   % "sbt-scalajs-bundler" % "0.10.0")

lazy val lib = project.settings(
  r"4.1.0",
  sbtPlugin := false,
  resolvers += Resolver.url("sbt-plugins", "https://dl.bintray.com/scala-js/scala-js-releases/")(Resolver.ivyStylePatterns),
  providedDeps += "org.scala-sbt" ^ "sbt" ^ sbtVersion.value,
  sbtScalaJS
)

lazy val assembly = project.settings(
  v"4.6.2",
  sbtAssembly,
  sbtScalaJS
).dependsOn(
  deps,
  lib,
  scala,
  versions
)

lazy val deps = project.settings(
  v"4.5.2",
  sbtScalaJS
).dependsOn(
  lib,
  versions
)

lazy val github = project.settings(r"4.1.0")

lazy val js = project.settings(
  v"1.2.2",
  sbtScalaJS,
  scalaJSBundler
).dependsOn(
  deps,
  versions
)

lazy val maven = project.settings(
  r"4.2.0",
  sonatype
).dependsOn(
  lib
)

lazy val root = project.settings(
  v"4.6.2",
  scoverage,
  dep(sourcecode)
).dependsOn(
  github,
  maven,
  versions
)

lazy val scala = project.settings(
  v"4.6.2"
).dependsOn(
  deps,
  lib,
  versions
)

lazy val spark = project.settings(
  v"4.6.2",
  dep(sourcecode)
).dependsOn(
  deps,
  lib,
  scala,
  test,
  versions
)

lazy val test = project.settings(
  v"4.5.2"
).dependsOn(
  deps,
  lib,
  versions
)

lazy val travis = project.settings(
  v"4.6.2",
  scoverage,
  coveralls
).dependsOn(
  root,
  scala
)

lazy val versions = project.settings(
  v"4.5.2",
  pgp,
  dep(
    sourcecode,
    hammerlab.io % "5.1.0"
  )
).dependsOn(
  lib
)

// Plugin exposing all non-hammerlab-specific functionality
lazy val parent = project.settings(
  v"4.6.3",
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
  v"4.6.3",
).dependsOn(
  parent
)

lazy val `sbt-parent` = Root.autoImport.root(
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
