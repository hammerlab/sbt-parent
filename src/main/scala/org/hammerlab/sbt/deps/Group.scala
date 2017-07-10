package org.hammerlab.sbt.deps

import sbt.CrossVersion

case class Group(value: String) {
  def ^(artifact: Artifact): Dep =
    GroupArtifact(
      this,
      artifact,
      CrossVersion.Disabled
    )

  def ^^(artifact: Artifact): Dep =
    GroupArtifact(
      this,
      artifact,
      CrossVersion.binary
    )

  def ^^^(artifact: Artifact): Dep =
    GroupArtifact(
      this,
      artifact,
      CrossVersion.full
    )
}

object Group {
  implicit def groupFromString(value: String): Group =
    Group(value)
}
