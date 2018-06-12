package org.hammerlab.sbt.dsl

import hammerlab.show._
import org.hammerlab.sbt.deps
import org.hammerlab.sbt.deps.CrossVersion
import sbt.SettingKey
import sbt.SettingsDefinition
import sbt.internal.DslEntry
import Dep.showDep
import org.hammerlab.sbt.plugin.Versions.autoImport.versions

class Dep(d: deps.Dep)(implicit fullname: sourcecode.FullName) {

  val name = {
    val names =
      fullname
        .value
        .split("\\.")
        .iterator
    names
      .find(_ == "autoImport")
      .map { _ ⇒ names.mkString("-") }
      .getOrElse {
        throw new IllegalStateException(
          s"Couldn't find `autoImport` in full-name of Dep: ${fullname.value}"
        )
      }
  }

  /** if a version is passed in, send it through [[org.hammerlab.sbt.plugin.Versions.autoImport.versions]] */
  val dep = d.copy(version = None)
  val group = dep.group
  val version =
    SettingKey[String](
      s"$name-version",
      show"Version of $dep to use"
    )

  def defaults: SettingsDefinition =
    Seq(
      versions += dep → version.value
    ) ++
    d
      .version
      .map { version := _ }
      .toSeq

  def settings: SettingsDefinition = Seq()
}

object Dep {
  implicit def toDep(dep: Dep): deps.Dep = dep.dep
  implicit def toSettings(dep: Dep): SettingsDefinition = dep.settings
  implicit def toDslEntry(dep: Dep): DslEntry = dep.settings

  import CrossVersion._
  implicit val showDep: Show[deps.Dep] =
    (d: deps.Dep) ⇒ {
      val sep =
        d.crossVersion match {
          case Disabled ⇒ ":"
          case Binary | BinaryJS ⇒ "::"
          case Full ⇒ ":::"
        }
      s"${d.group}$sep${d.artifact}"
    }
}
