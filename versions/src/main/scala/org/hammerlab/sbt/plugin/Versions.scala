package org.hammerlab.sbt.plugin

import com.typesafe.sbt.pgp.PgpKeys.publishSigned
import org.hammerlab.sbt.deps.VersionOps._
import org.hammerlab.sbt.deps.{ Dep, GroupArtifact, VersionsMap }
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
    val defaultVersions: SettingKey[Seq[DefaultVersion]] = settingKey[Seq[DefaultVersion]]("Appendable list of mappings from {group,artifact}s to default-version strings")

    /**
     * Syntax around [[defaultVersions]] that supports the usual [[SettingKey.+= +=]]/[[SettingKey.++= ++=]] syntax as
     * well as calling directly with via [[versions.apply apply]]
     */
    object versions {
      def apply(defaults: DefaultVersion*) =
        (defaultVersions in ThisBuild) ++= defaults
    }

    implicit def versionsAlias(v: versions.type): defaultVersions.type = defaultVersions

    val revision = settingKey[Option[String]]("Implementation of `version` setting that automatically appends '-SNAPSHOT', except in publishSigned")

    /**
     * Minimal syntax for setting [[revision]]
     */
    object v {
      def apply(v: String) = (revision := Some(v))
    }

    /**
     * More syntax for setting [[revision]], [[version]]:
     *
     * {{{
     * v"1.0.0"  // "1.0.0-SNAPSHOT" (set via `revision` key)
     * r"1.0.0"  // "1.0.0" (set on `version` key directly)
     * }}}
     */
    implicit class VersionContext(val sc: StringContext) extends AnyVal {
      def v() = autoImport.v(sc.parts.head)
      def r() = (version := sc.parts.head)
    }
  }
  import autoImport._

  override def projectSettings: Seq[Def.Setting[_]] =
    Seq(
      defaultVersions in Global := Nil,

      revision in Global := None,
      version in Global := revision.value.map(_.snapshot).getOrElse((version in Global).value),
      (version in publishSigned) := revision.value.getOrElse((version in publishSigned).value),

      versionsMap :=
        VersionsMap(
          defaultVersions
            .value
            .map { d ⇒ d.groupArtifact → d.version }
            .toMap
        )
    )
}
