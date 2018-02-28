package org.hammerlab.sbt.deps

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
      CrossVersion.BinaryJS
    )

  def ^^^(artifact: Artifact): Dep =
    GroupArtifact(
      this,
      artifact,
      CrossVersion.Full
    )
}

object Group {
  implicit def groupFromString(value: String): Group =
    Group(value)
}
