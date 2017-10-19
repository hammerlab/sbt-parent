package org.hammerlab.sbt.deps

import sbt.impl.GroupArtifactID
import sbt.{ ExclusionRule, ModuleID, SbtExclusionRule }

case class Dep(group: Group,
               artifact: Artifact,
               crossVersion: CrossVersion,
               configuration: Configuration = Configuration.Default,
               excludes: Seq[GroupArtifact] = Nil,
               version: Option[String] = None) {

  def groupArtifact: GroupArtifact =
    GroupArtifact(
      group,
      artifact,
      crossVersion
    )

  def toModuleID(crossVersionFn: (CrossVersion, String) ⇒ String): ModuleID =
    version match {
      case Some(version) ⇒
        val Configuration(scope, classifier) = configuration
        val id =
          ModuleID(
            organization = group.value,
            name = artifact.value,
            revision = version,
            configurations =
              scope match {
                case Scope.Compile ⇒ None
                case _ ⇒ Some(scope.toString)
              },
            crossVersion = crossVersion,
            exclusions =
              this
                .excludes
                .map {
                  case GroupArtifact(
                    Group(group),
                    Artifact(artifact),
                    crossVersion
                  ) ⇒
                    ExclusionRule(
                      group,
                      crossVersionFn(crossVersion, artifact)
                    )
                }
          )

        classifier match {
          case Classifier.Default ⇒ id
          case _ ⇒ id.classifier(classifier.toString)
        }
      case None ⇒
        throw VersionNotSetException(this)
    }

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

  def ^(configuration: Configuration): Dep =
    copy(configuration = configuration)

  def %(version: String): Dep =
    this.copy(version = Some(version))

  def ^(version: String): Dep =
    this.copy(version = Some(version))
}

object Dep {
  implicit def fromSBT(ga: GroupArtifactID): Dep = ga: GroupArtifact
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
