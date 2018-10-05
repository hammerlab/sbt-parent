import org.hammerlab.sbt.deps.Dep

default(
  scalatestOnly
)

lazy val a = cross
lazy val aJS  = a.js.settings(
  TaskKey[Unit]("check") := {
    assert(testDeps.value == Seq[Dep](scalatest))
    ()
  }
)
lazy val aJVM = a.jvm.settings(
  TaskKey[Unit]("check") := {
    assert(testDeps.value == Seq[Dep](scalatest))
    ()
  }
)
