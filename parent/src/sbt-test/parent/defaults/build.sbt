import com.github.daniel.shuy.sbt.scripted.scalatest._
import org.scalatest._

default(
  scalatest.version := "3.0.1"
)

lazy val p1 = project.settings(
  scriptedScalaTestSpec := Some(new FunSuite with Matchers with ScriptedScalaTestSuiteMixin { override val sbtState: State = state.value
    libraryDependencies.value should contain("org.scalatest" %% "scalatest" % "3.0.1" % "test")
    akka.version.value should be("2.5.20")
    akka.http.version.value should be("10.1.7")
  })
)

lazy val p2 = project.settings(
  scalatest.version := "3.0.2",
  scriptedScalaTestSpec := Some(new FunSuite with Matchers with ScriptedScalaTestSuiteMixin { override val sbtState: State = state.value
    libraryDependencies.value should contain("org.scalatest" %% "scalatest" % "3.0.2" % "test")
  })
)

lazy val p3 = project.settings(
  versions(scalatest → "3.0.3"),
  scriptedScalaTestSpec := Some(new FunSuite with Matchers with ScriptedScalaTestSuiteMixin { override val sbtState: State = state.value
    libraryDependencies.value should contain("org.scalatest" %% "scalatest" % "3.0.3" % "test")
  })
)
