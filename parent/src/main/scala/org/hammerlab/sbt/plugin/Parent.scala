package org.hammerlab.sbt.plugin

import org.hammerlab.sbt.deps.{ Group, SnapshotOps }
import org.hammerlab.sbt.plugin.Spark.autoImport.{ hadoop, sparkVersion }
import org.hammerlab.sbt.plugin.Test.autoImport.scalatest
import org.hammerlab.sbt.plugin.Versions.autoImport.versions
import sbt._

object Parent
  extends Plugin(
    Deps,
    GitHub,
    Maven,
    Spark,
    Versions
  ) {

  import Group._

  object autoImport
    extends SnapshotOps {

    val             args4j =                     "args4j"  ^ "args4j"
    val             breeze =               "org.scalanlp" ^^ "breeze"
    val           case_app = "com.github.alexarchambault" ^^ "case-app"
    val               cats =              "org.typelevel" ^^ "cats-core"
    val              guava =           "com.google.guava"  ^ "guava"
    val seqdoop_hadoop_bam =               ("org.seqdoop"  ^ "hadoop-bam") - hadoop
    val             htsjdk =       ("com.github.samtools"  ^ "htsjdk") - ("org.xerial.snappy" ^ "snappy-java")
    val            kittens =              "org.typelevel" ^^ "kittens"
    val              log4j =                  "org.slf4j"  ^ "slf4j-log4j12"
    val              mllib =          ("org.apache.spark" ^^ "spark-mllib") - scalatest
    val       parquet_avro =         "org.apache.parquet"  ^ "parquet-avro"
    val            purecsv =         "com.github.melrief" ^^ "purecsv"
    val          scalatags =                "com.lihaoyi" ^^ "scalatags"
    val         scalautils =             "org.scalautils" ^^ "scalautils"
    val          shapeless =                "com.chuusai" ^^ "shapeless"
    val              slf4j =                "org.clapper" ^^ "grizzled-slf4j"
    val              spire =              "org.typelevel" ^^ "spire"

    object bdg {
      object adam {
        val core = "org.bdgenomics.adam" ^^ "adam-core"
      }
      val formats = "org.bdgenomics.bdg-formats" ^ "bdg-formats"
      object quinine {
        val core = ("org.bdgenomics.quinine" ^^ "quinine-core") - adam.core
      }
      object utils {
        val version = SettingKey[String]("bdgUtilsVersion", "org.bdgenomics.utils version to use")
        val intervalrdd = "org.bdgenomics.utils" ^^ "utils-intervalrdd-spark2"
        val          io = "org.bdgenomics.utils" ^^ "utils-io-spark2"
        val     metrics = "org.bdgenomics.utils" ^^ "utils-metrics-spark2"
        val        misc = "org.bdgenomics.utils" ^^ "utils-misc-spark2"
      }
    }

    object commons {
      val   io =         "commons-io" ^    "commons-io"
      val math = "org.apache.commons" ^ "commons-math3"
    }

    object circe {
      val version = SettingKey[String]("circeVersion", "Circe JSON library version")
      def lib(name: String) = "io.circe" ^^ s"circe-$name"
      val generic = lib("generic")
      val literal = lib("literal")
      val    core = lib("core")
      val  parser = lib("parser")

      val defaultVersions =
        versions ++=
          Seq(
            generic → version.value,
            literal → version.value,
               core → version.value,
             parser → version.value
          )
    }

    object http4s {
      val version = SettingKey[String]("http4sVersion", "HTTP4S version")
      def lib(name: String) = "org.http4s" ^^ s"http4s-$name"
      val blaze_server = lib("blaze-server")
      val blaze_client = lib("blaze-client")
      val        circe = lib("circe")
      val          dsl = lib("dsl")

      val defaultVersions =
        versions ++=
      Seq(
         blaze_server → version.value,
         blaze_client → version.value,
                circe → version.value,
                  dsl → version.value
      )
    }
  }

  import autoImport._

  override def projectSettings: Seq[_root_.sbt.Def.Setting[_]] =
    Seq(
      bdg.utils.version := "0.2.13",
      circe.version := "0.9.3",
      http4s.version := "0.18.11",

      versions ++= Seq(
        args4j                → "2.33",
        bdg.formats           → "0.10.1",
        bdg.utils.intervalrdd → bdg.utils.version.value,
        bdg.utils.io          → bdg.utils.version.value,
        bdg.utils.metrics     → bdg.utils.version.value,
        bdg.utils.misc        → bdg.utils.version.value,
        breeze                → "0.13.2",
        cats                  → "1.0.1",
        commons.io            → "2.5",
        commons.math          → "3.6.1",
        case_app              → "2.0.0-M3",
        guava                 → "19.0",
        htsjdk                → "2.9.1",
        kittens               → "1.0.0-RC2",
        log4j                 → "1.7.21",
        mllib                 → sparkVersion.value,
        parquet_avro          → "1.8.1",
        purecsv               → "0.1.1",
        scalatags             → "0.6.7",
        scalautils            → "2.1.5",
        seqdoop_hadoop_bam    → "7.9.0",
        shapeless             → "2.3.3",
        slf4j                 → "1.3.1",
        spire                 → "0.15.0"
      ),

      circe.defaultVersions,
      http4s.defaultVersions
    )
}
