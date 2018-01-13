package org.hammerlab.sbt.plugin

import org.hammerlab.sbt.plugin.GitHub.autoImport._
import org.hammerlab.sbt.plugin.Maven.autoImport.mavenLocal
import sbt.Keys._
import sbt._
import scoverage.ScoverageKeys.coverageReport
import scoverage.ScoverageSbtPlugin

object Root
  extends Plugin(
    GitHub,
    Maven,
    ScoverageSbtPlugin
  ) {
  object autoImport {
    val root = settingKey[Boolean]("Set to true on multi-module projects' (empty) root modules")

    val rootSettings: Seq[Def.Setting[_]] =
      Seq(
        publish := {},
        mavenLocal := {},
        publishM2 := {},
        (test in sbt.Test) := {},
        coverageReport := {},
        publishArtifact := false,
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
     * [[sbt.Def.MapScoped]] that applies [[ThisBuild]] scope to a setting
     */
    def thisBuildScope[T] = new ~>[ScopedKey, ScopedKey] {
      def apply[T](a: ScopedKey[T]): ScopedKey[T] =
        a.copy(scope = a.scope.copy(project = Select(ThisBuild)))
    }

    /**
     * Set [[ThisBuild]] scope to some [[Setting]]s
     */
    def build(ss: SettingsDefinition*): Seq[Setting[_]] = ss.flatten.map(_.mapKey(thisBuildScope))
  }

  import autoImport._

  override def projectSettings: Seq[Def.Setting[_]] =
    Seq(
      root := false
    )
}
