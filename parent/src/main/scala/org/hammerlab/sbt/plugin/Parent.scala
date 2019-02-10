package org.hammerlab.sbt.plugin

import java.io.File

import org.hammerlab.sbt.deps.Group
import org.hammerlab.sbt.plugin.Root.autoImport.parent
import org.hammerlab.sbt.{ ContainerPlugin, Lib, Libs, aliases }
import sbt.Keys._
import sbt._
import sbtbuildinfo.BuildInfoKeys._
import sbtbuildinfo._
import sbtcrossproject.{ CrossPlugin, CrossProject, CrossType, JVMPlatform, Platform }
import scalajscrossproject.JSPlatform
import sourcecode.Name

object Parent
  extends ContainerPlugin(
    Deps,
    GitHub,
    Maven,
    CrossPlugin,
    Spark,
    Versions
  )
{

  import Group._

  trait platform {
    val js  =  JSPlatform
    val jvm = JVMPlatform
  }

  def pkg(str: String) = str.replaceAll("-", "_")

  object autoImport
    extends hammerlab.deps.syntax
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
        buildInfoPackage := Seq(pkg(organization.value), "build").mkString("."),
        buildInfoObject := pkg(name.value),
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
        .in(
          cp
            .projects(JVMPlatform)
            .base
            .getParentFile
        )
    }

    val   kryo = aliases.  kryo
    val hadoop = aliases.hadoop
    val  slf4j = aliases. slf4j

    // TODO: move these to a non-plugin library, for re-use
    val             args4j = Lib(                    "args4j"  ^        "args4j" ^     "2.33"           )
    val           autowire = Lib(               "com.lihaoyi" ^^      "autowire" ^    "0.2.6"           )
    val          boopickle = Lib(                 "io.suzaku" ^^     "boopickle" ^    "1.3.0"           )
    val             breeze = Lib(              "org.scalanlp" ^^        "breeze" ^   "0.13.2"           )
    val           case_app = Lib("com.github.alexarchambault" ^^      "case-app" ^ "2.0.0-M5"           )
    val              guava = Lib(          "com.google.guava"  ^         "guava" ^     "19.0"           )
    val              junit = Lib(                     "junit"  ^         "junit" ^     "4.12"           )
    val            kittens = Lib(             "org.typelevel" ^^       "kittens" ^    "1.2.0"           )
    val           magnolia = Lib(            "com.propensive" ^^      "magnolia" ^   "0.10.0"           )
    val       parquet_avro = Lib(        "org.apache.parquet"  ^  "parquet-avro" ^    "1.8.1"           )
    val            purecsv = Lib(        "com.github.melrief" ^^       "purecsv" ^    "0.1.1"           )
    val          scalatags = Lib(               "com.lihaoyi" ^^     "scalatags" ^    "0.6.7"           )
    val         scalautils = Lib(            "org.scalautils" ^^    "scalautils" ^    "2.1.5"           )
    val seqdoop_hadoop_bam = Lib(              ("org.seqdoop"  ^    "hadoop-bam" ^    "7.9.0") - hadoop )
    val          shapeless = Lib(               "com.chuusai" ^^     "shapeless" ^    "2.3.3"           )
    val             snappy = Lib(         "org.xerial.snappy"  ^   "snappy-java" ^  "1.1.7.2"           )
    val         sourcecode = Lib(               "com.lihaoyi" ^^    "sourcecode" ^    "0.1.5"           )
    val              spire = Lib(             "org.typelevel" ^^         "spire" ^   "0.15.0"           )
    val               sttp = Lib(     "com.softwaremill.sttp" ^^          "core" ^    "1.3.9"           )
    val             htsjdk = Lib(      ("com.github.samtools"  ^        "htsjdk" ^    "2.9.1") - snappy )  // out of sort-order bc depends on snappy

    object akka
      extends Libs(
        "com.typesafe.akka" ^^ "akka" ^ "2.5.20"
      ) {
      val  actor = lib
      val stream = lib
      object http
        extends Libs(
          "com.typesafe.akka" ^^ "akka-http" ^ "10.1.7"
        ) {
        val base = lib(_base)
        val core = lib
      }
    }

    object bdg {
      val artifactFn = (prefix: String, name: String) ⇒ s"$prefix-$name-spark2"
      object adam
        extends Libs(
          "org.bdgenomics.adam" ^^ "adam" ^ "0.24.0",
          artifactFn
        ) {
        val core = lib
      }
      val formats = Lib("org.bdgenomics.bdg-formats" ^ "bdg-formats" ^ "0.10.1")

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
      val   io = Lib(        "commons-io" ^ "commons-io"    ^ "2.5")
      val math = Lib("org.apache.commons" ^ "commons-math3" ^ "3.6.1")
    }

    object circe
      extends Libs(
        "io.circe" ^^ "circe" ^ "0.9.3"
      ) {
      val     core = lib
      val  literal = lib
      val   parser = lib
      object generic extends Libs("io.circe" ^^ "circe-generic" ^ "0.9.3") {
        val   core = lib(_base)
        val extras = lib
      }
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
        "me.shadaj" ^^ "slinky" ^ "0.5.1"
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

    object slogging extends Libs("biz.enef" ^^ "slogging" ^ "0.6.1") {
      val core = lib(_base)
      val slf4j = aliases.slf4j.slogging
    }
  }
}
