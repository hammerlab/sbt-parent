

default(
  scalatest.version := "3.0.1"
)

lazy val p1 = project.settings(
  TaskKey[Unit]("check") := {
    assert(
      libraryDependencies.value.contains("org.scalatest" %% "scalatest" % "3.0.1" % "test"),
      libraryDependencies.value.mkString("\n\t", "\n\t", "\n")
    )
    ()
  }
)

lazy val p2 = project.settings(
  scalatest.version := "3.0.2",
  TaskKey[Unit]("check") := {
    assert(
      libraryDependencies.value.contains("org.scalatest" %% "scalatest" % "3.0.2" % "test"),
      libraryDependencies.value.mkString("\n\t", "\n\t", "\n")
    )
    ()
  }
)

lazy val p3 = project.settings(
  versions(scalatest → "3.0.3"),
  TaskKey[Unit]("check") := {
    assert(
      libraryDependencies.value.contains("org.scalatest" %% "scalatest" % "3.0.3" % "test"),
      libraryDependencies.value.mkString("\n\t", "\n\t", "\n")
    )
    ()
  }
)
