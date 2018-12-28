package org.hammerlab.sbt.plugin

import org.hammerlab.sbt.deps.Group._
import org.hammerlab.sbt.dsl.{ Lib, Libs }
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
    object spark extends Libs(("org.apache.spark" ^^ "spark" ^ "2.4.0") - scalatest - ("org.slf4j"  ^ "slf4j-log4j12")) {
      /**
       * Add Spark dependencies and set the spark version
       */
      def apply(v: String): SettingsDefinition = Seq(version := v) ++ settings
      def apply(): SettingsDefinition = settings

      val   core = lib
      val graphx = lib
      val  mllib = lib
      val    sql = lib

      object tests extends Lib(("org.hammerlab" ^^ "spark-tests" ^ "2.3.4") - hadoop)

      /**
       * Add Spark dependencies and set the Scala version to 2.11.x
       */
      override val settings: SettingsDefinition =
        Seq(
          Deps.autoImport.dep(
            spark.core provided,
            spark.tests tests,
            hadoop provided,
            kryo
          ),

          // This trans-dep creates a mess in Spark+Hadoop land; just exclude it everywhere by default.
          excludeDependencies += ExclusionRule("javax.servlet", "servlet-api")
        )
    }

    object hadoop extends Lib("org.apache.hadoop" ^ "hadoop-client" ^ "2.7.3")
    object   kryo extends Lib("com.esotericsoftware.kryo" ^ "kryo" ^ "2.24.0")
  }

  private val computedSparkVersion  = settingKey[String]( "Spark version to use, taking in to account 'spark.version' system property")
  private val computedHadoopVersion = settingKey[String]("Hadoop version to use, taking in to account the 'hadoop.version' system property")

  import autoImport._

  override def globalSettings =
    Seq(

      versions ++= Seq[DefaultVersion](
        spark.  core → computedSparkVersion.value,
        spark.graphx → computedSparkVersion.value,
        spark. mllib → computedSparkVersion.value,
        spark.   sql → computedSparkVersion.value
      ),

      computedHadoopVersion := System.getProperty("hadoop.version", hadoop.version.value),
      computedSparkVersion  := System.getProperty( "spark.version",  spark.version.value),

      // SparkContexts play poorly with parallel test-execution
      parallelExecution in sbt.Test := false
    ) ++
           kryo.global ++
    spark.tests.global ++
         hadoop.global ++
          spark.global

  override def projectSettings =
           kryo.project ++
    spark.tests.project ++
         hadoop.project ++
          spark.project
}
