
lazy val a = cross.jvmSettings(
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
lazy val ax = a.x

lazy val b = cross.jvmSettings(
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
lazy val bx = parent(b.jvm, b.js)

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
