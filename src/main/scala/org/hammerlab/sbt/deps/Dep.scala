package org.hammerlab.sbt.deps

import sbt.ModuleID

case class Dep(group: Group,
               artifact: Artifact,
               crossVersion: CrossVersion,
               configurations: Configurations = Seq(Configuration.Default),
               excludes: Seq[GroupArtifact] = Nil,
               version: Option[String] = None) {

  def groupArtifact: GroupArtifact =
    GroupArtifact(
      group,
      artifact,
      crossVersion
    )

  def toModuleID: ModuleID =
    version
      .map(
        version ⇒
          ModuleID(
            organization = group.value,
            name = artifact.value,
            revision = version,
            configurations =
              configurations.configurations match {
                case Seq(Configuration.Default) ⇒
                  None
                case _ ⇒
                  Some(configurations.toString)
              },
            crossVersion = crossVersion
          )
      )
      .getOrElse(
        throw VersionNotSetException(this)
      )

  def withVersion(implicit versionsMap: VersionsMap): Dep =
    version
      .map(_ ⇒ this)
      .orElse(
        versionsMap
          .get(groupArtifact)
          .map(
            version ⇒
              copy(
                version =
                  this
                    .version
                    .orElse(
                      Some(version)
                    )
              )
          )
      )
      .getOrElse {
        throw GroupArtifactNotFound(
          group,
          artifact,
          crossVersion,
          versionsMap
        )
      }

  def -(excludes: Dep*): Dep =
    copy(
      excludes = this.excludes ++ excludes.map(_.groupArtifact)
    )

  def ^(configurations: Configurations): Dep =
    copy(configurations = configurations)

  def %(version: String): Dep =
    this.copy(version = Some(version))
}

object Dep {
  def apply(group: Group,
            artifact: Artifact,
            crossVersion: CrossVersion,
            configuration: Configuration): Dep =
    Dep(
      group,
      artifact,
      crossVersion,
      Seq(configuration)
    )

  implicit def depToModuleID(dep: Dep)(implicit versionsMap: VersionsMap): ModuleID =
    dep.toModuleID
}

case class GroupArtifactNotFound(group: Group,
                                 artifact: Artifact,
                                 crossVersion: CrossVersion,
                                 versionsMap: VersionsMap)
  extends IllegalArgumentException(
    s"$group:$artifact:$crossVersion\n$versionsMap"
  )

case class VersionNotSetException(dep: Dep)
  extends IllegalArgumentException(s"$dep")
