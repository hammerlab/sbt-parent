package org.hammerlab.sbt.plugin

import org.hammerlab.sbt.deps.Group

object Parent
  extends Plugin(
    Assembly,
    Maven,
    Scalariform,
    Spark,
    Test,
    Travis,
    Versions
  ) {

  import Group._

  object autoImport {
    val adam = "org.hammerlab.adam" ^^ "core"
    val args4j = "args4j" ^ "args4j"
    val args4s = "org.hammerlab" ^ "args4s"
    val bdg_formats = "org.bdgenomics.bdg-formats" ^ "bdg-formats"
    val bdg_utils_cli = "org.hammerlab.bdg-utils" ^^ "cli"
    val bdg_utils_intervalrdd = "org.bdgenomics.utils" ^^ "utils-intervalrdd"
    val bdg_utils_io = "org.bdgenomics.utils" ^^ "utils-io"
    val bdg_utils_metrics = "org.bdgenomics.utils" ^^ "utils-metrics"
    val bdg_utils_misc = "org.bdgenomics.utils" ^^ "utils-misc"
    val breeze = "org.scalanlp" ^^ "breeze"
    val commons_io = "commons-io" ^ "commons-io"
    val commons_math = "org.apache.commons" ^ "commons-math3"
    val genomic_utils = "org.hammerlab.genomics" ^^ "utils"
    val hadoop_bam = ("org.seqdoop" ^ "hadoop-bam") - ("org.apache.hadoop" ^ "hadoop-client")
    val htsjdk = ("com.github.samtools" ^ "htsjdk") - ("org.xerial.snappy" ^ "snappy-java")
    val iterators = "org.hammerlab" ^^ "iterator"
    val loci = ("org.hammerlab.genomics" ^^ "loci") - ("com.google.guava" ^ "guava")
    val log4j = "org.slf4j" ^ "slf4j-log4j12"
    val magic_rdds = "org.hammerlab" ^^ "magic-rdds"
    val mllib = ("org.apache.spark" ^^ "spark-mllib") - ("org.scalatest" ^^ "scalatest")
    val paths = "org.hammerlab" ^^ "paths"
    val parquet_avro = "org.apache.parquet" ^ "parquet-avro"
    val quinine_core = ("org.bdgenomics.quinine" ^^ "quinine-core") - ("org.bdgenomics.adam" ^^ "adam-core")
    val reads = "org.hammerlab.genomics" ^^ "reads"
    val readsets = "org.hammerlab.genomics" ^^ "readsets"
    val reference = "org.hammerlab.genomics" ^^ "reference"
    val scala_reflect = "org.scala-lang" ^ "scala-reflect"
    val scalautils = "org.scalautils" ^^ "scalautils"
    val slf4j = "org.clapper" ^^ "grizzled-slf4j"
    val spark_commands = "org.hammerlab" ^^ "spark-commands"
    val spark_util = "org.hammerlab" ^^ "spark-util"
    val spire = "org.spire-math" ^^ "spire"
    val string_utils = "org.hammerlab" ^^ "string-utils"
  }

//  implicit def depifyTuple(t: (String, GroupArtifact)): (String, Dep) = (t._1, t._2)

  override def projectSettings: Seq[_root_.sbt.Def.Setting[_]] =
    Seq(
    )
}
