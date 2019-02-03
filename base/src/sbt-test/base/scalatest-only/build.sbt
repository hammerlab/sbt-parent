import com.github.daniel.shuy.sbt.scripted.scalatest._
import org.scalatest._

default(
  hammerlab.test.disable
)

lazy val a = cross
lazy val aJS  = a.js.settings(
  scriptedScalaTestSpec := Some(new FunSuite with Matchers with ScriptedScalaTestSuiteMixin { override val sbtState: State = state.value
    deps.value should contain(scalatest.dep)
    deps.value should not contain(hammerlab.test.suite)
  })
)
lazy val aJVM = a.jvm.settings(
  scriptedScalaTestSpec := Some(new FunSuite with Matchers with ScriptedScalaTestSuiteMixin { override val sbtState: State = state.value
    deps.value should contain(scalatest.dep)
    deps.value should not contain(hammerlab.test.base)
  })
)
