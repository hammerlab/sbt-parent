
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

  scalaVersion := "2.12.7"
)

TaskKey[Unit]("check") := {
  assert(
    libraryDependencies.value ==
      Seq(
        "org.scala-lang" % "scala-library" % "2.12.7",
        "a1" %% "a2" % "a3" % "test",
        "c1"  % "c2" % "c3" % "provided"
      )
  )

  assert(
    excludeDependencies.value ==
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
  ()
}

// test the same settings in a subproject
lazy val a = project.settings(
  TaskKey[Unit]("check") := {
    assert(
      libraryDependencies.value ==
        Seq(
          "org.scala-lang" % "scala-library" % "2.12.7",
          "a1" %% "a2" % "a3" % "test",
          "c1"  % "c2" % "c3" % "provided"
        ),
      s"${libraryDependencies.value}"
    )

    assert(
      excludeDependencies.value ==
        Seq(
          InclExclRule(
            "b1",
            "b2",
            "*",
            Vector(),
            CrossVersion.Binary()
          )
        ),
      s"${excludeDependencies.value}"
    )
    ()
  }
)
