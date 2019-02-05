import com.github.daniel.shuy.sbt.scripted.scalatest._
import org.scalatest._

lazy val a = cross.jvmSettings(
  scriptedScalaTestSpec := Some(new FunSuite with Matchers with ScriptedScalaTestSuiteMixin { override val sbtState = state.value
    fork.value should be(true)
  })
).jsSettings(
  scriptedScalaTestSpec := Some(new FunSuite with Matchers with ScriptedScalaTestSuiteMixin { override val sbtState = state.value
    fork.value should be(false)
  })
)
lazy val ax = a.x

lazy val b = cross.jvmSettings(
  forkJVM := false,
  scriptedScalaTestSpec := Some(new FunSuite with Matchers with ScriptedScalaTestSuiteMixin { override val sbtState = state.value
    fork.value should be(false)
  })
).jsSettings(
  scriptedScalaTestSpec := Some(new FunSuite with Matchers with ScriptedScalaTestSuiteMixin { override val sbtState = state.value
    fork.value should be(false)
  })
)
lazy val bx = parent(b.jvm, b.js)

lazy val c = project.settings(
  scriptedScalaTestSpec := Some(new FunSuite with Matchers with ScriptedScalaTestSuiteMixin { override val sbtState = state.value
    fork.value should be(true)
  })
)

lazy val d = project.settings(
  forkJVM := false,
  scriptedScalaTestSpec := Some(new FunSuite with Matchers with ScriptedScalaTestSuiteMixin { override val sbtState = state.value
    fork.value should be(false)
  })
)
