import com.github.daniel.shuy.sbt.scripted.scalatest._
import org.scalatest._

default(
  hammerlab.test.disable,
  github.repo := "foo"
)

lazy val a = project.settings(
  r"1.2.3",
  scriptedScalaTestSpec := Some(new FunSuite with Matchers with ScriptedScalaTestSuiteMixin { override val sbtState: State = state.value
    testDeps.value should be(Seq(scalatest.dep))
    github.user.value should be(Some("hammerlab"))
    github.repo.value should be(Some("foo"))
    scmInfo.value should be(
      Some(
        ScmInfo(
          "https://github.com/hammerlab/foo",
          "scm:git:git@github.com:hammerlab/foo.git",
          Some("scm:git:git@github.com:hammerlab/foo.git")
        )
      )
    )
    version.value should be("1.2.3")
  })
)

lazy val b = project.settings(
  r"1.2.3",
  hammerlab.test.enable,  // hammerlab test-lib still isn't added due to r"…" release version above
  scriptedScalaTestSpec := Some(new FunSuite with Matchers with ScriptedScalaTestSuiteMixin { override val sbtState: State = state.value
    testDeps.value should be(Seq(scalatest.dep))
  })
)

lazy val c = cross.settings(
  hammerlab.test.enable
)
.jvmSettings(
  scriptedScalaTestSpec := Some(new FunSuite with Matchers with ScriptedScalaTestSuiteMixin { override val sbtState: State = state.value
    testDeps.value should be(Seq(scalatest.dep, hammerlab.test.base))
  })
)
.jsSettings(
  scriptedScalaTestSpec := Some(new FunSuite with Matchers with ScriptedScalaTestSuiteMixin { override val sbtState: State = state.value
    testDeps.value should be(Seq(scalatest.dep, hammerlab.test.suite))
  })
)
