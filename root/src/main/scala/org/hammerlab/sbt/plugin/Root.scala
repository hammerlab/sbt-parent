package org.hammerlab.sbt.plugin

import org.hammerlab.sbt.plugin.GitHub.autoImport._
import org.hammerlab.sbt.plugin.Maven.autoImport._
import org.hammerlab.sbt.plugin.Test.autoImport.disableTests
import org.hammerlab.sbt.plugin.Versions.noopSettings
import sbt._
import scoverage.ScoverageSbtPlugin

object Root
  extends Plugin(
    GitHub,
    Maven,
    Test,
    Versions,
    ScoverageSbtPlugin
  ) {
  object autoImport {
    val isRoot = settingKey[Boolean]("Set to true on multi-module projects' (empty) root modules")

    object root {
      def apply(
        modules: ProjectReference*
      )(
        implicit
        name: sourcecode.Name
      ):
        Project = {
        val file = new File(".")
        Project(name.value, file)
          .settings(
            settings,
            github.repo(name.value)
          )
          .aggregate(modules: _*)
      }

      val settings =
        Seq(
          isRoot := true,
          mavenLocal := {},
          disableTests
        ) ++
        noopSettings
    }

    object parent {
      def apply(
        modules: ProjectReference*
      )(
        implicit
        name: sourcecode.Name
      ):
        Project = {
        val file = new File(s"./${name.value}")
        Project(name.value, file)
          .settings(
            root.settings
          )
          .aggregate(modules: _*)
      }
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
      isRoot := false
    )
}
