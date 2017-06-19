package org.hammerlab.sbt.plugin

import org.hammerlab.sbt.plugin.Spark.autoImport.computedSparkVersion
import sbt.Keys._
import sbt._

object Parent
  extends Plugin(
    Assembly,
    Deps,
    Maven,
    Scalariform,
    Spark,
    Test,
    Travis
  ) {

  object autoImport {
    val libs = settingKey[Map[Symbol, ModuleID]]("Some common dependencies/versions")
    val bdgUtilsVersion = settingKey[String]("org.bdgenomics.utils version to use")
  }

  import autoImport._

  override def projectSettings: Seq[_root_.sbt.Def.Setting[_]] =
    Seq(
      bdgUtilsVersion := "0.2.13",

      libs := {
        Map(
          'adam_core -> "org.hammerlab.adam" %% "core" % "0.23.1",
          'args4j -> "args4j" % "args4j" % "2.33",
          'args4s -> "org.hammerlab" % "args4s" % "1.2.3",
          'bdg_formats -> "org.bdgenomics.bdg-formats" % "bdg-formats" % "0.10.1",
          'bdg_utils_cli -> "org.hammerlab.bdg-utils" %% "cli" % "0.2.15",
          'bdg_utils_intervalrdd -> "org.bdgenomics.utils" %% "utils-intervalrdd" % bdgUtilsVersion.value,
          'bdg_utils_io -> "org.bdgenomics.utils" %% "utils-io" % bdgUtilsVersion.value,
          'bdg_utils_metrics -> "org.bdgenomics.utils" %% "utils-metrics" % bdgUtilsVersion.value,
          'bdg_utils_misc -> "org.bdgenomics.utils" %% "utils-misc" % bdgUtilsVersion.value,
          'breeze -> "org.scalanlp" %% "breeze" % "0.12",
          'commons_io -> "commons-io" % "commons-io" % "2.5",
          'commons_math -> "org.apache.commons" % "commons-math3" % "3.6.1",
          'genomic_utils -> "org.hammerlab.genomics" %% "utils" % "1.2.3",
          'hadoop_bam -> ("org.seqdoop" % "hadoop-bam" % "7.8.0" exclude("org.apache.hadoop", "hadoop-client")),
          'htsjdk -> ("com.github.samtools" % "htsjdk" % "2.9.1" exclude("org.xerial.snappy", "snappy-java")),
          'iterators -> "org.hammerlab" %% "iterator" % "1.2.2",
          'loci -> ("org.hammerlab.genomics" %% "loci" % "1.5.8" exclude("com.google.guava", "guava")),
          'log4j -> "org.slf4j" % "slf4j-log4j12" % "1.7.21",
          'magic_rdds -> "org.hammerlab" %% "magic-rdds" % "1.4.3",
          'mllib -> ("org.apache.spark" %% "spark-mllib" % computedSparkVersion.value exclude("org.scalatest", s"scalatest_${scalaBinaryVersion.value}")),
          'paths -> "org.hammerlab" %% "paths" % "1.1.0",
          'parquet_avro -> "org.apache.parquet" % "parquet-avro" % "1.8.1",
          'quinine_core -> ("org.bdgenomics.quinine" %% "quinine-core" % "0.0.2" exclude("org.bdgenomics.adam", "adam-core")),
          'reads -> "org.hammerlab.genomics" %% "reads" % "1.0.5",
          'readsets -> "org.hammerlab.genomics" %% "readsets" % "1.0.6",
          'reference -> "org.hammerlab.genomics" %% "reference" % "1.3.0",
          'scala_reflect -> "org.scala-lang" % "scala-reflect" % scalaVersion.value,
          'scalautils -> "org.scalautils" %% "scalautils" % "2.1.5",
          'slf4j -> "org.clapper" %% "grizzled-slf4j" % "1.3.0",
          'spark_commands -> "org.hammerlab" %% "spark-commands" % "1.0.4",
          'spark_util -> "org.hammerlab" %% "spark-util" % "1.1.2",
          'spire -> "org.spire-math" %% "spire" % "0.13.0",
          'string_utils -> "org.hammerlab" %% "string-utils" % "1.2.0"
        )
      }
    )
}
