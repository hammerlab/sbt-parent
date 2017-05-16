package org.hammerlab.sbt.plugin

import org.hammerlab.sbt.Util.prop
import org.hammerlab.sbt.plugin.Deps.autoImport.{ providedDeps, testDeps }
import sbt.Keys._
import sbt._

object Spark
  extends Plugin(Deps) {

  object autoImport {
    val sparkTestingBaseVersion = settingKey[String]("Version of holdenk/spark-testing-base to use")

    val sparkVersion = settingKey[String]("Spark version to use")
    val spark1Version = settingKey[String]("When cross-building for Spark 1.x and 2.x, this version will be used when -Dspark1 is set.")
    val computedSparkVersion = settingKey[String]("Spark version to use, taking in to account 'spark.version' and 'spark1' env vars")

    val hadoopVersion = settingKey[String]("Hadoop version to use")
    val computedHadoopVersion = settingKey[String]("Hadoop version to use, taking in to account the 'hadoop.version' env var")

    val crossSpark1Deps = settingKey[Seq[ModuleID]]("Deps whose artifact-names should have a \"-spark1\" appended to them when building against the Spark 1.x line")
    val crossSpark2Deps = settingKey[Seq[ModuleID]]("Deps whose artifact-names should have a \"-spark2\" appended to them when building against the Spark 2.x line")

    val isSpark1 = settingKey[Boolean]("True when sparkVersion starts with '1'")
    val isSpark2 = settingKey[Boolean]("True when sparkVersion starts with '2'")

    val spark = settingKey[ModuleID]("Spark dependency")
    val hadoop = settingKey[ModuleID]("Hadoop dependency")
    val sparkTestingBase = settingKey[ModuleID]("com.holdenkarau:spark-testing-base dependency")
    val sparkTests = settingKey[ModuleID]("org.hammerlab:spark-tests dependency")
    val sparkTestsVersion = settingKey[String]("org.hammerlab:spark-tests version")
    val kryo = settingKey[ModuleID]("Kryo dependency")

    // Helper for appending "_spark1" to a project's name iff the "spark1" env var is set.
    def sparkName(name: String): String =
      prop("spark1") match {
        case Some(_) ⇒ s"${name}_spark1"
        case None ⇒ name
      }

    val addSparkDeps: SettingsDefinition = (
      Seq(
        providedDeps ++= (
          Seq(
            spark.value,
            hadoop.value
          )
        ),
        testDeps += sparkTests.value,
        libraryDependencies += kryo.value
      )
    )
  }

  import autoImport._

  override def projectSettings: Seq[Def.Setting[_]] =
    Seq(
      spark :=
        "org.apache.spark" %%
          "spark-core" %
          computedSparkVersion.value
          exclude("org.scalatest", s"scalatest_${scalaBinaryVersion.value}"),

      hadoop := "org.apache.hadoop" % "hadoop-client" % computedHadoopVersion.value,

      sparkTests :=
        "org.hammerlab" %%
          "spark-tests" %
          sparkTestsVersion.value
          exclude("org.apache.hadoop", "hadoop-client"),

      sparkTestsVersion := "1.3.6-SNAPSHOT",

      sparkTestingBase :=
        "com.holdenkarau" %%
          "spark-testing-base" %
          sparkTestingBaseVersion.value
          exclude("org.scalatest", s"scalatest_${scalaBinaryVersion.value}"),

      // Better than Spark's 2.21, which ill-advisedly shades in some minlog classes.
      kryo := "com.esotericsoftware.kryo" % "kryo" % "2.24.0",

      isSpark1 := computedSparkVersion.value(0) == '1',
      isSpark2 := computedSparkVersion.value(0) == '2',

      crossSpark1Deps := Seq(),
      crossSpark2Deps := Seq(),

      libraryDependencies ++= crossSpark1Deps.value.map(
        dep ⇒
          if (isSpark1.value)
            dep.copy(name = dep.name + "_spark1")
          else
            dep
      ),

      libraryDependencies ++= crossSpark2Deps.value.map(
        dep ⇒
          if (isSpark2.value)
            dep.copy(name = dep.name + "_spark2")
          else
            dep
      ),

      sparkTestingBaseVersion := {
        if (scalaBinaryVersion.value == "2.10" && isSpark2.value)
        // spark-testing-base topped out at Spark 2.0.0 for Scala 2.10.
          "2.0.0_0.5.0"
        else
          s"${computedSparkVersion.value}_0.5.0"
      },

      sparkVersion := "2.1.0",
      spark1Version := "1.6.3",

      computedSparkVersion := (
        prop("spark.version")
        .orElse(
          prop("spark1").map(_ ⇒ spark1Version.value)
        )
        .getOrElse(
          sparkVersion.value
        )
        ),

      hadoopVersion := "2.7.3",
      computedHadoopVersion := System.getProperty("hadoop.version", hadoopVersion.value),

      // SparkContexts play poorly with parallel test-execution
      parallelExecution in sbt.Test := false,

      // This trans-dep creates a mess in Spark+Hadoop land; just exclude it everywhere by default.
      excludeDependencies += SbtExclusionRule("javax.servlet", "servlet-api")
    )
}
