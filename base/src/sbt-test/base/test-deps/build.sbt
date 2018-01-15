
default(
  testDeps := Nil,
  github.repo("foo"),
  r"1.2.3"
)

lazy val a = project.settings(
  TaskKey[Unit]("check") := {
    assert(testDeps.value == Nil)
    assert(
      scmInfo.value ==
        Some(
          ScmInfo(
            "https://github.com/hammerlab/foo",
            "scm:git:git@github.com:hammerlab/foo.git",
            Some("scm:git:git@github.com:hammerlab/foo.git")
          )
        )
    )
    assert(version.value == "1.2.3")
    ()
  }
)
lazy val b = project.settings(
  testDeps += scalatest,
  TaskKey[Unit]("check") := {
    assert(testDeps.value == Seq(scalatest))
    ()
  }
)
