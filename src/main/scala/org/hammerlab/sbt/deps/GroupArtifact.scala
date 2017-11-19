package org.hammerlab.sbt.deps

import sbt.impl.{ ConvertGroupArtifact, GroupArtifactID }

case class GroupArtifact(group: Group,
                         artifact: Artifact,
                         crossVersion: CrossVersion) {
  def %(configuration: Configuration): Dep =
    Dep(
      group,
      artifact,
      crossVersion,
      configuration
    )
}

object GroupArtifact {
  implicit def GroupArtifactToDep(groupArtifact: GroupArtifact): Dep =
    Dep(
      groupArtifact.group,
      groupArtifact.artifact,
      groupArtifact.crossVersion
    )

  implicit def DepToGroupArtifact(dep: Dep): GroupArtifact = dep.groupArtifact
  implicit def fromSBT(ga: GroupArtifactID): GroupArtifact = ConvertGroupArtifact.fromSBT(ga)
}
