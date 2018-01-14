package org.hammerlab.sbt.plugin

import org.hammerlab.sbt.deps.{ Configuration, Dep, Group }
import org.hammerlab.sbt.plugin.Versions.versionsMap
import sbt.Keys._
import sbt._

object Deps
  extends Plugin(Versions) {

  object autoImport {
    val               deps = settingKey[Seq[Dep]]("Project dependencies; wrapper around libraryDependencies")
    val           testDeps = settingKey[Seq[Dep]]("Test-scoped dependencies")
    val       providedDeps = settingKey[Seq[Dep]]("Provided-scoped dependencies")
    val compileAndTestDeps = settingKey[Seq[Dep]]("Dependencies whose '-tests' JAR should be a test-scoped dependency (in addition to the normal compile->default dependency)")
    val       testTestDeps = settingKey[Seq[Dep]]("Dependencies whose '-tests' JAR should be a test-scoped dependency")
    val           excludes = settingKey[Seq[Dep]]("Libraries to exclude as transitive dependencies from all direct dependencies; wrapper for 'excludeDependencies'")

    implicit val stringToGroup = Group.groupFromString _

    implicit class ProjectConfigOps(val p: Project) extends AnyVal {
      def andTest: ClasspathDependency = ClasspathDependency(p, Some("compile->compile;test->test"))
      def test: ClasspathDependency = ClasspathDependency(p, Some("test->test"))
    }

    /**
     * Short-hand for declaring a sequence of dependencies
     */
    def dep(ds: Dep*) = deps ++= ds

    def group(name: String) = organization := name

    val    tests = Configuration.Test
    val testtest = Configuration.TestTest
    val provided = Configuration.Provided
  }

  import autoImport._

  override def globalSettings =
    Seq(
      deps := Nil,

      excludes := Nil,
      excludeDependencies ++=
        excludes
          .value
          .map(
            exclude ⇒
              ExclusionRule(
                exclude.group.value,
                exclude.artifact.value,
                "*",
                Vector(),
                exclude.crossVersion
              )
          ),

      testDeps := Nil,
      deps ++=
        testDeps
          .value
          .map(_ tests),

      providedDeps := Nil,
      deps ++=
        providedDeps
          .value
          .map(_ provided),

      testTestDeps := Nil,
      deps ++=
        testTestDeps
          .value
          .map(_ testtest),

      compileAndTestDeps := Nil,
              deps ++= compileAndTestDeps.value,
      testTestDeps ++= compileAndTestDeps.value
    )

  override def projectSettings =
    Seq(
      /**
       * Convert the [[deps]] hierarchy into SBT's native [[ModuleID]] format, joining with [[versionsMap]] to fill in
       * default versions for [[Dep]]s without explicit versions specified
       */
      libraryDependencies ++=
        deps
          .value
          .flatMap(
            _
              .withVersion(versionsMap.value)
              .toModuleIDs
          ),

      /**
       * Work-around for https://github.com/sbt/sbt/issues/3709: `dependsOn(foo % "test->test")` does not get correctly
       * translated into a test-scoped dependency on foo's "-tests" JAR in the depending project's POM
       */
      projectDependencies := (
        projectDependencies
          .value
          .flatMap {
            dep ⇒
              val configurations = dep.configurations.toSeq.flatMap(_.split(";"))
              val (testConfs, otherConfs) = configurations.partition(_ == "test->test")
              val testConf = testConfs.headOption
              if (testConf.isDefined)
                Seq(
                  dep
                    .withConfigurations(
                      if (otherConfs.nonEmpty)
                        Some(otherConfs.mkString(";"))
                      else
                        None
                    ),
                  dep
                    .withConfigurations(
                      Some("test->test")
                    )
                    .classifier("tests")
                )
              else
                Seq(dep)
          }
      )
    )
}
