
import sbt.librarymanagement.InclExclRule

default(
  dep(
    "org.scalatest" ^^ "scalatest" ^ "3.0.0" tests
  ),

  excludes += "org.apache.spark" ^^ "spark-core"
)

TaskKey[Unit]("check") := {
  assert(
    libraryDependencies.value ==
      Seq(
        "org.scala-lang" % "scala-library" % "2.12.4",
        "org.scalatest" %% "scalatest" % "3.0.0" % "test"
      )
  )

  assert(
    excludeDependencies.value ==
      Seq(
        InclExclRule(
          "org.apache.spark",
          "spark-core",
          "*",
          Vector(),
          CrossVersion.Binary()
        )
      )
  )
  ()
}

lazy val a = project.settings(
  TaskKey[Unit]("check") := {
    assert(
      libraryDependencies.value ==
        Seq(
          "org.scala-lang" % "scala-library" % "2.12.4",
          "org.scalatest" %% "scalatest" % "3.0.0" % "test"
        )
    )

    assert(
      excludeDependencies.value ==
        Seq(
          InclExclRule(
            "org.apache.spark",
            "spark-core",
            "*",
            Vector(),
            CrossVersion.Binary()
          )
        )
    )
    ()
  }
)
