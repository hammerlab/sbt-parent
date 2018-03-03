
build(
  v"4.5.6",
  snapshot := true
)

lazy val p1 = project.settings(
  v"1.2.3",
  snapshot := false,
  TaskKey[Unit]("check") := {
    assert(version.value == "1.2.3")
    assert(projectID.value.revision == "1.2.3", projectID.value.revision)
    assert(makePom.value.getName == "p1_2.12-1.2.3.pom", makePom.value.getName)
    assert(!isSnapshot.value)
    ()
  }
)

lazy val p2 = project.settings(
  v"1.2.3",
  TaskKey[Unit]("check") := {
    assert(version.value == "1.2.3-SNAPSHOT")
    assert(projectID.value.revision == "1.2.3-SNAPSHOT", projectID.value.revision)
    assert(makePom.value.getName == "p2_2.12-1.2.3-SNAPSHOT.pom", makePom.value.getName)
    assert(isSnapshot.value)
    ()
  }
)

lazy val p3 = project.settings(
  snapshot := false,
  TaskKey[Unit]("check") := {
    assert(version.value == "4.5.6")
    assert(projectID.value.revision == "4.5.6", projectID.value.revision)
    assert(makePom.value.getName == "p3_2.12-4.5.6.pom", makePom.value.getName)
    assert(!isSnapshot.value)
    ()
  }
)

lazy val p4 = project.settings(
  TaskKey[Unit]("check") := {
    assert(version.value == "4.5.6-SNAPSHOT")
    assert(projectID.value.revision == "4.5.6-SNAPSHOT", projectID.value.revision)
    assert(makePom.value.getName == "p4_2.12-4.5.6-SNAPSHOT.pom", makePom.value.getName)
    assert(isSnapshot.value)
    ()
  }
)
