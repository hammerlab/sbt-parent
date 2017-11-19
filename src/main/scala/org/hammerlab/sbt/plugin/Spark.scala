package org.hammerlab.sbt.plugin

import org.hammerlab.sbt.deps.Configuration.Provided
import org.hammerlab.sbt.deps.Group._
import org.hammerlab.sbt.deps.{ Configuration ⇒ Conf }
import org.hammerlab.sbt.plugin.Deps.autoImport.deps
import org.hammerlab.sbt.plugin.Test.autoImport.scalatest
import org.hammerlab.sbt.plugin.Versions.autoImport.versions
import sbt.Keys.{ excludeDependencies, parallelExecution }
import sbt.{ Def, SbtExclusionRule, SettingsDefinition, settingKey }

object Spark
  extends Plugin(Deps) {

  object autoImport {
    val sparkVersion = settingKey[String]("Spark version to use")
    val hadoopVersion = settingKey[String]("Hadoop version to use")
    val kryoVersion = settingKey[String]("Kryo version to use")
    val sparkTestsVersion = settingKey[String]("org.hammerlab:spark-tests version to use")

    val spark = ("org.apache.spark" ^^ "spark-core") - scalatest
    val hadoop = "org.apache.hadoop" ^ "hadoop-client"
    val sparkTests = ("org.hammerlab" ^^ "spark-tests") - hadoop
    val kryo = "com.esotericsoftware.kryo" ^ "kryo"

    val addSparkDeps: SettingsDefinition =
      Seq(
        deps ++=
          Seq(
            spark % Provided,
            hadoop % Provided,
            kryo,
            sparkTests % Conf.Test
          ),

        // This trans-dep creates a mess in Spark+Hadoop land; just exclude it everywhere by default.
        excludeDependencies += SbtExclusionRule("javax.servlet", "servlet-api")

      )
  }

  private val computedSparkVersion = settingKey[String]("Spark version to use, taking in to account 'spark.version' and 'spark1' env vars")
  private val computedHadoopVersion = settingKey[String]("Hadoop version to use, taking in to account the 'hadoop.version' env var")

  import autoImport._

  override def projectSettings: Seq[Def.Setting[_]] =
    Seq(

      versions ++=
        Seq(
          hadoop → computedHadoopVersion.value,
          spark → computedSparkVersion.value,
          kryo → kryoVersion.value,
          sparkTests → sparkTestsVersion.value
        ),

      kryoVersion := "2.24.0",
      sparkTestsVersion := "2.3.0",

      hadoopVersion := "2.7.3",
      computedHadoopVersion := System.getProperty("hadoop.version", hadoopVersion.value),

      sparkVersion := "2.2.0",
      computedSparkVersion := System.getProperty("spark.version", sparkVersion.value),

      // SparkContexts play poorly with parallel test-execution
      parallelExecution in sbt.Test := false
    )
}
