// upstream setting conflicts with module name in this project
import org.hammerlab.sbt.plugin.GitHub.autoImport.{ github ⇒ gh }
import Resolver.bintrayIvyRepo

default(
  `2.12` only,
  subgroup("sbt"),
  clearTestDeps,
  resolvers ++= Seq(
    bintrayIvyRepo("portable-scala", "sbt-plugins"),
    bintrayIvyRepo("scala-js", "scala-js-releases")
  )
)

// external-plugin short-hands
val         sbtAssembly = addSbtPlugin(   "com.eed3si9n"    % "sbt-assembly"             % "0.14.6")
val            sonatype = addSbtPlugin(   "org.xerial.sbt"  % "sbt-sonatype"             % "2.0"   )
val           scoverage = addSbtPlugin(   "org.scoverage"   % "sbt-scoverage"            % "1.5.1" )
val           coveralls = addSbtPlugin(   "org.hammerlab"   % "sbt-coveralls"            % "1.2.3" )
val                 pgp = addSbtPlugin(   "com.jsuereth"    % "sbt-pgp"                  % "1.1.1" )
val            coursier = addSbtPlugin(   "io.get-coursier" % "sbt-coursier"             % "1.0.2" )
val          sbtScalaJS = addSbtPlugin(   "org.scala-js"    % "sbt-scalajs"              % "0.6.25")
val      scalaJSBundler = addSbtPlugin(   "ch.epfl.scala"   % "sbt-scalajs-bundler"      % "0.13.1")
val   scalaCrossProject = addSbtPlugin("org.portable-scala" % "sbt-crossproject"         % "0.6.0" )
val scalaJSCrossProject = addSbtPlugin("org.portable-scala" % "sbt-scalajs-crossproject" % "0.6.0" )

lazy val lib = project.settings(
  v"4.2.0",
  providedDeps += "org.scala-sbt" ^ "sbt" ^ sbtVersion.value,
  sbtScalaJS
)

lazy val assembly = plugin.settings(
  v"4.6.3",
  sbtAssembly,
  sbtScalaJS
).dependsOn(
  deps,
  lib,
  scala,
  versions
)

lazy val deps = plugin.settings(
  v"4.5.3",
  sbtScalaJS
).dependsOn(
  lib,
  versions
)

lazy val github = plugin.settings(r"4.1.0")

lazy val js = plugin.settings(
  v"1.3.0",
  sbtScalaJS,
  scalaJSBundler,
  scalaJSCrossProject
).dependsOn(
  deps,
  versions
)

lazy val maven = plugin.settings(
  r"4.2.0",
  sonatype
).dependsOn(
  lib
)

lazy val root = plugin.settings(
  v"4.6.3",
  scoverage,
  dep(sourcecode)
).dependsOn(
  github,
  maven,
  versions
)

lazy val scala = plugin.settings(
  v"4.6.3"
).dependsOn(
  deps,
  lib,
  versions
)

lazy val spark = plugin.settings(
  v"4.6.3",
  dep(sourcecode)
).dependsOn(
  deps,
  lib,
  scala,
  test,
  versions
)

lazy val test = plugin.settings(
  v"4.5.3"
).dependsOn(
  deps,
  lib,
  versions
)

lazy val travis = plugin.settings(
  v"4.6.3",
  scoverage,
  coveralls
).dependsOn(
  root,
  scala
)
.enablePlugins(SbtPlugin)

lazy val versions = plugin.settings(
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
lazy val parent = plugin.settings(
  v"4.6.4",
  coursier,
  scalaCrossProject,
  dep(
    "org.scala-sbt" ^^ "main" ^ "1.2.3"
  )
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
lazy val base = plugin.settings(
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
