# sbt-parent

[![Maven Central](https://img.shields.io/maven-central/v/org.hammerlab/sbt-parent.svg)](http://search.maven.org/#search%7Cga%7C1%7Csbt-parent)

SBT plugin factoring out boilerplate for publishing to Maven Central, optionally cross-building against Apache Spark 1.x and 2.x versions, and building+publishing thin shaded JARs.

## Installation

### `create-scala-project` script
The easiest way to get started is to use the [`create-scala-project`](https://github.com/hammerlab/sbt-parent/blob/master/scripts/create-scala-project) script in this repo:

```bash
# Create a project called "proj" at /path/to/proj 
create-scala-project /path/to/proj      

# Create a project called "foo" at /path/to/proj
create-scala-project /path/to/proj foo  

# Create a project called "baz" at ./baz, and set Maven "group" and github-user to "org.foo" and "bar", resp.
GROUP=org.foo GITHUB_USER=bar create-scala-project baz
```

This will set create stubs for the following files:
- `build.sbt`: initialize `organization`, `name`, `version`, `libraryDependencies` settings.
- `project/plugins.sbt`: adding a dependency on this plugin.
- `.gitignore`
- `LICENSE` (Apache 2)
- `.travis.yml`

### Manually
To use `sbt-parent` in an existing project, add this to its `project/plugins.sbt`:

```
addSbtPlugin("org.hammerlab" % "sbt-parent" % "1.2.10")
```

Then you can specify minimal configuration in your `build.sbt`:

```
organization := "x.y"  // Default: "org.hammerlab"
name := "z"
version := "1.0.0"
libraryDependencies ++= Seq(
  "foo" %% "bar" % "baz",
  libraries.value('spark)
)
```

[A few named dependencies (like `'spark` above) are provided for convenience](https://github.com/hammerlab/sbt-parent/blob/master/src/main/scala/org/hammerlab/sbt/ParentPlugin.scala#L30-L33).

## Usage

### Publish snapshots
To publish snapshots of such a project, for both Scala 2.10 and 2.11:

```
sbt +publish
```

### Publish signed releases
Similarly, to publish releases for Scala 2.10 and 2.11:

```
sbt +publishSigned releaseSonatype
```

### Build/Publish "Thin" Shaded JARs
In general, [it is unwise to publish full assembly JARs](https://github.com/sbt/sbt-assembly#publishing-not-recommended). However, sometimes you may want to shade specific dependencies into the JAR that you publish.
 
 `sbt-parent` exposes hooks to enable this; in `build.sbt`:
  
```scala
// Declare deps to be shaded
shadedDeps += "org" % "name" % "version"
 
// Rename shaded classes.
shadeRenames += "org.name.**" -> "my_org.new_name.@1"

// Publish JAR that includes shaded Guava.
ParentPlugin.publishThinShadedJar
```

### Build Assembly JAR
`sbt-parent` sets a number of default `sbt-assembly` options, so that `sbt assembly` should Just Work without any additional configuration in downstream projects.

The `shadeRenames` hook above is also honored even when building a full assembly JAR. 

### Spark 1.x/2.x cross-building
To publish artifacts that depend on Spark 1.x and 2.x:

- Define the desired versions of each in your project's `build.sbt`:

  ```scala
  sparkVersion := "1.6.3"
  spark2Version := "2.0.0"
  ```

  (The above values are also the defaults).

- You'll also want to wrap your project's `name` setting in `build.sbt`:

  ```scala
  name := ParentPlugin.sparkName("foo")
  ```

  This will append `_spark2` to your artifact-names (before the Scala-version cross-publishing append of e.g. `_2.11`) when building against Spark 2.x.

- Finally, build/test/publish for Spark 2.x by passing `-Dspark2` to SBT:

  ```bash
  sbt -Dspark2 +test  # Run tests for Scala 2.10 and 2.11, linking against Spark 2.x.
  ```

### Scala 2.10 Builds
By default, Scala 2.11 will be used, and SBT defaults apply:
- a `+` prefix will run against both Scala 2.10 and 2.11, as in the above examples.
- to run a task against only Scala 2.10.6, use `sbt ++2.10.6 …`:

## Publish this plugin
Snapshots:
```bash
sbt publish
```

Releases:
```bash
sbt publish sonatypeRelease
```
