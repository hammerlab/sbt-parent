

default {
  versions(scalatest → "3.0.1")
}

lazy val p1 = project.settings(
  TaskKey[Unit]("check") := {
    assert(libraryDependencies.value.contains("org.scalatest" %% "scalatest" % "3.0.1" % "test"))
    ()
  }
)

lazy val p2 = project.settings(
  versions(scalatest → "3.0.2"),
  TaskKey[Unit]("check") := {
    assert(libraryDependencies.value.contains("org.scalatest" %% "scalatest" % "3.0.2" % "test"))
    ()
  }
)
