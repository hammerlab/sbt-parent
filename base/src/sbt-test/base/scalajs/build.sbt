
lazy val a = crossProject
lazy val aJS  = a.js.settings(
  TaskKey[Unit]("check") := {
    assert(testDeps.value == Seq(scalatest, testSuite))
    ()
  }
)
lazy val aJVM = a.jvm.settings(
  TaskKey[Unit]("check") := {
    assert(testDeps.value == Seq(scalatest, testUtils))
    ()
  }
)
