import com.github.daniel.shuy.sbt.scripted.scalatest.ScriptedScalaTestSuiteMixin
//import org.scalatest.Assertions._
import org.scalatest.{ FunSuite, Matchers }

import org.hammerlab.sbt.deps.Dep

default(
  clearTestDeps,
  github.repo := "foo"
)

lazy val a = project.settings(
  r"1.2.3",
  scriptedScalaTestSpec := Some(new FunSuite with Matchers with ScriptedScalaTestSuiteMixin { override val sbtState: State = state.value
    testDeps.value should be(Nil)
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
  testDeps += scalatest,
  scriptedScalaTestSpec := Some(new FunSuite with Matchers with ScriptedScalaTestSuiteMixin { override val sbtState: State = state.value
    testDeps.value should be(Seq[Dep](scalatest))
  })
)

lazy val c = cross.settings(
  testDeps += scalatest,
  scriptedScalaTestSpec := Some(new FunSuite with Matchers with ScriptedScalaTestSuiteMixin { override val sbtState: State = state.value
    testDeps.value should be(Seq[Dep](scalatest))
  })
)
