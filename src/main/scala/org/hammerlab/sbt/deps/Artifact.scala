package org.hammerlab.sbt.deps

case class Artifact(value: String)

object Artifact {
  implicit def artifactFromString(value: String): Artifact =
    Artifact(value)
}
