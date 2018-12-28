package org.hammerlab.sbt.plugin

import java.io.File

import org.hammerlab.sbt.deps.Group
import org.hammerlab.sbt.plugin.Root.autoImport.parent
import org.hammerlab.sbt.plugin.Versions.autoImport.versions
import org.hammerlab.sbt.{ Lib, Libs, aliases }
import sbt.Keys._
import sbt._
import sbtbuildinfo.BuildInfoKeys._
import sbtbuildinfo._
import sbtcrossproject.{ CrossPlugin, CrossProject, CrossType, JVMPlatform, Platform }
import scalajscrossproject.JSPlatform
import sourcecode.Name

object Parent
  extends Plugin(
    Deps,
    GitHub,
    Maven,
    CrossPlugin,
    Spark,
    Versions
  ) {

  import Group._

  trait platform {
    val js  =  JSPlatform
    val jvm = JVMPlatform
  }

  object autoImport
    extends hammerlab.deps.syntax
       with aliases
       with platform {

    def cross                      (implicit name: Name, crossType: CrossType = CrossType.Full): CrossProject = cross(jvm, js)(name, crossType)
    def cross(platforms: Platform*)(implicit name: Name, crossType: CrossType): CrossProject =
      CrossProject(
        name.value,
        new File(name.value)
      )(
        platforms: _*
      )
      .crossType(crossType)
      .enablePlugins(BuildInfoPlugin)
      .settings(
        buildInfoPackage := s"${organization.value}.${name.value}",
        buildInfoObject := "build",
        (unmanagedResourceDirectories in sbt.Test) +=
          crossType
            .sharedSrcDir(
              baseDirectory.value,
              "test"
            )
            .map {
              _.getParentFile / "resources"
            },
        buildInfoKeys :=
          Seq[BuildInfoKey](
            Keys.name,
            version,
            scalaVersion,
            sbtVersion,
            BuildInfoKey
              .map(resourceDirectories in sbt.Test) {
                case ("test_resourceDirectories",  v) ⇒
                           "resourceDirectories" → v.map(_.toString)
              }
          )
      )

    val resourceDirs = settingKey[Seq[String]]("resourceDirectories mapped as Strings, for use in scala.js projects' BuildInfo object")

    object platform extends platform

    implicit class CrossProjectXOps(val cp: CrossProject) extends AnyVal {
      def x(implicit name: Name) =
        parent(
          cp
            .componentProjects
            .map {
              p ⇒ p: ProjectReference
            }
            : _ *
        )
    }

    // TODO: move these to a non-plugin library, for re-use
    val             args4j =                     "args4j"  ^ "args4j"
    val           autowire =                "com.lihaoyi" ^^ "autowire"
    val          boopickle =                  "io.suzaku" ^^ "boopickle"
    val             breeze =               "org.scalanlp" ^^ "breeze"
    val           case_app = "com.github.alexarchambault" ^^ "case-app"
    val              guava =           "com.google.guava"  ^ "guava"
    val seqdoop_hadoop_bam =               ("org.seqdoop"  ^ "hadoop-bam") - hadoop
    val             htsjdk =       ("com.github.samtools"  ^ "htsjdk") - ("org.xerial.snappy" ^ "snappy-java")
    val            kittens =              "org.typelevel" ^^ "kittens"
    val           magnolia =             "com.propensive" ^^ "magnolia"
    val       parquet_avro =         "org.apache.parquet"  ^ "parquet-avro"
    val            purecsv =         "com.github.melrief" ^^ "purecsv"
    val          scalatags =                "com.lihaoyi" ^^ "scalatags"
    val         scalautils =             "org.scalautils" ^^ "scalautils"
    val          shapeless =                "com.chuusai" ^^ "shapeless"
    val              slf4j =                "org.clapper" ^^ "grizzled-slf4j"
    val         sourcecode =                "com.lihaoyi" ^^ "sourcecode"
    val              spire =              "org.typelevel" ^^ "spire"
    val               sttp =      "com.softwaremill.sttp" ^^ "core"

    object bdg {
      val artifactFn = (prefix: String, name: String) ⇒ s"$prefix-$name-spark2"
      object adam
        extends Libs(
          "org.bdgenomics.adam" ^^ "adam" ^ "0.24.0",
          artifactFn
        ) {
        val core = lib
      }
      object formats extends Lib("org.bdgenomics.bdg-formats" ^ "bdg-formats" ^ "0.10.1")
      object quinine extends Libs(("org.bdgenomics.quinine" ^^ "quinine" ^ "0.0.2") - adam.core) {
        val core = lib
      }
      object utils
        extends Libs(
          "org.bdgenomics.utils" ^^ "utils" ^ "0.2.14",
          artifactFn
        )
      {
        val         cli = lib
        val intervalrdd = lib
        val          io = lib
        val     metrics = lib
        val        misc = lib
      }
      def  global = Seq(adam, formats, quinine, utils).flatMap(_.global)
      def project = Seq(adam, formats, quinine, utils).flatMap(_.project)
    }

    object cats
      extends Libs(
        "org.typelevel" ^^ "cats" ^ "1.5.0"
      ) {
      val   core = lib
      val   free = lib
      val macros = lib
      val kernel = lib
      val   laws = lib
      val effect = {
        val dep = "org.typelevel" ^^ "cats-effect" ^ "1.1.0"
        libs += dep
        dep
      }
    }

    object commons {
      val   io =         "commons-io" ^ "commons-io"
      val math = "org.apache.commons" ^ "commons-math3"
      val defaults =
        versions(
            io → "2.5",
          math → "3.6.1"
        )
    }

    object circe
      extends Libs(
        "io.circe" ^^ "circe" ^ "0.9.3"
      ) {
      val    core = lib
      val generic = lib
      val literal = lib
      val  parser = lib
    }

    object fs2
      extends Libs(
        "co.fs2" ^^ "fs2" ^ "1.0.0"
      ) {
      val              core  = lib
      val                io  = lib
      val `reactive-streams` = lib
    }

    object http4s
      extends Libs(
        "org.http4s" ^^ "http4s" ^ "0.19.0"
      ) {
      val `blaze-server` = lib
      val `blaze-client` = lib
      val         circe  = lib
      val           dsl  = lib
    }

    object slinky
      extends Libs(
        "me.shadaj" ^^ "slinky" ^ "0.5.0"
      ) {
      val                  core  = lib
      val                   web  = lib
      val                native  = lib
      val                   hot  = lib
      val `scalajsreact-interop` = lib

      import sbt._
      import Keys._
      override def settings: SettingsDefinition = scalacOptions += "-P:scalajs:sjsDefinedByDefault"
    }
  }

  import autoImport._

  val wrappers = Seq(
    cats,
    circe,
    fs2,
    http4s,
    slinky
  )

  override def globalSettings = (
    Seq(
      versions(
        args4j             → "2.33"     ,
        autowire           → "0.2.6"    ,
        boopickle          → "1.3.0"    ,
        breeze             → "0.13.2"   ,
        case_app           → "2.0.0-M5" ,
        guava              → "19.0"     ,
        htsjdk             → "2.9.1"    ,
        kittens            → "1.2.0"    ,
        log4j              → "1.7.21"   ,
        magnolia           → "0.10.0"   ,
        parquet_avro       → "1.8.1"    ,
        purecsv            → "0.1.1"    ,
        scalatags          → "0.6.7"    ,
        scalautils         → "2.1.5"    ,
        seqdoop_hadoop_bam → "7.9.0"    ,
        shapeless          → "2.3.3"    ,
        slf4j              → "1.3.1"    ,
        sourcecode         → "0.1.4"    ,
        spire              → "0.15.0"   ,
        sttp               → "1.3.9"    ,
        test_logging       → "1.1.0"    ,
      )
    )
    ++ bdg.global
    ++ wrappers.flatMap(_.global)
  )

  override def projectSettings = (
    Seq(
      commons.defaults
    )
    ++ bdg.project
    ++ wrappers.flatMap(_.project)
  )
}
