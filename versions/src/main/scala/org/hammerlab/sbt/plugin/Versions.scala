package org.hammerlab.sbt.plugin

import org.hammerlab.sbt.dsl
import org.hammerlab.sbt.deps.VersionOps._
import org.hammerlab.sbt.deps.{ Dep, GroupArtifact, Snapshot, VersionsMap }
import sbt.Defaults.artifactPathSetting
import sbt.Keys._
import sbt._
import Command.command
import sbt.plugins.{ IvyPlugin, JvmPlugin }

import scala.collection.mutable.ArrayBuffer

object Versions
  extends Plugin(
    JvmPlugin,
    IvyPlugin
  ) {

  implicit val versionsMap = settingKey[VersionsMap]("Map from 'group:artifact' aliases/literals to versions numbers")

  /**
   * Wrapper for [[GroupArtifact]]→<version string> tuples entered into a [[VersionsMap]] for managing default library
   * versions.
   */
  case class DefaultVersion(groupArtifact: GroupArtifact, version: String)
  object DefaultVersion {
    implicit def fromTuple(t: (Dep, String)): DefaultVersion = DefaultVersion(t._1.groupArtifact, t._2)
    implicit def fromDslDepTuple(t: (dsl.Base, String)): DefaultVersion = DefaultVersion(t._1.groupArtifact, t._2)
  }

  /**
   * Add this to modules to disable common publishing- and test-related tasks
   *
   * It is global, mutable state! Other plugins can add to it.
   *
   * It is expected to be constructed before SBT settings/task machinery is invoked.
   */
  val noopSettings = ArrayBuffer[Setting[_]](
    publishLocal     :=    {},
    publishArtifact  := false,
    publish          :=    {},
    publishM2        :=    {},
    test in sbt.Test :=    {}
  )

  object autoImport {
    val defaultVersions = settingKey[Seq[DefaultVersion]]("Appendable list of mappings from {group,artifact}s to default-version strings")
    val snapshot = settingKey[Boolean]("When true, versions set via `v\"x.y.z\"` shorthands will have '-SNAPSHOT' appended, and snapshots repository will be used")

    val unsnap =
      command("unsnap") {
        s ⇒
          val e = Project.extract(s)
          e.append(Seq(snapshot := false), s)
      }

    val unsnapAll =
      command("unsnapAll") {
        s ⇒
          val e = Project.extract(s)
          e.append(Seq(snapshot in ThisBuild := false), s)
      }

    /*
     * Set the version and disable publishing, for e.g. when a module in a project has not changed and is to remain
     * pinned at its most recent release value, for other modules (which have changed and need a fresh release) to
     * depend on
     */
    def fixed(v: String) =
      Seq(
        version := v
      ) ++
      noopSettings

    /**
     * Syntax around [[defaultVersions]] that supports the usual [[SettingKey.+= +=]]/[[SettingKey.++= ++=]] syntax as
     * well as calling directly with via [[versions.apply apply]]
     */
    object versions {
      def apply(defaults: DefaultVersion*) =
        defaultVersions ++= defaults
    }

    /**
     * Allow appends to [[versions]] to delegate to [[defaultVersions]]
     */
    implicit def versionsAlias(v: versions.type): SettingKey[Seq[DefaultVersion]] = defaultVersions

    val revision = settingKey[Option[String]]("Implementation of `version` setting that automatically appends '-SNAPSHOT', unless `snapshot` is false")

    /**
     * Minimal syntax for setting [[revision]]
     */
    object v {
      def apply(v: String) = revision := Some(v)
    }

    /**
     * More syntax for setting [[revision]] / [[version]]:
     *
     * {{{
     * v"1.0.0"  /** "1.0.0-SNAPSHOT" (set [[version]] via [[revision]]), but "-SNAPSHOT" suffix will depend on the [[snapshot]] setting) */
     * r"1.0.0"  /** Pin to "1.0.0" (set on [[version]] directly), disable publishing */
     * }}}
     */
    implicit class VersionContext(val sc: StringContext) extends AnyVal {
      def v() = autoImport.v(sc.parts.head)
      def r() = fixed(sc.parts.head)
    }
  }

  import autoImport._

  override def globalSettings =
    Seq(
      defaultVersions :=   Nil,
             revision :=  None,
             snapshot :=  true
    )

  override def projectSettings =
    Seq(

      commands += unsnap,
      commands += unsnapAll,

      /**
       * Turn [[revision]] (if present) directly into [[version]] appending "-SNAPSHOT" or not based on [[snapshot]]
       */
      version :=
        revision
          .value
          .map {
            _.maybeForceSnapshot(snapshot.value)
          }
          .getOrElse(version.value),

      projectID := projectID.value.withRevision(revision = version.value),
      artifactPath := artifactPathSetting(artifact).value,

      /**
       * Re-iterate this definition of [[isSnapshot]] to reference [[version]] after it is overwritten by [[revision]]
       */
      isSnapshot := version.value.endsWith(Snapshot.suffix)
    ) ++
    Seq(
      /**
       * In general, append "-SNAPSHOT" when turning [[revision]] (which can be set with helpful syntaxes defined in
       * this plugin) into [[version]] (used by main/downstream SBT machinery)
       */

      versionsMap :=
        VersionsMap(
          defaultVersions
            .value
            .map { d ⇒ d.groupArtifact → d.version }
            .groupBy(_._1)
            .mapValues(_.last._2)
        )
    )
}
