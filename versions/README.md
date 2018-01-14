# org.hammerlab.sbt:versions

[![Maven Central](https://img.shields.io/badge/maven%20central-4.1.0-green.svg)](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22org.hammerlab.sbt%22%20a%3A%versions%22)

```scala
addSbtPlugin("org.hammerlab.sbt" % "versions" % "4.1.0")
```

SBT plugin for setting and managing dependencies' versions.

## Shorthands

Set the current project's version with a variety of short-hands:

```scala
// Set the version to 1.2.3-SNAPSHOT, except during sbt-pgp's `publishSigned` task, where it will be "1.2.3"
v"1.2.3"
v("1.2.3")

// Set the version to 1.2.3
r"1.2.3"
```

These work by setting the `revision` key, which then propagates to the `version` key.

## Default Versions

The `defaultVersions` setting, and `versions()` helper, allow for setting versions for dependencies that may be used more than once in a project, or in multiple projects that use the same plugin.

For example, a plugin can define {group,artifact} coordinates for common dependencies:

```scala
object MyPlugin extends AutoPlugin {
  object autoImport {
    val     guava = "com.google.guava" ^ "gauva"
    val scalatest = "org.scalatest" ^^ "scalatest"
  }
}
```

Then a downstream, multi-module project can just specify the versions for such dependencies:

```scala
versions(
      guava → "19.0",
  scalatest → "3.0.0"
)

lazy val subproject1 = project.settings(dep(guava))
lazy val subproject2 = project.settings(dep(scalatest.tests))
```

[hammerlab/spark-bam](https://github.com/hammerlab/spark-bam) provides an example of this pattern, and the [`parent`](../parent) plugin in this repo provides many common dependency-aliases, including the two above.
