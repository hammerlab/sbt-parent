package org.hammerlab.sbt.deps

sealed trait CrossVersion {
  def toSBT: sbt.CrossVersion
}
object CrossVersion {

  implicit def toSBT(crossVersion: CrossVersion): sbt.CrossVersion =
    crossVersion.toSBT

  case object Disabled extends CrossVersion {
    override def toSBT: sbt.CrossVersion = sbt.CrossVersion.Disabled
  }
  case object Binary extends CrossVersion {
    override def toSBT: sbt.CrossVersion = sbt.CrossVersion.binary
  }
  case object Full extends CrossVersion {
    override def toSBT: sbt.CrossVersion = sbt.CrossVersion.full
  }
}
