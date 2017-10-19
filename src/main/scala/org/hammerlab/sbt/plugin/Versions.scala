package org.hammerlab.sbt.plugin

import org.hammerlab.sbt.deps.{ Dep, GroupArtifact, VersionsMap }
import sbt.{ Def, settingKey }

object Versions
  extends Plugin {

  implicit val versionsMap = settingKey[VersionsMap]("Map from 'group:artifact' aliases/literals to versions numbers")

  // Wrapper for [[GroupArtifact]]→<version string> tuples entered into a [[VersionsMap]] for managing default library
  // versions.
  case class DefaultVersion(groupArtifact: GroupArtifact, version: String)
  object DefaultVersion {
    implicit def fromTuple(t: (Dep, String)): DefaultVersion = DefaultVersion(t._1.groupArtifact, t._2)
  }

  object autoImport {
    val versions = settingKey[Seq[DefaultVersion]]("Appendable list of mappings from {group,artifact}s to default-version strings")
  }
  import autoImport._

  override def projectSettings: Seq[Def.Setting[_]] =
    Seq(
      versions := Nil,

      versionsMap :=
        VersionsMap(
          versions
            .value
            .map(v ⇒ v.groupArtifact → v.version)
            .toMap
        )
    )
}
