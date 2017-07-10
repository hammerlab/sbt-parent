package org.hammerlab.sbt.plugin

import org.hammerlab.sbt.deps.{ Dep, VersionsMap }
import sbt.Keys.libraryDependencies
import sbt.{ Def, settingKey }

object Versions
  extends Plugin {

  object autoImport {
    val deps = settingKey[Seq[Dep]]("Project dependencies; wrapper around libraryDependencies")
  }

  implicit val versionsMap = settingKey[VersionsMap]("Map from 'group:artifact' aliases/literals to versions numbers")

  val versions = settingKey[Seq[(Dep, String)]]("")

  import autoImport._

  override def projectSettings: Seq[Def.Setting[_]] =
    Seq(
      deps := Nil,

      versions := Nil,

      versionsMap :=
        VersionsMap(
          versions
            .value
            .toMap
        ),

      libraryDependencies ++=
        deps
          .value
          .map(
            _
              .withVersion(versionsMap.value)
              .toModuleID
          )
    )
}
