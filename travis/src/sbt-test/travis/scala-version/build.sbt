
lazy val eleven = project.settings(
  scalaVersion := "2.11.12",
  crossScalaVersions := Seq("2.11.12"),
  TaskKey[Unit]("check") := {
    assert(travisCoverageScalaVersion.value == Some("2.11.12"))
    ()
  }
)

lazy val twelve = project.settings(
  scalaVersion := "2.12.7",
  crossScalaVersions := Seq("2.12.7"),
  TaskKey[Unit]("check") := {
    assert(travisCoverageScalaVersion.value == Some("2.12.7"))
    ()
  }
)

lazy val both = project.settings(
  scalaVersion := "2.11.12",
  crossScalaVersions := Seq("2.11.12", "2.12.7"),
  TaskKey[Unit]("check") := {
    assert(travisCoverageScalaVersion.value == Some("2.12.7"))
    ()
  }
)
