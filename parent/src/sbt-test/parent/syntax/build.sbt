
import com.typesafe.sbt.pgp.PgpKeys._

build(
  v"4.5.6",
  snapshot := true
)

lazy val p1 = project.settings(
  v"1.2.3",
  snapshot := false,
  TaskKey[Unit]("check") := {
    assert(version.value == "1.2.3-SNAPSHOT")
    assert((version in publishSigned).value == "1.2.3")

    assert(isSnapshot.value)
    assert(!(isSnapshot in publishSigned).value)
    ()
  }
)

lazy val p2 = project.settings(
  v"1.2.3",
  TaskKey[Unit]("check") := {
    assert(version.value == "1.2.3-SNAPSHOT")
    assert((version in publishSigned).value == "1.2.3-SNAPSHOT")

    assert(isSnapshot.value)
    assert((isSnapshot in publishSigned).value)
    ()
  }
)

lazy val p3 = project.settings(
  snapshot := false,
  TaskKey[Unit]("check") := {
    assert(version.value == "4.5.6-SNAPSHOT")
    assert((version in publishSigned).value == "4.5.6")

    assert(isSnapshot.value)
    assert(!(isSnapshot in publishSigned).value)
    ()
  }
)

lazy val p4 = project.settings(
  TaskKey[Unit]("check") := {
    assert(version.value == "4.5.6-SNAPSHOT")
    assert((version in publishSigned).value == "4.5.6-SNAPSHOT")

    assert(isSnapshot.value)
    assert((isSnapshot in publishSigned).value)
    ()
  }
)
