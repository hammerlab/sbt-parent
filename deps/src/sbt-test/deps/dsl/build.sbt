import com.github.daniel.shuy.sbt.scripted.scalatest._
import org.scalatest._

import sbt.librarymanagement.InclExclRule

/**
 * Test setting dependencies and excludes via [[default]] ([[ThisBuild]])
 */
default(
  dep(
    "a1" ^^ "a2" ^ "a3" tests
  ),

  excludes += "b1" ^^ "b2",

  providedDeps += "c1" ^ "c2" ^ "c3",

  scalaVersion := "2.12.8"
)

scriptedScalaTestSpec := Some(new FunSuite with Matchers with ScriptedScalaTestSuiteMixin { override val sbtState: State = state.value
  libraryDependencies.value should be(
    Seq(
      "org.scala-lang" % "scala-library" % "2.12.8",
      "a1" %% "a2" % "a3" % "test",
      "c1"  % "c2" % "c3" % "provided"
    )
  )

  excludeDependencies.value should be(
    Seq(
      InclExclRule(
        "b1",
        "b2",
        "*",
        Vector(),
        CrossVersion.Binary()
      )
    )
  )
})

// test the same settings in a subproject
lazy val a = project.settings(
  scriptedScalaTestSpec := Some(new FunSuite with Matchers with ScriptedScalaTestSuiteMixin { override val sbtState: State = state.value
    libraryDependencies.value should be(
      Seq(
        "org.scala-lang" % "scala-library" % "2.12.8",
        "a1" %% "a2" % "a3" % "test",
        "c1"  % "c2" % "c3" % "provided"
      )
    )

    excludeDependencies.value should be(
      Seq(
        InclExclRule(
          "b1",
          "b2",
          "*",
          Vector(),
          CrossVersion.Binary()
        )
      )
    )
  })
)
