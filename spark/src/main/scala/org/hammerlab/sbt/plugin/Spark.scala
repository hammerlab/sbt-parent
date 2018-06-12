package org.hammerlab.sbt.plugin

import org.hammerlab.sbt.dsl
import org.hammerlab.sbt.deps.Dep
import org.hammerlab.sbt.deps.Group._
import org.hammerlab.sbt.plugin.Deps.autoImport.deps
import org.hammerlab.sbt.plugin.Scala.autoImport._
import org.hammerlab.sbt.plugin.Test.autoImport.scalatest
import org.hammerlab.sbt.plugin.Versions.DefaultVersion
import org.hammerlab.sbt.plugin.Versions.autoImport.versions
import sbt.Keys.{ excludeDependencies, parallelExecution }
import sbt._
import sbt.librarymanagement.syntax.ExclusionRule

object Spark
  extends Plugin(
    Deps,
    Scala,
    Versions
  ) {

  object autoImport {
    object spark extends dsl.Dep(spark.core) {
      /**
       * Add Spark dependencies and set the spark version
       */
      def apply(v: String): SettingsDefinition = Seq(version := v) ++ settings
      def apply(): SettingsDefinition = settings

      def lib(name: String) = ("org.apache.spark" ^^ s"spark-$name") - scalatest

      val   core = lib(  "core")
      val graphx = lib("graphx")
      val  mllib = lib( "mllib")
      val    sql = lib(   "sql")

      object tests {
        val dep = ("org.hammerlab" ^^ "spark-tests") - hadoop
        val version = SettingKey[String]("sparkTestsVersion", "Version of org.hammerlab::spark-tests to use")
      }
      implicit def testsDep(t: tests.type): Dep = t.dep

      /**
       * Add Spark dependencies and set the Scala version to 2.11.x
       */
      val settings: SettingsDefinition =
        `2.11`.only ++
          Seq(
            deps ++=
              Seq(
                spark.core provided,
                spark.tests.dep tests,
                hadoop.dep provided,
                kryo.dep
              ),

            // This trans-dep creates a mess in Spark+Hadoop land; just exclude it everywhere by default.
            excludeDependencies += ExclusionRule("javax.servlet", "servlet-api")
          )
    }
    implicit def sparkDep(s: spark.type): Dep = spark.core
    implicit def sparkSettings(s: spark.type): SettingsDefinition = spark.settings

    object hadoop {
      val dep = "org.apache.hadoop" ^ "hadoop-client"
      val version = SettingKey[String]("hadoopVersion", "Hadoop version to use")
    }
    implicit def hadoopDep(h: hadoop.type): Dep = h.dep

    object kryo {
      val dep = "com.esotericsoftware.kryo" ^ "kryo"
      val version = SettingKey[String]("kryoVersion", "Version of kryo to use")
    }
    implicit def kryoDep(k: kryo.type): Dep = k.dep
  }

  private val computedSparkVersion = settingKey[String]("Spark version to use, taking in to account 'spark.version' and 'spark1' env vars")
  private val computedHadoopVersion = settingKey[String]("Hadoop version to use, taking in to account the 'hadoop.version' env var")

  import autoImport._

  override def globalSettings =
    Seq(

      versions ++= Seq[DefaultVersion](
          hadoop.   dep → computedHadoopVersion.value,
            kryo.   dep → kryo.version         .value,
        spark.  core    → computedSparkVersion .value,
        spark.graphx    → computedSparkVersion .value,
        spark. mllib    → computedSparkVersion .value,
        spark.   sql    → computedSparkVersion .value,
        spark.tests.dep → spark.tests.version  .value
      ),

      kryo.version := "2.24.0",
      spark.tests.version := "2.3.1",

      hadoop.version := "2.7.3",
      computedHadoopVersion := System.getProperty("hadoop.version", hadoop.version.value),

      spark.version := "2.2.1",
      computedSparkVersion := System.getProperty("spark.version", spark.version.value),

      // SparkContexts play poorly with parallel test-execution
      parallelExecution in sbt.Test := false
    )
}
