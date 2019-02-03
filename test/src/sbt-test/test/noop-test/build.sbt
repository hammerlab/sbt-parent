
val st = libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.4" % "test"

lazy val a = project.settings(
  // pin to a "release" version; no tests- or publish-tasks should run
  r"1.0.0",
  st,
  TaskKey[Unit]("check") := {
    assert(!test_?.value)
    ()
  }
)

lazy val b = project.settings(
  // pin to a "release" version; no tests- or publish-tasks should run
  v"1.0.0",
  scalatest,
  TaskKey[Unit]("check") := {
    assert(test_?.value)
    ()
  }
)
