package org.hammerlab.sbt.plugin

import sbt.{ Def, File, Project, ProjectReference, settingKey }
import sbt.Keys.{ publish, publishArtifact, test }
import scoverage.ScoverageKeys.coverageReport

object Root
  extends Plugin {
  object autoImport {
    val root = settingKey[Boolean]("Set to true on multi-module projects' (empty) root modules")

    val rootSettings: Seq[Def.Setting[_]] =
      Seq(
        publish := {},
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
        rootSettings
      )
      .aggregate(modules: _*)
    }

    def rootProject(modules: ProjectReference*): Project = {
      val file = new File(".")
      Project(file.getCanonicalFile.getName, file)
        .settings(
          rootSettings
        )
        .aggregate(modules: _*)
    }
  }

  import autoImport._

  override def projectSettings: Seq[Def.Setting[_]] =
    Seq(
      root := false
    )
}
