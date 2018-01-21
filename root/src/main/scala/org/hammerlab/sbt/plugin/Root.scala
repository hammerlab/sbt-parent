package org.hammerlab.sbt.plugin

import org.hammerlab.sbt.plugin.GitHub.autoImport._
import org.hammerlab.sbt.plugin.Maven.autoImport._
import org.hammerlab.sbt.plugin.Versions.autoImport._
import sbt.Keys._
import sbt._
import scoverage.ScoverageKeys.coverageReport
import scoverage.ScoverageSbtPlugin

object Root
  extends Plugin(
    GitHub,
    Maven,
    Versions,
    ScoverageSbtPlugin
  ) {
  object autoImport {
    val root = settingKey[Boolean]("Set to true on multi-module projects' (empty) root modules")

    val rootSettings: Seq[Def.Setting[_]] =
      noPublish ++
      Seq(
        mavenLocal := {},
        (test in sbt.Test) := {},
        coverageReport := {},
        root := true
      )

    def rootProject(name: String,
                    modules: ProjectReference*): Project = {
      val file = new File(".")
      Project(name, file)
        .settings(
          rootSettings,
          github.repo(name)
        )
        .aggregate(modules: _*)
    }

    def rootProject(modules: ProjectReference*): Project = {
      val file = new File(".")
      val name = file.getCanonicalFile.getName
      Project(name, file)
        .settings(
          rootSettings,
          github.repo(name)
        )
        .aggregate(modules: _*)
    }

    /**
     * Set [[ThisBuild]] scope to some [[Setting]]s
     */
    def build  (ss: SettingsDefinition*): Seq[Setting[_]] = inThisBuild(ss.flatten)
    def default(ss: SettingsDefinition*): Seq[Setting[_]] = inThisBuild(ss.flatten)
  }

  import autoImport._

  override def projectSettings: Seq[Def.Setting[_]] =
    Seq(
      root := false
    )
}
