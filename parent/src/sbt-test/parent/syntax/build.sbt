
import com.typesafe.sbt.pgp.PgpKeys._

build(v"4.5.6")

lazy val p1 = project.settings(
  v"1.2.3",
  TaskKey[Unit]("check") := {
    assert(version.value == "1.2.3-SNAPSHOT")
    assert((version in publishSigned).value == "1.2.3")
    ()
  }
)

lazy val p2 = project.settings(
  TaskKey[Unit]("check") := {
    assert(version.value == "4.5.6-SNAPSHOT")
    assert((version in publishSigned).value == "4.5.6")
    ()
  }
)
