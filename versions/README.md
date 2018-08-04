# `org.hammerlab.sbt:versions`

[![org.hammerlab.sbt:versions](https://img.shields.io/badge/org.hammerlab.sbt:versions-4.5.2-green.svg)](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22org.hammerlab.sbt%22%20a%3A%22versions%22)

```scala
addSbtPlugin("org.hammerlab.sbt" % "versions" % "4.5.2")
```

SBT plugin for setting and managing dependencies' versions; [source](src/main/scala/org/hammerlab/sbt/plugin/Versions.scala).

## `v`: set snapshot version

Set the current project's version with a variety of short-hands:

```scala
// Set the version to "1.2.3-SNAPSHOT", or "1.2.3" if "snapshot" is true
v"1.2.3"
v("1.2.3")

// un-snapshot the versions set above
snapshot := false
```

These work by setting the `revision` key, which then propagates to the `version` key.

## `r`: fix "released" version, disable publishing

The `r` ("release") syntax sets the version, presumably to an already-released value, and disables `publish` tasks:

```scala
r"1.2.3"
```

This is useful if you want to publish some modules in a project that depend on other modules in the same project that you don't want to publish.

## Workflow: releasing some modules, not others

I use a command alias `prep` in my `~/.sbt/1.0/settings.sbt` to set prepare all non-`r"…"` modules for release:

```scala
addCommandAlias("prep", "set snapshot in ThisBuild := false")
```

- this un-snapshots all `v"…"` modules, which are now eligible for release
- `r"…"` modules can be depended on by them, but will not be released themselves.

Then, to perform a release of certain modules in a project:
- set the desired modules' release versions using `v"…"`
- fix all other modules' versions with `r"…"`
- in the SBT repl:
  ```scala
  prep
  +publishSigned
  sonatypeClose
  sonatypeRelease
  ```

## Default Versions

The `defaultVersions` setting, and `versions()` helper, allow for setting versions for dependencies that may be used more than once in a project.

For example, a plugin can define {group,artifact} coordinates for common dependencies:

```scala
object MyPlugin extends AutoPlugin {
  object autoImport {
    val     guava = "com.google.guava" ^ "gauva"
    val scalatest = "org.scalatest" ^^ "scalatest"
  }
}
```

(cf. the [`parent`](../parent) module in this repo)

Then a downstream, multi-module project can just specify the versions for such dependencies:

```scala
default(
  versions(
        guava → "19.0",
    scalatest → "3.0.0"
  )
)

lazy val module1 = project.settings(dep(guava))
lazy val module2 = project.settings(dep(scalatest tests))

// override defaults
lazy val module3 = project.settings(
  dep(
    guava % "16.0.1",
    scalatest % "3.0.4" tests
  )
)
```

[hammerlab/spark-bam](https://github.com/hammerlab/spark-bam/blob/master/build.sbt) provides an example of this pattern, and the [`parent`](../parent) plugin in this repo provides many common dependency-aliases, including the two above.

## [Dependency-group alias DSL](src/main/scala/org/hammerlab/sbt/dsl)

Many shorthands for defining aliases and settings for groups of related dependencies.

[Here's an example from `parent`](../parent/src/main/scala/org/hammerlab/sbt/plugin/Parent.scala#L81-L86):

```scala
object circe
  extends Libs(
    // this is a template for Maven coordinates of the modules defined with `lib` below
    "io.circe" ^^ "circe" ^ "0.9.3"
  ) {
  val    core = lib
  val generic = lib
  val literal = lib
  val  parser = lib
}
```

This enables depending on various `circe` modules via:

```scala
dep(
  // alias for circe.core, since it was the first declared `lib` above
  circe,
  circe.generic
)
```

Their (shared) version is also given its own setting:

```scala
versions(
  circe → "0.9.2"
)

// equivalent
circe.version = "0.9.2"
```

You can override the `settings` member of a `Libs` group like the above; here's [an example from `spark`](../spark/src/main/scala/org/hammerlab/sbt/plugin/Spark.scala#L39-L51):

```scala
object spark
  extends Libs(
    ("org.apache.spark" ^^ "spark" ^ "2.2.1") - scalatest
  ) {

  val   core = lib
  val graphx = lib
  val  mllib = lib
  val    sql = lib

  /**
   * Add Spark dependencies and set the Scala version to 2.11.x
   */
  override val settings: SettingsDefinition =
    `2.11`.only ++
    Seq(
      Deps.autoImport.dep(
        spark.core provided,
        spark.tests tests,
        hadoop provided,
        kryo
      ),

      // This trans-dep creates a mess in Spark+Hadoop land; just exclude it everywhere by default.
      excludeDependencies += ExclusionRule("javax.servlet", "servlet-api")
    )
}
```

This allows a top-level `build.sbt` declaration:

```scala
spark
```

That adds the specified settings (see [`spark`](../spark) for more discussion; note also the `scalatest` exclusion in the coordinate-template, as another option).

Many plugins in thie repo use this DSL in interesting ways; feel free to explore!
