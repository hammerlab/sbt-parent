package org.hammerlab.sbt.plugin

import org.hammerlab.sbt.deps.{ Dep, GroupArtifact, VersionsMap }
import sbt.{ Def, settingKey }

object Versions
  extends Plugin {

  implicit val versionsMap = settingKey[VersionsMap]("Map from 'group:artifact' aliases/literals to versions numbers")

  val versions = settingKey[Seq[(GroupArtifact, String)]]("")

  implicit def widenDepTuples(ts: Seq[(Dep, String)]): Seq[(GroupArtifact, String)] = ts.map(widenDepTuple)
  implicit def widenDepTuple(t: (Dep, String)): (GroupArtifact, String) = t._1.groupArtifact → t._2


  override def projectSettings: Seq[Def.Setting[_]] =
    Seq(
      versions := Nil,

      versionsMap :=
        VersionsMap(
          versions
          .value
          .toMap
        )
    )
}
