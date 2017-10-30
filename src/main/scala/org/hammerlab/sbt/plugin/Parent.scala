package org.hammerlab.sbt.plugin

import org.hammerlab.sbt.deps.Group
import org.hammerlab.sbt.plugin.Spark.autoImport.{ hadoop, sparkVersion }
import org.hammerlab.sbt.plugin.Test.autoImport.scalatest
import org.hammerlab.sbt.plugin.Versions.autoImport.versions
import sbt.settingKey

object Parent
  extends Plugin(Versions) {

  import Group._

  object autoImport {

    def hammerlab(name: String) = "org.hammerlab" ^^ name
    def hammerlab(subgroup: String, name: String) = s"org.hammerlab.$subgroup" ^^ name

    val adam = "org.hammerlab.adam" ^^ "core"
    val args4j = "args4j" ^ "args4j"
    val args4j_cli = "org.hammerlab.cli" ^^ "args4j"
    val args4s = "org.hammerlab" ^^ "args4s"
    val bdg_formats = "org.bdgenomics.bdg-formats" ^ "bdg-formats"
    val bdg_utils_cli = "org.hammerlab.bdg-utils" ^^ "cli"
    val bdg_utils_intervalrdd = "org.bdgenomics.utils" ^^ "utils-intervalrdd-spark2"
    val bdg_utils_io = "org.bdgenomics.utils" ^^ "utils-io-spark2"
    val bdg_utils_metrics = "org.bdgenomics.utils" ^^ "utils-metrics-spark2"
    val bdg_utils_misc = "org.bdgenomics.utils" ^^ "utils-misc-spark2"
    val breeze = "org.scalanlp" ^^ "breeze"
    val bytes = hammerlab("bytes")
    val case_app = "com.github.alexarchambault" ^^ "case-app"
    val case_cli = "org.hammerlab.cli" ^^ "case-app"
    val cats = "org.typelevel" ^^ "cats"
    val channel = hammerlab("channel")
    val commons_io = "commons-io" ^ "commons-io"
    val commons_math = "org.apache.commons" ^ "commons-math3"
    val genomic_utils = "org.hammerlab.genomics" ^^ "utils"
    val guava = "com.google.guava" ^ "guava"
    val hammerlab_hadoop_bam = ("org.hammerlab" ^ "hadoop-bam") - hadoop
    val seqdoop_hadoop_bam = ("org.seqdoop" ^ "hadoop-bam") - hadoop
    val htsjdk = ("com.github.samtools" ^ "htsjdk") - ("org.xerial.snappy" ^ "snappy-java")
    val io = hammerlab("io")
    val iterators = hammerlab("iterator")
    val loci = ("org.hammerlab.genomics" ^^ "loci") - guava
    val log4j = "org.slf4j" ^ "slf4j-log4j12"
    val magic_rdds = hammerlab("magic-rdds")
    val math = hammerlab("math")
    val monoids = hammerlab("monoids")
    val mllib = ("org.apache.spark" ^^ "spark-mllib") - scalatest
    val paths = hammerlab("paths")
    val parquet_avro = "org.apache.parquet" ^ "parquet-avro"
    val quinine_core = ("org.bdgenomics.quinine" ^^ "quinine-core") - ("org.bdgenomics.adam" ^^ "adam-core")
    val reads = "org.hammerlab.genomics" ^^ "reads"
    val readsets = "org.hammerlab.genomics" ^^ "readsets"
    val reference = "org.hammerlab.genomics" ^^ "reference"
    val scalautils = "org.scalautils" ^^ "scalautils"
    val shapeless = "com.chuusai" ^^ "shapeless"
    val slf4j = "org.clapper" ^^ "grizzled-slf4j"
    val spark_bam = hammerlab("bam", "load")
    val spark_util = hammerlab("spark-util")
    val spire = "org.spire-math" ^^ "spire"
    val stats = hammerlab("stats")
    val string_utils = hammerlab("string-utils")

    val bdgUtilsVersion = settingKey[String]("org.bdgenomics.utils version to use")
  }

  import autoImport._

  override def projectSettings: Seq[_root_.sbt.Def.Setting[_]] =
    Seq(
      bdgUtilsVersion := "0.2.13",

      versions ++=
        Seq(
          args4j → "2.33",
          bdg_formats → "0.10.1",
          bdg_utils_intervalrdd → bdgUtilsVersion.value,
          bdg_utils_io → bdgUtilsVersion.value,
          bdg_utils_metrics → bdgUtilsVersion.value,
          bdg_utils_misc → bdgUtilsVersion.value,
          breeze → "0.12",
          cats → "0.9.0",
          commons_io → "2.5",
          commons_math → "3.6.1",
          case_app → "1.2.0-M3",
          guava → "19.0",
          htsjdk → "2.9.1",
          log4j → "1.7.21",
          mllib → sparkVersion.value,
          parquet_avro → "1.8.1",
          scalautils → "2.1.5",
          seqdoop_hadoop_bam → "7.9.0",
          shapeless → "2.3.2",
          slf4j → "1.3.1",
          spire → "0.13.0"
        )
    )
}
