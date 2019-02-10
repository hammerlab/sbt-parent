import com.github.daniel.shuy.sbt.scripted.scalatest._
import org.scalatest._
import scala.io.Source.fromFile

github("my-org", "repo-name")

build(
  v"1.2.3"
)

lazy val a = project.settings(
  /*
   * disable coverage otherwise (on Travis, or when TRAVIS_SCALA_VERSION matches [[scalaVersion]]) we get an extra
   * POM dependency on the scoverage scalac plugin that we are not expecting in `pom.xml`
   */
  coverageEnabled := false,
  scriptedScalaTestSpec := Some(new FunSuite with Matchers with ScriptedScalaTestSuiteMixin { override val sbtState: State = state.value
    github.user.value should be(Some("my-org"))
    github.repo.value should be(Some("repo-name"))
    scmInfo.value should be(
        Some(
          ScmInfo(
            "https://github.com/my-org/repo-name",
            "scm:git:git@github.com:my-org/repo-name.git",
            Some("scm:git:git@github.com:my-org/repo-name.git")
          )
        )
    )
    version.value should be("1.2.3-SNAPSHOT")

    val   actualPom = fromFile( makePom.value ).mkString
    val expectedPom = fromFile(   "pom.xml"   ).mkString.trim  // actual POM doesn't contain final newline

    actualPom should be(expectedPom)
  })
)
