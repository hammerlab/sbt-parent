package sbt.impl

import org.hammerlab.sbt.deps.{ Artifact, Group, GroupArtifact }

/**
 * Package-hack to get at [[GroupArtifactID]] fields
 */
object ConvertGroupArtifact {
  def fromSBT(ga: GroupArtifactID): GroupArtifact =
    GroupArtifact(
      Group(ga.groupID),
      Artifact(ga.artifactID),
      ga.crossVersion
    )
}
