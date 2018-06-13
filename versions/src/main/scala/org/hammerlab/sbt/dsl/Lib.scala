package org.hammerlab.sbt.dsl

import hammerlab.show._
import org.hammerlab.sbt.deps.{ CrossVersion, Dep, Group }
import org.hammerlab.sbt.plugin.Versions.autoImport.{ defaultVersions, versions }
import sbt.{ SettingKey, SettingsDefinition }
import sbt.internal.DslEntry
import Base.showDep

import scala.collection.mutable.ArrayBuffer

sealed abstract class Base(implicit fullname: sourcecode.FullName) {
  def group: Group
  def version: SettingKey[String]

  protected def base: Dep
  def global: SettingsDefinition =
    base
      .version
      .map { version := _ }
      .toSeq

  def settings: SettingsDefinition = Seq()

  def dep: Dep
  def deps: Seq[Dep]

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

  def project: SettingsDefinition =
    Seq(
      versions ++= deps.map(_ → version.value)
    )
}

class Lib(
  protected val base: Dep
)(
  implicit
  fullname: sourcecode.FullName
)
extends Base {

  /** if a version is passed in, send it through [[org.hammerlab.sbt.plugin.Versions.autoImport.versions]] */
  val dep: Dep = base.copy(version = None)
  val deps = Seq(dep)

  val group = dep.group
  val version =
    SettingKey[String](
      s"$name-version",
      show"Version of $dep to use"
    )
}

class Libs(
  protected val base: Dep,
  artifactFn: (String, String) ⇒ String = (prefix, name) ⇒ s"$prefix-$name"
)(
  implicit
  fullname: sourcecode.FullName
)
  extends Base {
  val group = base.group
  val artifactPrefix = base.artifact.value

  def artifact(name: String) = artifactFn(artifactPrefix, name)

  val version =
    SettingKey[String](
      s"$name-version",
      show"Version of ${base.copy(artifact = s"${artifact("*")}")} to use"
    )

  def dep: Dep = deps.head
  val deps = ArrayBuffer[Dep]()
  def lib(implicit name: sourcecode.Name) = {
    val dep =
      base.copy(
        artifact = artifact(name.value),
         version = None
      )
    deps += dep
    dep
  }
}

object Base {

  implicit def toSettings(dep: Base): SettingsDefinition = dep.settings
  implicit def toDslEntry(dep: Base): DslEntry = dep.settings
  implicit def toDep(b: Base): Dep = b.dep

  import CrossVersion._
  implicit val showDep: Show[Dep] =
    (d: Dep) ⇒ {
      val sep =
        d.crossVersion match {
          case Disabled ⇒ ":"
          case Binary | BinaryJS ⇒ "::"
          case Full ⇒ ":::"
        }
      s"${d.group}$sep${d.artifact}"
    }
}
