import org.hammerlab.sbt.deps.Dep

lazy val a = cross
lazy val aJS  = a.js.settings(
  TaskKey[Unit]("check") := {
    assert(testDeps.value == Seq[Dep](scalatest, hammerlab.test.suite))
    ()
  }
)
lazy val aJVM = a.jvm.settings(
  TaskKey[Unit]("check") := {
    assert(testDeps.value == Seq[Dep](scalatest, hammerlab.test.base))
    ()
  }
)
