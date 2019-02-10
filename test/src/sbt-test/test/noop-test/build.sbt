import com.github.daniel.shuy.sbt.scripted.scalatest._
import org.scalatest._

val st = libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.4" % "test"

lazy val a = project.settings(
  // pin to a "release" version; no tests- or publish-tasks should run
  r"1.0.0",
  st,
  scriptedScalaTestSpec := Some(new FunSuite with Matchers with ScriptedScalaTestSuiteMixin { override val sbtState: State = state.value
    test_?.value should be(false)
  })
)

lazy val b = project.settings(
  // pin to a "release" version; no tests- or publish-tasks should run
  v"1.0.0",
  scalatest,
  scriptedScalaTestSpec := Some(new FunSuite with Matchers with ScriptedScalaTestSuiteMixin { override val sbtState: State = state.value
    test_?.value should be(true)
  })
)
