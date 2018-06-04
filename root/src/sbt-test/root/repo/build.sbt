
val foo = root(p1, p2)

val scm =
  Some(
    ScmInfo(
      "https://github.com/bar/foo",
      "scm:git:git@github.com:bar/foo.git",
      Some("scm:git:git@github.com:bar/foo.git")
    )
  )

lazy val p1 = project.settings(
  github.user("bar"),
  TaskKey[Unit]("check") := {
    assert(githubRepo.value == Some("foo"))
    assert(scmInfo.value == scm)
    ()
  }
)
lazy val p2 = project.settings(
  TaskKey[Unit]("check") := {
    assert(githubRepo.value == Some("foo"))
    // github.user setting in p1 applies to whole build!
    assert(scmInfo.value == scm)
    ()
  }
)
