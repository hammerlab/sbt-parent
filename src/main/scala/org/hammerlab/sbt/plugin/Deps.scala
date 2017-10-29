package org.hammerlab.sbt.plugin

import org.hammerlab.sbt.deps.{ Configuration, Dep, Group }
import org.hammerlab.sbt.plugin.Scala.autoImport.appendCrossVersion
import org.hammerlab.sbt.plugin.Versions.versionsMap
import sbt.Keys.{ excludeDependencies, libraryDependencies, projectDependencies }
import sbt.{ ClasspathDependency, Def, Project, SbtExclusionRule, settingKey }

object Deps
  extends Plugin(Scala, Versions) {

  object autoImport {
    val deps = settingKey[Seq[Dep]]("Project dependencies; wrapper around libraryDependencies")
    val testDeps = settingKey[Seq[Dep]]("Test-scoped dependencies")
    val providedDeps = settingKey[Seq[Dep]]("Provided-scoped dependencies")
    val compileAndTestDeps = settingKey[Seq[Dep]]("Dependencies whose '-tests' JAR should be a test-scoped dependency (in addition to the normal compile->default dependency)")
    val testTestDeps = settingKey[Seq[Dep]]("Dependencies whose '-tests' JAR should be a test-scoped dependency")
    val excludes = settingKey[Seq[Dep]]("Libraries to exclude as transitive dependencies from all direct dependencies; wrapper for 'excludeDependencies'")

    implicit val stringToGroup = Group.groupFromString _

    implicit class ProjectConfigOps(val p: Project) extends AnyVal {
      def andTest: ClasspathDependency = ClasspathDependency(p, Some("compile->compile;test->test"))
      def test: ClasspathDependency = ClasspathDependency(p, Some("test->test"))
    }
  }

  import autoImport._

  override def projectSettings: Seq[Def.Setting[_]] =
    Seq(
      deps := Nil,

      excludes := Nil,
      excludeDependencies ++=
        excludes
          .value
          .map(
            exclude ⇒
              SbtExclusionRule(
                exclude.group.value,
                appendCrossVersion.value(
                  exclude.crossVersion,
                  exclude.artifact.value
                )
              )
          ),

      testDeps := Nil,
      deps ++=
        testDeps
          .value
          .map(_ ^ Configuration.Test),

      providedDeps := Nil,
      deps ++=
        providedDeps
          .value
          .map(_ ^ Configuration.Provided),

      testTestDeps := Nil,
      deps ++=
        testTestDeps
          .value
          .map(_ ^ Configuration.TestTest),

      compileAndTestDeps := Nil,
      deps ++= compileAndTestDeps.value,
      testTestDeps ++= compileAndTestDeps.value,

      libraryDependencies ++=
        deps
          .value
          .map(
            _
              .withVersion(versionsMap.value)
              .toModuleID(appendCrossVersion.value)
          ),

      projectDependencies := projectDependencies.value.flatMap {
        dep ⇒
          val configurations = dep.configurations.toSeq.flatMap(_.split(";"))
          val (testConfs, otherConfs) = configurations.partition(_ == "test->test")
          val testConf = testConfs.headOption
          if (testConf.isDefined)
            Seq(
              dep.copy(
                configurations =
                  if (otherConfs.nonEmpty)
                    Some(otherConfs.mkString(";"))
                  else
                    None
              ),
              dep
              .copy(
                configurations =
                  Some("test->test")
              )
              .classifier("tests")
            )
          else
            Seq(dep)
      }
    )
}
