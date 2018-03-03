
lazy val eleven = project.settings(
  scalaVersion := "2.11.12",
  crossScalaVersions := Seq("2.11.12"),
  TaskKey[Unit]("check") := {
    assert(travisCoverageScalaVersion.value == "2.11.12")
    ()
  }
)

lazy val twelve = project.settings(
  scalaVersion := "2.12.4",
  crossScalaVersions := Seq("2.12.4"),
  TaskKey[Unit]("check") := {
    assert(travisCoverageScalaVersion.value == "2.12.4")
    ()
  }
)

lazy val both = project.settings(
  scalaVersion := "2.11.12",
  crossScalaVersions := Seq("2.11.12", "2.12.4"),
  TaskKey[Unit]("check") := {
    assert(travisCoverageScalaVersion.value == "2.12.4")
    ()
  }
)
