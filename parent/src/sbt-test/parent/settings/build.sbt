
import scala.io.Source.fromFile

github("my-org", "repo-name")

build(
  v"1.2.3"
)

lazy val a = project.settings(
  TaskKey[Unit]("check") := {
    assert(githubUser.value == Some("my-org"))
    assert(githubRepo.value == Some("repo-name"))
    assert(
      scmInfo.value ==
        Some(
          ScmInfo(
            "https://github.com/my-org/repo-name",
            "scm:git:git@github.com:my-org/repo-name.git",
            Some("scm:git:git@github.com:my-org/repo-name.git")
          )
        )
    )
    assert(version.value == "1.2.3-SNAPSHOT")

    val   actualPom = fromFile( makePom.value ).mkString
    val expectedPom = fromFile(   "pom.xml"   ).mkString.trim  // actual POM doesn't contain final newline

    if (actualPom != expectedPom)
      sys.error(s"Actual POM:\n$actualPom\n\nExpected POM:\n$expectedPom")

    ()
  }
)
