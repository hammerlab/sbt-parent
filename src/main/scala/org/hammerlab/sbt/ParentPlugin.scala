package org.hammerlab.sbt

import sbt._
import Keys._

object ParentPlugin extends AutoPlugin {

  object autoImport {
    val libraries = settingKey[Map[Symbol, ModuleID]]("Common dependencies")
    val scalatestVersion = settingKey[String]("Version of scalatest test-dep to use")
    val sparkVersion = settingKey[String]("Spark version to use")
    val githubUser = settingKey[String]("Github user/org to point to")
  }

  import autoImport._

  override def trigger: PluginTrigger = allRequirements

  override def projectSettings: Seq[_root_.sbt.Def.Setting[_]] = Seq(

    organization := "org.hammerlab",

    githubUser := "hammerlab",

    scalatestVersion := "3.0.0",
    sparkVersion := "1.6.3",

    libraries :=
      Map(
        'scalatest -> "org.scalatest" %% "scalatest" % scalatestVersion.value,
        'spark -> "org.apache.spark" %% "spark-core" % sparkVersion.value,
        'spark_testing_base -> "com.holdenkarau" %% "spark-testing-base" % s"${sparkVersion.value}_0.4.4",
        'spire -> "org.spire-math" %% "spire" % "0.11.0"
      ),

    libraryDependencies <++= libraries { v => Seq(
      v('scalatest) % "test"
    )},

    parallelExecution in Test := false,

    publishTo := {
      val nexus = "https://oss.sonatype.org/"
      if (isSnapshot.value)
        Some("snapshots" at nexus + "content/repositories/snapshots")
      else
        Some("releases" at nexus + "service/local/staging/deploy/maven2")
    },

    publishMavenStyle := true,
    publishArtifact in Test := false,
    pomIncludeRepository := { _ => false },

    pomExtra := {
      val n = name.value
      val user = githubUser.value
      <url>
        https://github.com/{user}/{n}
      </url>
        <licenses>
          <license>
            <name>Apache License</name>
            <url>https://raw.github.com/{user}/{n}/master/LICENSE</url>
            <distribution>repo</distribution>
          </license>
        </licenses>
        <scm>
          <url>git@github.com:{user}/{n}.git</url>
          <connection>scm:git:git@github.com:{user}/{n}.git</connection>
          <developerConnection>scm:git:git@github.com:{user}/{n}.git</developerConnection>
        </scm>
        <developers>
          <developer>
            <id>hammerlab</id>
            <name>Hammer Lab</name>
            <url>https://github.com/{user}</url>
          </developer>
        </developers>
    },

    crossScalaVersions := Seq("2.10.6", "2.11.8"),

    scalaVersion := "2.11.8"
  )
}
