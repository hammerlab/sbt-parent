import sbt.librarymanagement.InclExclRule

spark

versions(
  spark → "2.1.1"
)

TaskKey[Unit]("check") := {
  assert(scalaVersion.value == "2.11.12")
  assert(crossScalaVersions.value == Seq("2.11.12"))

  // spark.version has
  assert(spark.version.value == "2.2.1", spark.version)

  /**
   * Reproduce the Spark dependency, with ScalaTest excluded, that we expect to find in [[libraryDependencies]]
   *
   * Its version will be `2.1.1`, as set above
   */
  val exclusion = InclExclRule("org.scalatest", "scalatest", "*", Vector(), CrossVersion.Binary())
  val expected = "org.apache.spark" %% "spark-core" % "2.1.1" % "provided" excludeAll(exclusion)

  assert(libraryDependencies.value.contains(expected))
  ()
}
