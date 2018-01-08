package org.hammerlab.sbt.plugin

import org.hammerlab.sbt.deps.Group
import org.hammerlab.sbt.plugin.Deps.autoImport.testDeps
import org.hammerlab.sbt.plugin.GitHub.autoImport._
import org.hammerlab.sbt.plugin.Parent.autoImport._
import org.hammerlab.sbt.plugin.Spark.autoImport.hadoop
import org.hammerlab.sbt.plugin.Versions.autoImport.versions
import sbt.Keys._
import sbt._
import xerial.sbt.Sonatype.SonatypeKeys.sonatypeProfileName

object HammerLab
  extends Plugin(
    GitHub,
    Maven,
    Versions,
    Test
  ) {

  import Group._

  implicit def liftOption[T](t: T): Option[T] = Some(t)

  object autoImport {
    val testUtilsVersion = settingKey[String]("Version of org.hammerlab:test_utils test-dep to use")

    def hammerlab(name: String) = "org.hammerlab" ^^ name
    def hammerlab(subgroup: String, name: String) = s"org.hammerlab.$subgroup" ^^ name

    val adam = hammerlab("adam", "core")
    val args4j_cli = hammerlab("cli", "args4j")
    val args4s = hammerlab("args4s")
    val bdg_utils_cli = hammerlab("bdg-utils", "cli")
    val bytes = hammerlab("bytes")
    val case_cli = hammerlab("cli", "case-app")
    val channel = hammerlab("channel")
    val genomic_utils = hammerlab("genomics", "utils")
    val hammerlab_hadoop_bam = ("org.hammerlab" ^ "hadoop-bam") - hadoop
    val io_utils = hammerlab("io")
    val iterators = hammerlab("iterator")
    val iterator_macros = hammerlab("iterator-macros")
    val loci = hammerlab("genomics", "loci") - guava
    val magic_rdds = hammerlab("magic-rdds")
    val math = hammerlab("math")
    val paths = hammerlab("paths")
    val reads = hammerlab("genomics", "reads")
    val readsets = hammerlab("genomics", "readsets")
    val reference = hammerlab("genomics", "reference")
    val shapeless_utils = hammerlab("shapeless-utils")
    val spark_bam = hammerlab("bam", "load")
    val spark_util = hammerlab("spark-util")
    val stats = hammerlab("stats")
    val string_utils = hammerlab("string-utils")
    val testUtils = hammerlab("test-utils")
    val types = hammerlab("types")
  }

  import autoImport._

  override def projectSettings: Seq[Def.Setting[_]] =
    Seq(
      organization in Global := "org.hammerlab",
      apache2,

      github.user("hammerlab"),

      // All org.hammerlab* repos are published with this Sonatype profile.
      sonatypeProfileName := (
        if (organization.value.startsWith("org.hammerlab"))
          "org.hammerlab"
        else
          sonatypeProfileName.value
      ),

      developers :=
        List(
          Developer(
            id    = "HammerLab",
            name  = "Hammer Lab",
            email = "info@hammerlab.org",
            url   = "https://github.com/hammerlab"
          )
        ),

      testUtilsVersion := "1.5.1",
      versions += testUtils → testUtilsVersion.value,
      testDeps in Global += testUtils
    )
}
