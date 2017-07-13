package org.hammerlab.sbt.deps

case class GroupArtifact(group: Group,
                         artifact: Artifact,
                         crossVersion: CrossVersion) {
  def ^(configuration: Configuration): Dep =
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
}
