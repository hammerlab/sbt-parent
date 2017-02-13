package org.hammerlab.sbt

import com.typesafe.sbt.SbtScalariform
import org.scoverage.coveralls.CommandSupport
import sbt.Keys._
import sbt.TestFrameworks.ScalaTest
import sbt._
import sbtassembly.AssemblyKeys.assemblyOption
import sbtassembly.{ AssemblyPlugin, PathList }
import sbtassembly.AssemblyPlugin.autoImport.{ MergeStrategy, ShadeRule, assembly, assemblyExcludedJars, assemblyJarName, assemblyMergeStrategy, assemblyShadeRules }
import scoverage.ScoverageKeys.{ coverageEnabled, coverageReport }
import xerial.sbt.Sonatype
import xerial.sbt.Sonatype.SonatypeKeys.sonatypeProfileName

import scala.collection.mutable.ArrayBuffer

object ParentPlugin extends AutoPlugin with CommandSupport {

  import com.typesafe.sbt.SbtScalariform.ScalariformKeys

  import scalariform.formatter.preferences._

  object autoImport {
    val libs = settingKey[Map[Symbol, ModuleID]]("Some common dependencies/versions")

    val scalatestVersion = settingKey[String]("Version of scalatest test-dep to use")

    val sparkTestingBaseVersion = settingKey[String]("Version of holdenk/spark-testing-base to use")

    val sparkVersion = settingKey[String]("Spark version to use")
    val spark1Version = settingKey[String]("When cross-building for Spark 1.x and 2.x, this version will be used when -Dspark1 is set.")
    val computedSparkVersion = settingKey[String]("Spark version to use, taking in to account 'spark.version' and 'spark1' env vars")

    val hadoopVersion = settingKey[String]("Hadoop version to use")
    val computedHadoopVersion = settingKey[String]("Hadoop version to use, taking in to account the 'hadoop.version' env var")

    val bdgUtilsVersion = settingKey[String]("org.bdgenomics.utils version to use")

    val githubUser = settingKey[String]("Github user/org to point to")

    val providedDeps = settingKey[Seq[ModuleID]]("Dependencies to be scoped 'provided'")

    val deps = settingKey[Seq[ModuleID]]("Short-hand for libraryDependencies")
    val testDeps = settingKey[Seq[ModuleID]]("Dependencies to be scoped 'test'")
    val testJarTestDeps = settingKey[Seq[ModuleID]]("Modules whose \"tests\"-qualified artifacts should be test-dependencies")

    val compileAndTestDeps = settingKey[Seq[ModuleID]]("Dependencies to be added as compile-scoped-compile-deps as well as test-scoped-test-deps")

    val shadedDeps = settingKey[Seq[ModuleID]]("When set, the main JAR produced will include these libraries shaded")
    val shadeRenames = settingKey[Seq[(String, String)]]("Shading renames to perform")

    val main = settingKey[String]("Main class; non-Option wrapper for `mainClass`")

    val crossSpark1Deps = settingKey[Seq[ModuleID]]("Deps whose artifact-names should have a \"-spark1\" appended to them when building against the Spark 1.x line")
    val crossSpark2Deps = settingKey[Seq[ModuleID]]("Deps whose artifact-names should have a \"-spark2\" appended to them when building against the Spark 2.x line")

    val isSpark1 = settingKey[Boolean]("True when sparkVersion starts with '1'")
    val isSpark2 = settingKey[Boolean]("True when sparkVersion starts with '2'")

    val isScala210 = settingKey[Boolean]("True iff the Scala binary version is 2.10")
    val isScala211 = settingKey[Boolean]("True iff the Scala binary version is 2.11")
    val isScala212 = settingKey[Boolean]("True iff the Scala binary version is 2.12")

    val assemblyIncludeScala = settingKey[Boolean]("When set, include Scala libraries in the assembled JAR")

    val noCrossPublishing =
      Seq(
        crossScalaVersions := Nil,
        crossPaths := false
      )

    val enableScalariform = (
      SbtScalariform.defaultScalariformSettings ++
        Seq(
          ScalariformKeys.preferences := ScalariformKeys.preferences.value
                                         .setPreference(AlignParameters, true)
                                         .setPreference(CompactStringConcatenation, false)
                                         .setPreference(AlignSingleLineCaseStatements, true)
                                         .setPreference(DoubleIndentClassDeclaration, true)
                                         .setPreference(PreserveDanglingCloseParenthesis, true)
        )
      )

    def propOrElse(keys: String*)(default: String): String =
      prop(keys: _*).getOrElse(default)

    def prop(keys: String*): Option[String] =
      keys
        .flatMap(key ⇒ Option(System.getProperty(key)).toSeq)
        .headOption

    // Helper for appending "_spark1" to a project's name iff the "spark1" env var is set.
    def sparkName(name: String): String =
      prop("spark1") match {
        case Some(_) ⇒ s"${name}_spark1"
        case None ⇒ name
      }

    val travisCoverageScalaVersion = settingKey[String]("Scala version to measure/report test-coverage for")

    // Evaluate these settings to build a "thin" assembly JAR instead of the default and publish it in place of the
    // usual (unshaded) JAR.
    val publishThinShadedJar: SettingsDefinition =
      Seq(
        assemblyExcludedJars in assembly := {
          val log = streams.value.log

          val cp = (fullClasspath in assembly).value

          // Build best-guesses of basenames of JARs corresponding to the deps we want to shade: s"$name-$version.jar".
          val shadedDepJars =
            shadedDeps
              .value
              .map(
                dep => {
                  val crossFn =
                    CrossVersion(
                      dep.crossVersion,
                      scalaVersion.value,
                      scalaBinaryVersion.value
                    )
                    .getOrElse((x: String) => x)

                  val name = crossFn(dep.name)
                  s"$name-${dep.revision}.jar"
                }
              )
              .toSet

          log.debug(s"Looking for jars to shade:\n${shadedDepJars.mkString("\t", "\n\t", "")}")

          // Scan the classpath flagging JARs *to exclude*: all JARs whose basenames don't match our JARs-to-shade list
          // from above.
          cp filter { path => {
            val name = path.data.getName

            val exclude = !shadedDepJars(name)

            if (exclude)
              log.debug(s"Skipping JAR: $name")
            else
              log.debug(s"Shading classes jar: $name")

            exclude
          }}
        },

        assemblyJarName in assembly := {
          val newName = s"${name.value}_${scalaBinaryVersion.value}-${version.value}.jar"
          streams.value.log.debug(s"overwriting assemblyJarName: ${assemblyJarName in assembly value} -> $newName")
          newName
        },

        // Add a classifier to the default (unshaded) JAR.
        artifactClassifier in (Compile, packageBin) := Some("unshaded"),

        // Don't add "unshaded" classifier to -tests JAR.
        artifactClassifier in (Test, packageBin) := None,

        artifact in (Compile, assembly) := {
          // Make the assembly JAR the unclassified artifact.
          (artifact in (Compile, assembly)).value.copy(classifier = None)
        },

        packagedArtifacts := {
          // Don't publish the unshaded JAR.
          val newArtifacts = packagedArtifacts.value.filterKeys(_.classifier != Some("unshaded"))
          streams.value.log.debug(
            s"packagedArtifacts, after removing unshaded JAR:\n${newArtifacts.mkString("\t", "\n\t", "")}"
          )
          newArtifacts
        }
      ) ++
        addArtifact(artifact in (Compile, assembly), assembly)  // Publish the assembly JAR.

    val publishAssemblyJar =
      Seq(
        artifact in (Compile, assembly) := {
          val art = (artifact in (Compile, assembly)).value
          art.copy(`classifier` = Some("assembly"))
        }
      ) ++
        addArtifact(artifact in (Compile, assembly), assembly)

    val addSparkDeps: SettingsDefinition =
      Seq(
        providedDeps ++=
          Seq(
            libs.value('spark),
            libs.value('hadoop)
          ),
        testDeps += libs.value('spark_tests) exclude("org.apache.hadoop", "hadoop-client"),
        libraryDependencies += libs.value('kryo)
      )

    val publishTestJar = (publishArtifact in Test := true)

    val scala210Version = settingKey[String]("Patch version of Scala 2.10.x line to use")
    val scala211Version = settingKey[String]("Patch version of Scala 2.11.x line to use")
    val scala212Version = settingKey[String]("Patch version of Scala 2.12.x line to use")

    val addScala212 = (crossScalaVersions += scala212Version.value)
    val omitScala210 = (crossScalaVersions -= scala210Version.value)

    val scala210Only =
      Seq(
        scalaVersion := scala210Version.value,
        crossScalaVersions := Seq(scala210Version.value)
      )

    val scala211Only =
      Seq(
        scalaVersion := scala211Version.value,
        crossScalaVersions := Seq(scala211Version.value)
      )

    val scala212Only =
      Seq(
        scalaVersion := scala212Version.value,
        crossScalaVersions := Seq(scala212Version.value)
      )

    val takeFirstLog4JProperties =
      assemblyMergeStrategy in assembly := {
        // Two org.bdgenomics deps include the same log4j.properties.
        case PathList("log4j.properties") => MergeStrategy.first
        case x => (assemblyMergeStrategy in assembly).value(x)
      }
  }

  import autoImport._

  /**
   * Helper for running [[Task]]s from within [[Command]]s.
   */
  def runTask[T](taskKey: ScopedKey[Task[T]], state: State): State =
    Project
      .runTask(taskKey, state)
      .map(_._1)
      .getOrElse(state)

  /**
   * Command for building and submitting a coverage report in Travis, *only* for the build corresponding to a specific
   * Scala version (indicated by travisCoverageScalaVersion).
   */
  val travisReportCmd =
    Command.command("travis-report")(
      state ⇒ {
        implicit val iState = state
        val extracted = Project.extract(state)
        implicit val pr = extracted.currentRef
        implicit val bs = extracted.structure

        val actualTravisScalaVersion = System.getenv("TRAVIS_SCALA_VERSION")
        val tcsv = travisCoverageScalaVersion.gimme

        if (actualTravisScalaVersion == tcsv) {
          val nextState = runTask(coverageReport, state)
          Command.process("coveralls", nextState)
        } else {
          log.info(s"Skipping coverage reporting for scala version $actualTravisScalaVersion (reporting enabled for $tcsv)")
          state
        }
      }
    )

  private val commandsToRegister = ArrayBuffer[Command]()

  /**
   * Helper for creating a [[Command]] that runs a given task on a given project as well as all its dependencies (as
   * declared by [[Project.dependsOn]]), in a multi-project setting.
   */
  def makeTransitiveCommand(name: String, task: TaskKey[_]): Unit =
    commandsToRegister +=
      Command.command(name) {
        state ⇒
          val nextState =
            Project
              .extract(state)
              .currentProject
              .dependencies
              .foldLeft(state) {
                (curState, dep) ⇒
                  state.log.info(s"Running task ${task.key.label} in dependency ${dep.project.project}")
                  runTask(task in dep.project, curState)
              }

          runTask(task, nextState)
      }

  makeTransitiveCommand("publishM2Transitive", publishM2)
  makeTransitiveCommand("testTransitive", test in Test)
  makeTransitiveCommand("cleanTransitive", clean)

  override def trigger: PluginTrigger = allRequirements

  override def requires: Plugins = super.requires && AssemblyPlugin && Sonatype

  override def projectSettings: Seq[_root_.sbt.Def.Setting[_]] = {

    // Settings to configure `publish`, `publishM2`, and `publishSigned`.
    val mavenSettings =
      Seq(
        publishTo := {
          val nexus = "https://oss.sonatype.org/"
          if (isSnapshot.value)
            Some("snapshots" at nexus + "content/repositories/snapshots")
          else
            Some("releases" at nexus + "service/local/staging/deploy/maven2")
        },

        publishMavenStyle := true,
        publishArtifact in Test := false,
        pomIncludeRepository := { _ => false },

        pomExtra := {
          val n = name.value
          val user = githubUser.value
          <url>
            https://github.com/{user}/{n}
          </url>
            <licenses>
              <license>
                <name>Apache License</name>
                <url>https://raw.github.com/{user}/{n}/master/LICENSE</url>
                <distribution>repo</distribution>
              </license>
            </licenses>
            <scm>
              <url>git@github.com:{user}/{n}.git</url>
              <connection>scm:git:git@github.com:{user}/{n}.git</connection>
              <developerConnection>scm:git:git@github.com:{user}/{n}.git</developerConnection>
            </scm>
            <developers>
              <developer>
                <id>hammerlab</id>
                <name>Hammer Lab</name>
                <url>https://github.com/{user}</url>
              </developer>
            </developers>
        }
      )

    val sparkSettings =
      Seq(
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
        )
      )

    val testSettings =
      Seq(
        scalatestVersion := "3.0.0",

        sparkTestingBaseVersion := {
          if (scalaBinaryVersion.value == "2.10" && isSpark2.value)
            // spark-testing-base topped out at Spark 2.0.0 for Scala 2.10.
            "2.0.0_0.5.0"
          else
            s"${computedSparkVersion.value}_0.5.0"
        },

        testOptions in Test += Tests.Argument(TestFrameworks.ScalaTest, "-oF"),

        // Only use ScalaTest by default; without this, other frameworks get instantiated and can inadvertently mangle
        // test-command-lines/args/classpaths.
        testFrameworks := Seq(ScalaTest),

        // Implement "deps" as a short-hand for libraryDependencies to add
        deps := Nil,
        libraryDependencies ++= deps.value,

        // Add hammerlab:test-utils and scalatest as test-deps by default.
        testDeps := Seq(
          libs.value('test_utils),
          libs.value('scalatest)
        ),

        testJarTestDeps := Seq(),

        compileAndTestDeps := Seq(),

        // Add any other `testDeps` as test-scoped dependencies.
        libraryDependencies ++= testDeps.value.map(_ % "test"),
        libraryDependencies ++= testJarTestDeps.value.map(_ % "test" classifier("tests")),

        libraryDependencies ++= compileAndTestDeps.value,
        testJarTestDeps ++= compileAndTestDeps.value,

        // SparkContexts play poorly with parallel test-execution.
        parallelExecution in Test := false
      )

    val versionSettings =
      Seq(
        scala210Version := "2.10.6",
        scala211Version := "2.11.8",
        scala212Version := "2.12.1",

        isScala210 := (scalaBinaryVersion.value == "2.10"),
        isScala211 := (scalaBinaryVersion.value == "2.11"),
        isScala212 := (scalaBinaryVersion.value == "2.12"),

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

        hadoopVersion := "2.6.0",
        computedHadoopVersion := System.getProperty("hadoop.version", hadoopVersion.value),

        bdgUtilsVersion := "0.2.11",

        libs := {
          Map(
            'adam_core -> "org.hammerlab.adam" %% "core" % "0.21.1",
            'args4j -> "args4j" % "args4j" % "2.33",
            'args4s -> "org.hammerlab" % "args4s" % "1.1.0",
            'bdg_formats -> "org.bdgenomics.bdg-formats" % "bdg-formats" % "0.10.1",
            'bdg_utils_cli -> "org.bdgenomics.utils" %% "utils-cli" % bdgUtilsVersion.value,
            'bdg_utils_intervalrdd -> "org.bdgenomics.utils" %% "utils-intervalrdd" % bdgUtilsVersion.value,
            'bdg_utils_io -> "org.bdgenomics.utils" %% "utils-io" % bdgUtilsVersion.value,
            'bdg_utils_metrics -> "org.bdgenomics.utils" %% "utils-metrics" % bdgUtilsVersion.value,
            'bdg_utils_misc -> "org.bdgenomics.utils" %% "utils-misc" % bdgUtilsVersion.value,
            'breeze -> "org.scalanlp" %% "breeze" % "0.12",
            'commons_io -> "commons-io" % "commons-io" % "2.4",
            'commons_math -> "org.apache.commons" % "commons-math3" % "3.6.1",
            'genomic_utils -> "org.hammerlab.genomics" %% "utils" % "1.2.0",
            'hadoop -> "org.apache.hadoop" % "hadoop-client" % computedHadoopVersion.value,
            'hadoop_bam -> ("org.seqdoop" % "hadoop-bam" % "7.7.1" exclude("org.apache.hadoop", "hadoop-client")),
            'htsjdk -> ("com.github.samtools" % "htsjdk" % "2.6.1" exclude("org.xerial.snappy", "snappy-java")),
            'iterators -> "org.hammerlab" %% "iterator" % "1.2.0",
            'kryo -> "com.esotericsoftware.kryo" % "kryo" % "2.24.0",  // Better than Spark's 2.21, which ill-advisedly shades in some minlog classes.
            'loci -> "org.hammerlab.genomics" %% "loci" % "1.5.2",
            'log4j -> "org.slf4j" % "slf4j-log4j12" % "1.7.21",
            'magic_rdds -> "org.hammerlab" %% "magic-rdds" % "1.4.0",
            'mllib -> ("org.apache.spark" %% "spark-mllib" % computedSparkVersion.value exclude("org.scalatest", s"scalatest_${scalaBinaryVersion.value}")),
            'quinine_core -> ("org.bdgenomics.quinine" %% "quinine-core" % "0.0.2" exclude("org.bdgenomics.adam", "adam-core")),
            'reads -> "org.hammerlab.genomics" %% "reads" % "1.0.2",
            'readsets -> "org.hammerlab.genomics" %% "readsets" % "1.0.3",
            'reference -> "org.hammerlab.genomics" %% "reference" % "1.2.1",
            'scala_reflect -> "org.scala-lang" % "scala-reflect" % scalaVersion.value,
            'scalatest -> "org.scalatest" %% "scalatest" % scalatestVersion.value,
            'scalautils -> "org.scalautils" %% "scalautils" % "2.1.5",
            'slf4j -> "org.clapper" %% "grizzled-slf4j" % "1.0.3",
            'spark -> ("org.apache.spark" %% "spark-core" % computedSparkVersion.value exclude("org.scalatest", s"scalatest_${scalaBinaryVersion.value}")),
            'spark_commands -> "org.hammerlab" %% "spark-commands" % "1.0.1",
            'spark_tests -> "org.hammerlab" %% "spark-tests" % "1.3.2",
            'spark_testing_base -> ("com.holdenkarau" %% "spark-testing-base" % sparkTestingBaseVersion.value exclude("org.scalatest", s"scalatest_${scalaBinaryVersion.value}")),
            'spark_util -> "org.hammerlab" %% "spark-util" % "1.1.1",
            'spire -> "org.spire-math" %% "spire" % "0.13.0",
            'string_utils -> "org.hammerlab" %% "string-utils" % "1.2.0",
            'test_utils -> "org.hammerlab" %% "test-utils" % "1.1.6"
          )
        }
      )

    // Settings related to the sbt-assembly plugin.
    val assemblySettings =
      Seq(
        shadeRenames := Nil,
        shadedDeps := Nil,

        // If any shadeRenames are specified, apply them.
        assemblyShadeRules in assembly ++= Seq(
          ShadeRule.rename(
            shadeRenames.value: _*
          ).inAll
        ),

        // Don't run tests when building assembly JAR, by default.
        test in assembly := {},

        // Don't include scala in the assembly JAR, by default; if it is used with Spark downstream, the runtime will
        // include the Scala libraries.
        assemblyIncludeScala := false,

        // If the user overrides the above by setting assemblyIncludeScala to true, pick that up here.
        assemblyOption in assembly := (assemblyOption in assembly).value.copy(includeScala = assemblyIncludeScala.value)
      )

    // Settings related to running in Travis CI.
    val travisSettings =
      Seq(
        travisCoverageScalaVersion := scala211Version.value,

        // Register `travis-report` from above.
        commands += travisReportCmd,

        // Enable coverage-measurement if the TRAVIS_SCALA_VERSION env var matches the corresponding plugin setting.
        coverageEnabled := (
          if (System.getenv("TRAVIS_SCALA_VERSION") == travisCoverageScalaVersion.value)
            true
          else
            coverageEnabled.value
        )
      )

    val depsSettings =
      Seq(
        providedDeps := Nil,
        shadedDeps := Nil,

        libraryDependencies ++= providedDeps.value.map(_ % "provided"),
        libraryDependencies ++= shadedDeps.value,

        // This trans-dep creates a mess in Spark+Hadoop land; just exclude it everywhere by default.
        excludeDependencies += SbtExclusionRule("javax.servlet", "servlet-api")
      )

    Seq(
      organization := "org.hammerlab",
      githubUser := "hammerlab",

      // Build for Scala 2.11 by default
      scalaVersion := scala211Version.value,

      // Only build for Scala 2.11, by default
      crossScalaVersions := Seq(scala211Version.value),

      // All org.hammerlab* repos are published with this Sonatype profile.
      sonatypeProfileName := (
        if (organization.value.startsWith("org.hammerlab"))
          "org.hammerlab"
        else
          sonatypeProfileName.value
      ),

      commands ++= commandsToRegister,

      resolvers += Resolver.sonatypeRepo("releases"),
      resolvers += Resolver.sonatypeRepo("snapshots"),

      main := "",
      mainClass := (
        if (main.value.isEmpty)
          None
        else
          Some(main.value)
      )
    ) ++
      depsSettings ++
      sparkSettings ++
      testSettings ++
      travisSettings ++
      versionSettings ++
      mavenSettings ++
      assemblySettings
  }
}
