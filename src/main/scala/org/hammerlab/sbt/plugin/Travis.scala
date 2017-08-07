package org.hammerlab.sbt.plugin

import org.hammerlab.sbt.Util.runTask
import org.hammerlab.sbt.plugin.Root.autoImport.root
import org.hammerlab.sbt.plugin.Scala.autoImport.scala211Version
import org.scoverage.coveralls.CommandSupport
import sbt.Keys.{ commands, streams, test }
import sbt.{ Command, Def, Project, settingKey, taskKey }
import scoverage.ScoverageKeys.{ coverageAggregate, coverageEnabled, coverageReport }

object Travis
  extends Plugin(Root, Scala)
    with CommandSupport {

  object autoImport {
    val travisCoverageScalaVersion = settingKey[String]("Scala version to measure/report test-coverage for")
    val coverageTest = taskKey[Unit]("Wrapper for test, coverageReport, coverageAggregate")
  }

  import autoImport._

  /**
   * Command for building and submitting a coverage report in Travis, *only* for the build corresponding to a specific
   * Scala version (indicated by travisCoverageScalaVersion).
   */
  val travisReportCmd =
    Command.command("travis-report") {
      state ⇒
        implicit val iState = state
        val extracted = Project.extract(state)
        implicit val pr = extracted.currentRef
        implicit val bs = extracted.structure

        val disableCoverallsEnv = System.getProperty("coveralls.disable") != null
        val actualTravisScalaVersion = System.getenv("TRAVIS_SCALA_VERSION")
        val tcsv = travisCoverageScalaVersion.gimme

        if (disableCoverallsEnv) {
          log.info(s"Coveralls reporting disabled by -Dcoveralls.disable")
          state
        } else if (actualTravisScalaVersion == tcsv) {

          val postAggregateState =
            if (root.gimme) {
              log.info("Aggregating coverage from submodules")
              runTask(coverageAggregate, state)
            } else
              state

          log.info(s"Running coveralls")
          Command.process("coveralls", postAggregateState)
        } else {
          log.info(
            s"Skipping coverage reporting for scala version '${Option(actualTravisScalaVersion).getOrElse("")}' (env var: TRAVIS_SCALA_VERSION); reporting enabled for $tcsv (sbt key: travisCoverageScalaVersion)"
          )
          state
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
}
