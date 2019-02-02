package org.hammerlab.sbt.plugin

import org.hammerlab.sbt.Base
import org.hammerlab.sbt.deps.{ Configuration, CrossVersion, Dep, Group }
import org.hammerlab.sbt.plugin.Versions.versionsMap
import org.scalajs.sbtplugin.ScalaJSPlugin.autoImport.isScalaJSProject
import sbt.Keys._
import sbt.{ CrossVersion ⇒ _, _ }

object Deps
  extends Plugin(
    Versions
  ) {

  sealed abstract class DepArg(val deps: Seq[Dep])
  object DepArg {
    implicit class SingleDep(dep: Dep) extends DepArg(Seq(dep))
    implicit class MultiDep(_deps: Seq[Dep]) extends DepArg(_deps)
    implicit class DslDep(dep: Base) extends DepArg(dep.asDeps)
  }

  /**
   * Short-hand for declaring a sequence of dependencies
   */
  def dep(ds: DepArg*) = deps ++= ds.flatMap(_.deps)

  val               deps = autoImport.              deps
  val           testDeps = autoImport.          testDeps
  val       providedDeps = autoImport.      providedDeps
  val compileAndTestDeps = autoImport.compileAndTestDeps
  val       testTestDeps = autoImport.      testTestDeps
  val           excludes = autoImport.          excludes

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
      def testtest: ClasspathDependency = ClasspathDependency(p, Some("test->test"))
      def test: ClasspathDependency = ClasspathDependency(p, Some("compile->test"))
    }

    /**
     * Short-hand for declaring a sequence of dependencies
     */
    def dep(ds: DepArg*) = Deps.dep(ds: _*)

    def group(org: String) = organization := org
    def group(org: String, artifact: String) =
      Seq(
        organization := org,
        name := artifact
      )

    def subgroup(org: String) = organization := s"${organization.value}.$org"
    def subgroup(org: String, artifact: String) =
      Seq(
        organization := s"${organization.value}.$org",
        name := artifact
      )

    val    tests = Configuration.Test
    val testtest = Configuration.TestTest
    val provided = Configuration.Provided
  }

  import autoImport._

  globals +=
    Seq(
                    deps := Nil,
                excludes := Nil,
                testDeps := Nil,
            providedDeps := Nil,
            testTestDeps := Nil,
      compileAndTestDeps := Nil
    )

  projects +=
    Seq(
      /**
       * Convert the [[deps]] hierarchy into SBT's native [[ModuleID]] format, joining with [[Versions.versionsMap]] to
       * fill in default versions for [[Dep]]s without explicit versions specified
       */
      libraryDependencies ++=
        deps
          .value
          .flatMap(
            _
              .withVersion(versionsMap.value)
              .toModuleIDs(isScalaJSProject.value)
          ),

      /**
       * [[excludes]] is a simple syntax for scala-cross-versioned entries in [[excludeDependencies]]
       */
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
                CrossVersion.toSBT(exclude.crossVersion)(isScalaJSProject.value)
              )
          ),

      deps ++=
        testDeps
          .value
          .map(_ tests),

      deps ++=
        providedDeps
          .value
          .map(_ provided),

      deps ++=
        testTestDeps
          .value
          .map(_ testtest),

      deps ++= compileAndTestDeps.value,

      testTestDeps ++= compileAndTestDeps.value,

      /**
       * Work-around for https://github.com/sbt/sbt/issues/3709: `dependsOn(foo % "test->test")` does not get correctly
       * translated into a test-scoped dependency on foo's "-tests" JAR in the depending project's POM
       */
      projectDependencies :=
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
}
