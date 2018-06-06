package org.hammerlab.sbt.plugin

import org.hammerlab.sbt.deps.CrossVersion.BinaryJS
import org.hammerlab.sbt.deps.{ Dep, Group }
import org.hammerlab.sbt.plugin.    Deps.autoImport.testDeps
import org.hammerlab.sbt.plugin.  GitHub.autoImport._
import org.hammerlab.sbt.plugin.   Maven.autoImport._
import org.hammerlab.sbt.plugin.  Parent.autoImport._
import org.hammerlab.sbt.plugin.   Spark.autoImport.hadoop
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

  trait all {
    def lib(name: String) = "org.hammerlab" ^^ name
    def lib(subgroup: String, name: String) = s"org.hammerlab.$subgroup" ^^ name

    val            adam = lib("adam", "core")
    val      args4j_cli = lib("cli", "args4j")
    val          args4s = lib("args4s")
    val   bdg_utils_cli = lib("bdg-utils", "cli")
    val           bytes = lib("bytes")
    val        case_cli = lib("cli", "case-app")
    val         channel = lib("channel")
    val        io_utils = lib("io")
    val      magic_rdds = lib("magic-rdds")
    val        parallel = lib("parallel")
    val           paths = lib("paths")
    val shapeless_utils = lib("shapeless-utils")
    val       spark_bam = lib("bam", "load")
    val      spark_util = lib("spark-util")
    val           stats = lib("math", "stats")
    val    string_utils = lib("string-utils")
    val       testSuite = lib("test", "suite")
    val       testUtils = lib("test", "base")
    val           types = lib("types")

    object cli {
      val  base = lib("cli",  "base")
      val spark = lib("cli", "spark")
    }

    object genomics {
      val      loci = lib("genomics",      "loci") - guava
      val     reads = lib("genomics",     "reads")
      val  readsets = lib("genomics",  "readsets")
      val reference = lib("genomics", "reference")
      val     utils = lib("genomics",     "utils")
    }

    val iterators =
      new Dep("org.hammerlab", "iterator", BinaryJS) {
        val macros = lib("macros", "iterators")
      }

    object math {
      val    format = lib("math",    "format")
      val tolerance = lib("math", "tolerance")
      val     utils = lib("math",     "utils")
    }

    val scalatestOnly = addTestLib := false
    val clearTestDeps = Seq(
      addTestLib := false,
      testDeps := Nil
    )
  }

  object autoImport extends all {
    val testSuiteVersion = settingKey[String]("Version of org.hammerlab.test:suite test-dep to use")
    val testUtilsVersion = settingKey[String]("Version of org.hammerlab.test:base test-dep to use")

    object hammerlab extends all {

      def apply(name: String) = lib(name)
      def apply(subgroup: String, name: String) = lib(subgroup, name)

      val hadoop_bam = ("org.hammerlab" ^ "hadoop-bam") - hadoop

      val io = io_utils

      object test {
        object suite {
          val version = testSuiteVersion
        }
        implicit def toVersion(t: suite.type): Dep = testSuite

        object base {
          val version = testUtilsVersion
        }
        implicit def toVersion(t: base.type): Dep = testUtils
      }
    }
  }

  val addTestLib = settingKey[Boolean]("When false, skip adding org.hammerlab.test:{base,suite} as a test-dependency (which this plugin otherwise does by default")

  import autoImport._

  override def globalSettings =
    Seq(
      organization := "org.hammerlab",
      sonatypeStagingPrefix := Some("orghammerlab"),

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

      testSuiteVersion := "1.0.1",
      testUtilsVersion := "1.0.1",
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
        testSuite → testSuiteVersion.value,
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
