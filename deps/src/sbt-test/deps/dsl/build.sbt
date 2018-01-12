
dep(
  "org.scalatest" ^^ "scalatest" ^ "3.0.0" tests
)

TaskKey[Unit]("check") := {
  assert(
    libraryDependencies.value ==
      Seq(
        "org.scala-lang" % "scala-library" % "2.12.4",
        "org.scalatest" %% "scalatest" % "3.0.0" % "test"
      )
  )
  ()
}
