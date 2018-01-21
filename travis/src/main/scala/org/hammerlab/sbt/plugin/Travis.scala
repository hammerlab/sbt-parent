package org.hammerlab.sbt.plugin

import org.hammerlab.sbt.plugin.Root.autoImport.root
import org.hammerlab.sbt.plugin.Scala.autoImport.scala211Version
import org.scoverage.coveralls.CoverallsPlugin.coveralls
import sbt.Keys._
import sbt._
import scoverage.ScoverageKeys._

object Travis
  extends Plugin(Root, Scala) {

  object autoImport {
    val travisCoverageScalaVersion = settingKey[String]("Scala version to measure/report test-coverage for")
    val coverageTest = taskKey[Unit]("Wrapper for test and, if $TRAVIS_SCALA_VERSION matches travisCoverageScalaVersion, coverageReport")
  }

  import autoImport._

  def disableCoverallsEnv = System.getProperty("coveralls.disable") != null

  /**
   * Command for building and submitting a coverage report in Travis, *only* for the build corresponding to a specific
   * Scala version (indicated by travisCoverageScalaVersion).
   */
  val travisReport = taskKey[Unit]("Wrapper for coverageAggregate and coveralls, iff TRAVIS_SCALA_VERSION matches `travisCoverageScalaVersion`")

  def travisReportTask = Def.taskDyn {
    val log = streams.value.log
    val extracted = Project.extract(state.value)
    implicit val pr = extracted.currentRef
    implicit val bs = extracted.structure

    val actualTravisScalaVersion = System.getenv("TRAVIS_SCALA_VERSION")
    val tcsv = travisCoverageScalaVersion.value

    if (disableCoverallsEnv) {
      log.info(s"Coveralls reporting disabled by -Dcoveralls.disable")
      Def.task {}
    } else if (actualTravisScalaVersion == tcsv) {

      if (root.value) {
        log.info("Aggregating coverage from submodules")
        coverageAggregate.value
      }

      log.info(s"Running coveralls")
      coveralls
    } else {
      log.info(
        s"Skipping coverage reporting for scala version '${Option(actualTravisScalaVersion).getOrElse("")}' (env var: TRAVIS_SCALA_VERSION); reporting enabled for $tcsv (sbt key: travisCoverageScalaVersion)"
      )
      Def.task {}
    }
  }

  override def projectSettings: Seq[Def.Setting[_]] =
    Seq(
      travisCoverageScalaVersion := scala211Version.value,

      coverageTest := Def.sequential(
        (test in sbt.Test),
        Def.taskDyn[Unit] {
          if (coverageEnabled.value) {
            streams.value.log.debug("Generating coverage reports")
            coverageReport
          } else {
            streams.value.log.debug("Skipping coverageReport generation")
            Def.task {}
          }
        }
      ).value,

      travisReport := travisReportTask.value,

      // Enable coverage-measurement if the TRAVIS_SCALA_VERSION env var matches the corresponding plugin setting.
      coverageEnabled := (
        if (System.getenv("TRAVIS_SCALA_VERSION") == travisCoverageScalaVersion.value && !disableCoverallsEnv)
          true
        else
          coverageEnabled.value
        )
    )
}
