package org.hammerlab.sbt

import hammerlab.deps.syntax._
import hammerlab.show._
import org.hammerlab.sbt.Base.showDep
import org.hammerlab.sbt.deps.{ CrossVersion, Dep, Group }
import org.hammerlab.sbt.plugin.Plugin
import org.hammerlab.sbt.plugin.Versions.autoImport.defaultVersions
import sbt.internal.DslEntry
import sbt.{ AutoPlugin, Def, SettingKey, SettingsDefinition }

import scala.collection.mutable.ArrayBuffer

case class Settings(container: Container) {
  override def toString: String = s"${container.name}.settings"
  val bases = ArrayBuffer[Base]()
  def apply(base: Base): Unit = {
    println(s"$this: adding base $base")
    bases += base
  }
}

trait Container {
  protected def self = this
  def name = getClass.getSimpleName
  implicit protected val _settings: Settings = Settings(this)
  def apply(base: Base): Unit = {
    println(s"$name: adding base ${base.fullname.value}")
    _settings.bases += base
  }
  def apply(container: Container): Unit = {
    println(s"$name: adding container ${container.name}")
    _settings.bases ++= container.bases
  }
  def bases = _settings.bases
  def  global = bases.flatMap { _. global }
  def project = bases.flatMap { _.project }
}

abstract class ContainerPlugin(deps: AutoPlugin*)
  extends Plugin(deps: _*)
    with Container {

  override def globalSettings = {
    println(s"$name: adding global settings for bases: ${bases.mkString(",")}")
    super.globalSettings ++ global
  }
  override def projectSettings = {
    println(s"$name: adding project settings for bases: ${bases.mkString(",")}")
    super.projectSettings ++ project
  }
}


// TODO: separate this out to a non-plugin build target
// TOOD: add string-interpolator macro a la mill ivy"…"
/**
 * Wrapper for [[Lib one]] or [[Libs more]] Maven coordinates, along with default versions and hooks for global- and
 * project-level settings to use when passing the wrapper as a top-level [[SettingsDefinition SBT setting]].
 */
sealed abstract class Base(
  implicit
  val fullname: sourcecode.FullName,
  _settings: Settings
) {
  println(s"${_settings}: register new Base ${fullname.value}")
  _settings(this)

  def group: Group
  def version: SettingKey[String]

  /**
   * An input [[Dep]], or [[Dep]]-template, used to set a default [[Group]] and [[version]], though not necessarily a
   * valid [[Dep]] itself (see [[Libs]], where [[_base]] is a ctor argument providing a template from which to construct
   * module-[[Dep]]s)
   */
  protected def _base: Dep
  def global: SettingsDefinition =
    _base
      .version
      .map { version := _ }
      .toSeq

  def project: SettingsDefinition =
    Seq(
      defaultVersions ++=
        deps
          .filterNot {
            dep ⇒
              defaultVersions
                .value
                .exists {
                  _.groupArtifact == dep.groupArtifact
                }
          }
          .map { _ → version.value }
    )

  /**
   * Settings that this [[Base]] will implicitly convert to, where necessary
   *
   * TODO: have [[dep]] be added as a dependency by default; tricky to avoid circular-dep between Versions, Deps plugins
   */
  def settings: SettingsDefinition = Seq()

  /**
   * Default [[Dep]] to implicitly unroll this [[Base]] as; see [[Base.toDep]]
   */
  def dep: Dep

  /**
   * Full list of [[Dep]]s contained in this [[Base]], for setting
   * [[org.hammerlab.sbt.plugin.Versions.DefaultVersion default versions]] [[project below]]
   */
  def deps: Seq[Dep]

  /**
   * Hook for letting a [[Base]] decide how it should be unrolled to one or more [[Dep]]s
   */
  def asDeps: Seq[Dep] = Seq(dep)

  val name = {
    val names =
      fullname
        .value
        .split("\\.")

    // Attempt to abbreviate the name by dropping common prefixes
    names
      .reverse
      .takeWhile { name ⇒ name != "autoImport" && name != "sbt" }
      .reverse
      .mkString("-")
  }

  def snapshot: SettingsDefinition = version := version.value.snapshot
}

case class Lib(
  protected val _base: Dep
)(
  implicit
  settings: Settings,
  fullname: sourcecode.FullName
)
extends Base {

  /** if a version is passed in, send it through [[org.hammerlab.sbt.plugin.Versions.autoImport.versions]] */
  val dep: Dep = _base.copy(version = None)
  def deps = Seq(dep)

  val group = dep.group
  val version =
    SettingKey[String](
      s"$name-version",
      show"Version of $dep to use"
    )
}

abstract class Name(override val toString: String)
object Name {
  implicit def sourceMacro(implicit name: sourcecode.Name) = new Name(name.value) {}
  implicit def fromString(name: String): Name = new Name(name) {}
  implicit def fromSymbol(name: Symbol): Name = new Name(name.name) {}
  implicit def unwrap(name: Name): String = name.toString
}

class Libs(
  protected val _base: Dep,
  artifactFn: (String, String) ⇒ String = Libs.prepend
)(
  implicit
  fullname: sourcecode.FullName,
  settings: Settings
)
extends Base {
  val group = _base.group
  val artifactPrefix = _base.artifact.value

  def artifact(name: String) = artifactFn(artifactPrefix, name)

  val version =
    SettingKey[String](
      s"$name-version",
      show"Version of ${_base.copy(artifact = s"${artifact("*")}")} to use"
    )

  def dep: Dep = libs.head
  def deps = libs

  protected val libs = ArrayBuffer[Dep]()
  def lib(dep: Dep): Dep = {
    libs += dep
    dep
  }
  def lib(artifact: String): Dep = lib(_base.copy(artifact = artifact))
  def lib(implicit name: Name): Dep = {
    val dep =
      _base.copy(
        artifact = artifact(name),
         version = None
      )
    libs += dep
    dep
  }
}
object Libs {
  val prepend: (String, String) ⇒ String = (prefix, name) ⇒ s"$prefix-$name"
  val disablePrepend: (String, String) ⇒ String = (_, name) ⇒ name
  val replace = disablePrepend
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
