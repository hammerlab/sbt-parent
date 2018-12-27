import org.hammerlab.sbt.plugin.Spark.autoImport.spark
import sbt.librarymanagement.InclExclRule

spark

versions(
  spark → "2.4.1"
)

TaskKey[Unit]("check") := {
  assert(scalaVersion.value == "2.12.8")
  assert(crossScalaVersions.value == Seq("2.12.8"))

  // the previous default value here is not used in the actual dependency below; the override above takes precedence
  assert(spark.version.value == "2.4.0", spark.version.value)

  /**
   * Reproduce the Spark dependency, with ScalaTest excluded, that we expect to find in [[libraryDependencies]]
   *
   * Its version will be `2.1.1`, as set above
   */
  val exclusion = InclExclRule("org.scalatest", "scalatest", "*", Vector(), CrossVersion.Binary())
  val expected = "org.apache.spark" %% "spark-core" % "2.4.1" % "provided" excludeAll exclusion

  assert(libraryDependencies.value.contains(expected), s"actual:\n\t${libraryDependencies.value.mkString("\n\t")}\nExpected to contain:\n\t$exclusion")
  ()
}
