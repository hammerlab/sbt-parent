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
  private val _bases = ArrayBuffer[Base]()
  private var finalized = false
  def check(arg: Any) = {
    if (finalized)
      throw new IllegalStateException(
        s"$this: can't add $arg post-finalization"
      )
  }
  def bases = {
    finalized = true
    _bases.toVector
  }
  def add(settings: Settings) = {
    check(settings)
    _bases ++= settings.bases
  }
  def apply(base: Base): Unit = {
    check(base)
    _bases += base
  }
}

trait Container {
  protected def self = this
  def name = getClass.getSimpleName
  implicit protected val _settings: Settings = Settings(this)
  def apply(base: Base): Unit = _settings(base)
  def apply(container: Container): Unit = _settings.add(container)
  def bases = _settings.bases
  def  global = bases.flatMap { _. global }
  def project = bases.flatMap { _.project }
}
object Container {
  implicit def toSettings(container: Container): Settings = container._settings
}

abstract class ContainerPlugin(deps: AutoPlugin*)
  extends Plugin(deps: _*)
    with Container {

  override def  globalSettings = super. globalSettings ++  global
  override def projectSettings = super.projectSettings ++ project
}


// TODO: separate this out to a non-plugin build target
// TOOD: add string-interpolator macro a la mill ivy"…"
/**
 * Wrapper for [[Lib one]] or [[Libs more]] Maven coordinates, along with default versions and hooks for global- and
 * project-level settings to use when passing the wrapper as a top-level [[SettingsDefinition SBT setting]].
 */
sealed abstract class Base(
  implicit
  fullname: sourcecode.FullName,
  _settings: Settings
) {
  _settings(this)

  // Force-register this [[Base]] with its container; needed in some cases when the container's settings would otherwise
  // be accessed before this [[Base]] has a chance to be initialized and register itself with the container
  def !() = this

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

  def addDefaultVersions =
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

  def project: SettingsDefinition = addDefaultVersions

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
  fullname: sourcecode.FullName,
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
