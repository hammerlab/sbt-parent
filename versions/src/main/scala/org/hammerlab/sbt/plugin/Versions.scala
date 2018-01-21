package org.hammerlab.sbt.plugin

import com.typesafe.sbt.pgp.PgpKeys.publishSigned
import org.hammerlab.sbt.deps.VersionOps._
import org.hammerlab.sbt.deps.{ Dep, GroupArtifact, Snapshot, VersionsMap }
import sbt.Keys._
import sbt._

object Versions
  extends Plugin {

  implicit val versionsMap = settingKey[VersionsMap]("Map from 'group:artifact' aliases/literals to versions numbers")

  /**
   * Wrapper for [[GroupArtifact]]→<version string> tuples entered into a [[VersionsMap]] for managing default library
   * versions.
   */
  case class DefaultVersion(groupArtifact: GroupArtifact, version: String)
  object DefaultVersion {
    implicit def fromTuple(t: (Dep, String)): DefaultVersion = DefaultVersion(t._1.groupArtifact, t._2)
  }

  object autoImport {
    val defaultVersions = settingKey[Seq[DefaultVersion]]("Appendable list of mappings from {group,artifact}s to default-version strings")
    val snapshot = settingKey[Boolean]("When true, ensure that 'version' ends with '-SNAPSHOT' and snapshots repository is used")

    /**
     * Disable common publishing-related tasks
     */
    val noPublish =
      Seq(
        publishLocal := {},
        publishArtifact := false,
        publish := {},
        publishM2 := {}
      )

    /*
     * Set the version and disable publishing, for e.g. when a module in a project has not changed and is to remain
     * pinned at its most recent release value, for other modules (which have changed and need a fresh release) to
     * depend on
     */
    def fixed(v: String) =
      Seq(
        version := v
      ) ++
      inTask(publishSigned)(Seq(version := v)) ++
      noPublish

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
    implicit def versionsAlias(v: versions.type): defaultVersions.type = defaultVersions

    val revision = settingKey[Option[String]]("Implementation of `version` setting that automatically appends '-SNAPSHOT', except in `publishSigned` if `snapshot` is false")

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
     * v"1.0.0"  /** "1.0.0-SNAPSHOT" (set via [[revision]]), but in [[publishSigned]] will lose the "-SNAPSHOT" suffix iff [[snapshot]] is false) */
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
      defaultVersions := Nil,
      revision := None,
      snapshot := false
    )

  override def projectSettings =
    inTask(publishSigned)(
      Seq(
        /**
         * In [[com.typesafe.sbt.pgp.PgpKeys.publishSigned publishSigned]], turn [[revision]] (if present) directly into
         * [[version]] without appending "-SNAPSHOT"
         */
        version :=
          revision
            .value
            .map {
              _.maybeForceSnapshot(snapshot.value)
            }
            .getOrElse(version.value),

        /**
         * Re-iterate this definition of [[isSnapshot]] to reference [[version]] in [[com.typesafe.sbt.pgp.PgpKeys.publishSigned]]
         */
        isSnapshot := version.value.endsWith(Snapshot.suffix)
      )
    ) ++
    Seq(
      /**
       * In general, append "-SNAPSHOT" when turning [[revision]] (which can be set with helpful syntaxes defined in
       * this plugin) into [[version]] (used by main/downstream SBT machinery)
       */
      version := revision.value.map(_.snapshot).getOrElse(version.value),

      versionsMap :=
        VersionsMap(
          defaultVersions
            .value
            .map { d ⇒ d.groupArtifact → d.version }
            .toMap
        )
    )
}
