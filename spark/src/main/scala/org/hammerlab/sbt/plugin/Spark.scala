package org.hammerlab.sbt.plugin

import org.hammerlab.sbt.dsl.Dep
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

  private def lib(implicit name: sourcecode.Name) = ("org.apache.spark" ^^ s"spark-${name.value}") - scalatest
  val core = lib

  object autoImport {
    object spark extends Dep(core ^ "2.2.1") {
      /**
       * Add Spark dependencies and set the spark version
       */
      def apply(v: String): SettingsDefinition = Seq(version := v) ++ settings
      def apply(): SettingsDefinition = settings

      val   core = dep
      val graphx = lib
      val  mllib = lib
      val    sql = lib

      object tests extends Dep(("org.hammerlab" ^^ "spark-tests" ^ "2.3.1") - hadoop)

      /**
       * Add Spark dependencies and set the Scala version to 2.11.x
       */
      override val settings: SettingsDefinition =
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

    object hadoop extends Dep("org.apache.hadoop" ^ "hadoop-client" ^ "2.7.3")
    object   kryo extends Dep("com.esotericsoftware.kryo" ^ "kryo" ^ "2.24.0")
  }

  private val computedSparkVersion  = settingKey[String]( "Spark version to use, taking in to account 'spark.version' system property")
  private val computedHadoopVersion = settingKey[String]("Hadoop version to use, taking in to account the 'hadoop.version' system property")

  import autoImport._

  override def globalSettings =
    Seq(

      versions ++= Seq[DefaultVersion](
        spark.  core → computedSparkVersion .value,
        spark.graphx → computedSparkVersion .value,
        spark. mllib → computedSparkVersion .value,
        spark.   sql → computedSparkVersion .value,
        spark. tests → spark.tests. version .value
      ),

      computedHadoopVersion := System.getProperty("hadoop.version", hadoop.version.value),

      computedSparkVersion := System.getProperty("spark.version", spark.version.value),

      // SparkContexts play poorly with parallel test-execution
      parallelExecution in sbt.Test := false
    ) ++
    kryo.defaults ++
    spark.tests.defaults ++
    hadoop.defaults ++
    spark.defaults
}
