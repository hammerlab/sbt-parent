// upstream setting conflicts with module name in this project
import org.hammerlab.sbt.plugin.GitHub.autoImport.{ github ⇒ gh }
import Resolver.bintrayIvyRepo

default(
  `2.12` only,
  subgroup("sbt"),
  clearTestDeps,
  sbtPlugin := true,
  resolvers ++= Seq(
    bintrayIvyRepo("portable-scala", "sbt-plugins"),
    bintrayIvyRepo("scala-js", "scala-js-releases")
  )
)


// external-plugin short-hands
val       sbtAssembly = addSbtPlugin(   "com.eed3si9n"    % "sbt-assembly"             % "0.14.6")
val          sonatype = addSbtPlugin(   "org.xerial.sbt"  % "sbt-sonatype"             % "2.0"   )
val         scoverage = addSbtPlugin(   "org.scoverage"   % "sbt-scoverage"            % "1.5.1" )
val         coveralls = addSbtPlugin(   "org.hammerlab"   % "sbt-coveralls"            % "1.2.3" )
val               pgp = addSbtPlugin(   "com.jsuereth"    % "sbt-pgp"                  % "1.1.1" )
val          coursier = addSbtPlugin(   "io.get-coursier" % "sbt-coursier"             % "1.0.2" )
val        sbtScalaJS = addSbtPlugin(   "org.scala-js"    % "sbt-scalajs"              % "0.6.25")
val    scalaJSBundler = addSbtPlugin(   "ch.epfl.scala"   % "sbt-scalajs-bundler"      % "0.13.1")
val scalaCrossProject = addSbtPlugin("org.portable-scala" % "sbt-scalajs-crossproject" % "0.6.0" )

lazy val lib = project.settings(
  v"4.2.0",
  sbtPlugin := false,
  providedDeps += "org.scala-sbt" ^ "sbt" ^ sbtVersion.value,
  sbtScalaJS
)

lazy val assembly = project.settings(
  v"4.6.3",
  sbtAssembly,
  sbtScalaJS
).dependsOn(
  deps,
  lib,
  scala,
  versions
)

lazy val deps = project.settings(
  v"4.5.3",
  sbtScalaJS
).dependsOn(
  lib,
  versions
)

lazy val github = project.settings(r"4.1.0")

lazy val js = project.settings(
  v"1.3.0",
  sbtScalaJS,
  scalaJSBundler,
  scalaCrossProject
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
  v"4.6.3",
  scoverage,
  dep(sourcecode)
).dependsOn(
  github,
  maven,
  versions
)

lazy val scala = project.settings(
  v"4.6.3"
).dependsOn(
  deps,
  lib,
  versions
)

lazy val spark = project.settings(
  v"4.6.3",
  dep(sourcecode)
).dependsOn(
  deps,
  lib,
  scala,
  test,
  versions
)

lazy val test = project.settings(
  v"4.5.3"
).dependsOn(
  deps,
  lib,
  versions
)

lazy val travis = project.settings(
  v"4.6.3",
  scoverage,
  coveralls
).dependsOn(
  root,
  scala
)

lazy val versions = project.settings(
  v"4.5.3",
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
  v"4.6.4",
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
  v"4.6.4"
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
