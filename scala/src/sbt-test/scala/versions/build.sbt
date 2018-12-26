
lazy val a = project.settings(
  TaskKey[Unit]("check") := {
    assert(scalaVersion.value == "2.12.8")
    assert(crossScalaVersions.value == Seq("2.11.12", "2.12.8"))
    ()
  }
)

lazy val b = project.settings(
  `2.12`.only,
  `2.12`.version := "2.12.4",
  TaskKey[Unit]("check") := {
    assert(scalaVersion.value == "2.12.4")
    assert(crossScalaVersions.value == Seq("2.12.4"))
    ()
  }
)

lazy val c = project.settings(
  `2.12`.version := "2.12.4",
  TaskKey[Unit]("check") := {
    assert(scalaVersion.value == "2.12.4", s"${scalaVersion.value} vs 2.12.4")
    assert(crossScalaVersions.value == Seq("2.11.12", "2.12.4"))
    ()
  }
)
