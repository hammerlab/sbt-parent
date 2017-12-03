package org.hammerlab.sbt.plugin

import com.typesafe.sbt.pgp.PgpKeys.publishSigned
import org.hammerlab.sbt.deps.VersionOps._
import org.hammerlab.sbt.deps.{ Dep, GroupArtifact, VersionsMap }
import sbt.KeyRanks.ATask
import sbt.Keys._
import sbt.{ Def, TaskKey, settingKey }

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
    val versions = settingKey[Seq[DefaultVersion]]("Appendable list of mappings from {group,artifact}s to default-version strings")

    val revision = settingKey[String]("Implementation of `version` setting that automatically appends '-SNAPSHOT', except in publishSigned")

    val mavenLocal = TaskKey[Unit]("maven-local", "Wrapper for publishM2 which skips non-SNAPSHOT modules", ATask)

    /**
     * Minimal syntax for setting [[revision]]
     */
    object v {
      def apply(v: String) = (revision := v)
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
      versions := Nil,

      revision := "0.0",
      version := revision.value.snapshot,
      (version in publishSigned) := revision.value,

      mavenLocal := Def.taskDyn[Unit] {
        if (version.value.isSnapshot) {
          streams.value.log.info(s"publishing: ${version.value}")
          publishM2
        } else {
          streams.value.log.info(s"skipping publishing: ${version.value}")
          Def.task {}
        }
      }
      .value,

      versionsMap :=
        VersionsMap(
          versions
            .value
            .map { v ⇒ v.groupArtifact → v.version }
            .toMap
        )
    )
}
