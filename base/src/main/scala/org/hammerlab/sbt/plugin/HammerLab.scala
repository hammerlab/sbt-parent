package org.hammerlab.sbt.plugin

import org.hammerlab.sbt.deps.CrossVersion.BinaryJS
import org.hammerlab.sbt.deps.{ Dep, Group, GroupArtifact }
import org.hammerlab.sbt.plugin.Deps.autoImport.testDeps
import org.hammerlab.sbt.plugin.GitHub.autoImport._
import org.hammerlab.sbt.plugin.Parent.autoImport._
import org.hammerlab.sbt.plugin.Spark.autoImport.hadoop
import org.hammerlab.sbt.plugin.Versions.autoImport.versions
import org.scalajs.sbtplugin.ScalaJSPlugin.autoImport.isScalaJSProject
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

    val           adam = hammerlab("adam", "core")
    val     args4j_cli = hammerlab("cli", "args4j")
    val         args4s = hammerlab("args4s")
    val  bdg_utils_cli = hammerlab("bdg-utils", "cli")
    val          bytes = hammerlab("bytes")
    val       case_cli = hammerlab("cli", "case-app")
    val        channel = hammerlab("channel")
    val        io_utils = hammerlab("io")
    val      magic_rdds = hammerlab("magic-rdds")
    val        parallel = hammerlab("parallel")
    val           paths = hammerlab("paths")
    val shapeless_utils = hammerlab("shapeless-utils")
    val       spark_bam = hammerlab("bam", "load")
    val      spark_util = hammerlab("spark-util")
    val           stats = hammerlab("math", "stats")
    val    string_utils = hammerlab("string-utils")
    val       testSuite = hammerlab("test", "suite")
    val       testUtils = hammerlab("test", "base")
    val           types = hammerlab("types")

    object genomics {
      val      loci = hammerlab("genomics",      "loci") - guava
      val     reads = hammerlab("genomics",     "reads")
      val  readsets = hammerlab("genomics",  "readsets")
      val reference = hammerlab("genomics", "reference")
      val     utils = hammerlab("genomics",     "utils")
    }

    object hammerlab {
      val hadoop_bam = ("org.hammerlab" ^ "hadoop-bam") - hadoop
    }

    val iterators =
      new Dep("org.hammerlab", "iterator", BinaryJS) {
        val macros = hammerlab("macros", "iterators")
      }

    object math {
      val    format = hammerlab("math",    "format")
      val tolerance = hammerlab("math", "tolerance")
      val     utils = hammerlab("math",     "utils")
    }

    val scalatestOnly = addTestLib := false
    val clearTestDeps = Seq(
      addTestLib := false,
      testDeps := Nil
    )
  }

  val addTestLib = settingKey[Boolean]("")

  import autoImport._

  override def globalSettings =
    Seq(
      organization := "org.hammerlab",
      apache2,

      githubUser := Some("hammerlab"),

      developers :=
        List(
          Developer(
            id    = "hammerlab",
            name  = "Hammer Lab",
            email = "info@hammerlab.org",
            url   = "https://github.com/hammerlab"
          )
        ),

      testUtilsVersion := "1.0.0",
      addTestLib := true
    )

  override def projectSettings =
    Seq(
      /**
       * All org.hammerlab* repos are published with this Sonatype profile
       *
       * Must be defined here instead of [[globalSettings]] because it is originally only defined in
       * [[projectSettings]] (in [[xerial.sbt.Sonatype]])
       */
      sonatypeProfileName := (
        if (organization.value.startsWith("org.hammerlab"))
          "org.hammerlab"
        else
          sonatypeProfileName.value
      ),

      versions ++= Seq(
        testSuite → testUtilsVersion.value,
        testUtils → testUtilsVersion.value
      ),

      /**
       * This would ideally be a global-setting, so that it would be obviated in projects that declare e.g.:
       *
       * {{{
       * default(testDeps := Seq(scalatest)
       * }}}
       *
       * However, [[org.scalajs.sbtplugin.ScalaJSPlugin.autoImport.isScalaJSProject]] only gets initialized at the
       * project level, so reading it in global scope always returns `false`.
       */
      testDeps ++= (
        if (addTestLib.value)
          Seq(
            if (isScalaJSProject.value)
              testSuite
            else
              testUtils
          )
        else
          Nil
      )
    )
}
