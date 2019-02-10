import com.github.daniel.shuy.sbt.scripted.scalatest._
import org.scalatest._

val foo = root(p1, p2)

val scm =
  Some(
    ScmInfo(
      "https://github.com/bar/foo",
      "scm:git:git@github.com:bar/foo.git",
      Some("scm:git:git@github.com:bar/foo.git")
    )
  )

lazy val p1 = project.settings(
  // '*'-assignment even inside this project applies the value globally (to `ThisBuild`)
  github.user.*("bar"),
  scriptedScalaTestSpec := Some(new FunSuite with Matchers with ScriptedScalaTestSuiteMixin { override val sbtState: State = state.value
    github.repo.value should be(Some("foo"))
    scmInfo.value should be(scm)
  })
)
lazy val p2 = project.settings(
  scriptedScalaTestSpec := Some(new FunSuite with Matchers with ScriptedScalaTestSuiteMixin { override val sbtState: State = state.value
    github.repo.value should be(Some("foo"))
    // github.user setting in p1 applies to whole build!
    scmInfo.value should be(scm)
  })
)
