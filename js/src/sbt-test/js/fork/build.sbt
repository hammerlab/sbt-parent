
lazy val a = crossProject.jvmSettings(
  TaskKey[Unit]("check") := {
    assert(fork.value == true)
    ()
  }
).jsSettings(
  TaskKey[Unit]("check") := {
    assert(fork.value == false)
    ()
  }
)
lazy val aJS = a.js
lazy val aJVM = a.jvm

lazy val b = crossProject.jvmSettings(
  forkJVM := false,
  TaskKey[Unit]("check") := {
    assert(fork.value == false)
    ()
  }
).jsSettings(
  TaskKey[Unit]("check") := {
    assert(fork.value == false)
    ()
  }
)
lazy val bJS = b.js
lazy val bJVM = b.jvm

lazy val c = project.settings(
  TaskKey[Unit]("check") := {
    assert(fork.value == true)
    ()
  }
)

lazy val d = project.settings(
  forkJVM := false,
  TaskKey[Unit]("check") := {
    assert(fork.value == false)
    ()
  }
)
