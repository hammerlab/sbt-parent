
TaskKey[Unit]("check") := {
  assert(scalaVersion.value == "2.12.6")
  assert(crossScalaVersions.value == Seq("2.11.12", "2.12.6"))
  ()
}
