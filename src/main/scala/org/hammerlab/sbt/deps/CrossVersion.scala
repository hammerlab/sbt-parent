package org.hammerlab.sbt.deps

sealed trait CrossVersion {
  def toSBT: sbt.CrossVersion
}
object CrossVersion {

  implicit def toSBT(crossVersion: CrossVersion): sbt.CrossVersion =
    crossVersion.toSBT

  implicit def fromSBT(cv: sbt.CrossVersion): CrossVersion =
    cv match {
      case sbt.CrossVersion.Disabled ⇒ Disabled
      case _: sbt.CrossVersion.Binary ⇒ Binary
      case _: sbt.CrossVersion.Full ⇒ Full
    }

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
