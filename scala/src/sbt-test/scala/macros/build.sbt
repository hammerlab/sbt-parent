
build(
  enableMacroParadise,
  `2.11`.only
)

TaskKey[Unit]("check") := {
  val depSet = libraryDependencies.value.toSet
  assert(depSet("org.scala-lang" % "scala-library" % "2.11.12"))
  assert(depSet("org.scala-lang" % "scala-reflect" % "2.11.12"))
  ()
}

lazy val a = project.settings(
  TaskKey[Unit]("check") := {
    val depSet = libraryDependencies.value.toSet
    assert(depSet("org.scala-lang" % "scala-library" % "2.11.12"))
    assert(depSet("org.scala-lang" % "scala-reflect" % "2.11.12"))
    ()
  }
)
