import com.github.daniel.shuy.sbt.scripted.scalatest._
import org.scalatest._

lazy val a = project.settings(
  scriptedScalaTestSpec := Some(new FunSuite with Matchers with ScriptedScalaTestSuiteMixin {
    override val sbtState: State = state.value
    testFrameworks.value should be(Seq(scalatest.framework))
    testDeps.value should be(Seq(scalatest.dep))
  })
)

lazy val b = project.settings(
  utest,
  scriptedScalaTestSpec := Some(new FunSuite with Matchers with ScriptedScalaTestSuiteMixin {
    override val sbtState: State = state.value
    testFrameworks.value should be(Seq(utest.framework))
    testDeps.value should be(Seq(utest.dep))
  })
)

lazy val c = project.settings(
  tests.framework := None,
  scriptedScalaTestSpec := Some(new FunSuite with Matchers with ScriptedScalaTestSuiteMixin {
    override val sbtState: State = state.value
    testFrameworks.value should be(Nil)
    testDeps.value should be(Nil)
  })
)
