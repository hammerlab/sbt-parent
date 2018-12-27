
lazy val a = project.settings(
  `2.11`.add,
  TaskKey[Unit]("check") := {
    assert(scalaVersion.value == "2.12.8", s"a scalaVersion: ${scalaVersion.value}")
    assert(crossScalaVersions.value == Seq("2.12.8", "2.11.12"), s"a: ${crossScalaVersions.value.toString}")
    ()
  }
)

lazy val b = project.settings(
  `2.12`.version := "2.12.4",
  TaskKey[Unit]("check") := {
    assert(scalaVersion.value == "2.12.4")
    assert(crossScalaVersions.value == Seq("2.12.4"), s"b: ${crossScalaVersions.value.toString}")
    ()
  }
)

lazy val c = project.settings(
  `2.11`.add,
  `2.12`.version := "2.12.4",
  TaskKey[Unit]("check") := {
    assert(scalaVersion.value == "2.12.4", s"${scalaVersion.value} vs 2.12.4")
    assert(crossScalaVersions.value == Seq("2.12.4", "2.11.12"), s"c: ${crossScalaVersions.value.toString}")
    ()
  }
)
