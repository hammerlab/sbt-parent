package org.hammerlab.sbt.deps

import org.scalajs.sbtplugin.ScalaJSCrossVersion

sealed trait CrossVersion
object CrossVersion {

  implicit def toSBT(crossVersion: CrossVersion)(implicit isScalaJS: IsScalaJS): sbt.CrossVersion =
    crossVersion match {
      case Disabled ⇒ sbt.librarymanagement.Disabled()
      case Binary ⇒ sbt.CrossVersion.Binary()
      case Full ⇒ sbt.CrossVersion.Full()
      case BinaryJS ⇒
        if (isScalaJS.value)
          ScalaJSCrossVersion.binary
        else
          sbt.CrossVersion.Binary()
    }

  implicit def fromSBT(cv: sbt.CrossVersion): CrossVersion =
    cv match {
      case _: sbt.librarymanagement.Disabled ⇒ Disabled
      case _: sbt.CrossVersion.Binary ⇒ Binary
      case _: sbt.CrossVersion.Full ⇒ Full
    }

  case object Disabled extends CrossVersion
  case object Binary extends CrossVersion
  case object BinaryJS extends CrossVersion
  case object Full extends CrossVersion
}

sealed case class IsScalaJS(value: Boolean)
object IsScalaJS {
  val yes = IsScalaJS( true)
  val  no = IsScalaJS(false)
  implicit def make(b: Boolean) =
    if (b)
      yes
    else
      no
}
