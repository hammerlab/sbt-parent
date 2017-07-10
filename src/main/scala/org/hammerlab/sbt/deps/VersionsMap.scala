package org.hammerlab.sbt.deps

import org.hammerlab.sbt.deps.VersionsMap.T

case class VersionsMap(map: T)

object VersionsMap {
  type T = Map[GroupArtifact, String]
  implicit def unwrap(versionsMap: VersionsMap): T = versionsMap.map
}
