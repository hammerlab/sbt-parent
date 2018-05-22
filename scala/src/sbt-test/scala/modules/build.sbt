
default(`2.12`.only)

lazy val p1 = project.settings(
  `2.11`.add,
  TaskKey[Unit]("check") := {
    assert(scalaVersion.value == "2.12.6")
    assert(crossScalaVersions.value == Seq("2.12.6", "2.11.12"))
    ()
  }
)

lazy val p2 = project.settings(
  TaskKey[Unit]("check") := {
    assert(scalaVersion.value == "2.12.6")
    assert(crossScalaVersions.value == Seq("2.12.6"))
    ()
  }
)
