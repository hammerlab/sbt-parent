package org.hammerlab.sbt.plugin

import org.hammerlab.sbt.deps.{ Group, SnapshotOps }
import org.hammerlab.sbt.dsl.Libs
import org.hammerlab.sbt.plugin.Spark.autoImport.hadoop
import org.hammerlab.sbt.plugin.Versions.autoImport.versions

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
    val           autowire =                "com.lihaoyi" ^^ "autowire"
    val          boopickle =                  "io.suzaku" ^^ "boopickle"
    val             breeze =               "org.scalanlp" ^^ "breeze"
    val           case_app = "com.github.alexarchambault" ^^ "case-app"
    val               cats =              "org.typelevel" ^^ "cats-core"
    val              guava =           "com.google.guava"  ^ "guava"
    val seqdoop_hadoop_bam =               ("org.seqdoop"  ^ "hadoop-bam") - hadoop
    val             htsjdk =       ("com.github.samtools"  ^ "htsjdk") - ("org.xerial.snappy" ^ "snappy-java")
    val            kittens =              "org.typelevel" ^^ "kittens"
    val              log4j =                  "org.slf4j"  ^ "slf4j-log4j12"
    val       parquet_avro =         "org.apache.parquet"  ^ "parquet-avro"
    val            purecsv =         "com.github.melrief" ^^ "purecsv"
    val          scalatags =                "com.lihaoyi" ^^ "scalatags"
    val         scalautils =             "org.scalautils" ^^ "scalautils"
    val          shapeless =                "com.chuusai" ^^ "shapeless"
    val              slf4j =                "org.clapper" ^^ "grizzled-slf4j"
    val         sourcecode =                "com.lihaoyi" ^^ "sourcecode"
    val              spire =              "org.typelevel" ^^ "spire"

    object bdg {
      object adam {
        val core = "org.bdgenomics.adam" ^^ "adam-core"
      }
      val formats = "org.bdgenomics.bdg-formats" ^ "bdg-formats"
      object quinine {
        val core = ("org.bdgenomics.quinine" ^^ "quinine-core") - adam.core
      }
      object utils
        extends Libs(
          "org.bdgenomics.utils" ^^ "utils" ^ "0.2.13",
          (prefix, name) ⇒ s"$prefix-$name-spark2"
        )
      {
        val intervalrdd = lib
        val          io = lib
        val     metrics = lib
        val        misc = lib
      }
      val defaults =
        Seq(
          versions(
            formats → "0.10.1"
          )
        ) ++
        utils.defaults
    }

    object commons {
      val   io =         "commons-io" ^    "commons-io"
      val math = "org.apache.commons" ^ "commons-math3"
      val defaults =
        versions(
            io → "2.5",
          math → "3.6.1"
        )
    }

    object circe extends Libs("io.circe" ^^ "circe" ^ "0.9.3") {
      val generic = lib
      val literal = lib
      val    core = lib
      val  parser = lib
    }

    object http4s extends Libs("org.http4s" ^^ "http4s" ^ "0.18.11") {
      val `blaze-server` = lib
      val `blaze-client` = lib
      val         circe  = lib
      val           dsl  = lib
    }
  }

  import autoImport._

  override def projectSettings =
    Seq(
      versions ++= Seq(
        args4j             → "2.33",
        autowire           → "0.2.6",
        boopickle          → "1.3.0",
        breeze             → "0.13.2",
        cats               → "1.0.1",
        case_app           → "2.0.0-M3",
        guava              → "19.0",
        htsjdk             → "2.9.1",
        kittens            → "1.0.0-RC2",
        log4j              → "1.7.21",
        parquet_avro       → "1.8.1",
        purecsv            → "0.1.1",
        scalatags          → "0.6.7",
        scalautils         → "2.1.5",
        seqdoop_hadoop_bam → "7.9.0",
        shapeless          → "2.3.3",
        slf4j              → "1.3.1",
        sourcecode         → "0.1.4",
        spire              → "0.15.0"
      ),

      commons.defaults
    ) ++
    bdg.defaults ++
    circe.defaults ++
    http4s.defaults
}
