# sbt-parent

[![Maven Central](https://img.shields.io/badge/maven%20central-3.5.1-green.svg)](http://search.maven.org/#search%7Cga%7C1%7Csbt-parent)

SBT plugin factoring out boilerplate for:
- publishing to Maven Central
- managing dependencies
- building+publishing thin shaded JARs
- interfacing with [Coveralls]() on [TravisCI]()
- and more!

```scala
addSbtPlugin("org.hammerlab" % "sbt-parent" % "3.5.1")
```

## Features

### Dependency-management configs

[The bundled `Deps` plugin](src/main/scala/org/hammerlab/sbt/plugin/Deps.scala) offers many syntaxes for easier dependency-configuration:

```scala
dep(
  guava             // regular library dependency
  scalatest tests,  // test-scoped dependency
  spark provided,   // `provided` dependency
  hadoop testtest,  // test-scoped dependency on -tests JAR
  kryo  +testtest   // combined `compile`- and `test->test`-scoped dependency
)
```

Dedicated settings are available for each dependency configuration as well:

```scala
              deps += guava
          testDeps += scalatest
      providedDeps += spark
      testTestDeps += hadoop
compileAndTestDeps += kryo
```

#### Global excludes

```scala
excludes += "javax.servlet" % "servlet-api"
excludes += "org.scalatest" %% "scalatest"   // cross-version syntax works!
```

#### Library aliases

Shorthands for many popular libraries, along with default versions, can be found in [`Parent`](src/main/scala/org/hammerlab/sbt/plugin/Parent.scala), with additional "default" versions appendable to the `versions` key defined in [`Versions`](src/main/scala/org/hammerlab/sbt/plugin/Versions.scala).

### Test configs

[The bundled `Test` plugin](src/main/scala/org/hammerlab/sbt/plugin/Test.scala) adds test-configurations:

- ScalaTest framework with full stack-traces and a `test`-scoped dependency
	- default version: `3.0.0`
	- configurable as `scalatestVersion`
- `test`-dependency on [hammerlab/test-utils]()
	- default version: `1.5.1`
	- configurable as `testUtilsVersion`
  - default versions: `1.5.1` and `3.0.0`

Configure adding a `-tests` artifact to Maven-Central publishing:

```scala
publishTestJar
```

### Assembly/Shading

Shade and rename some Guava classes:

```scala
shadedDeps   += guava
shadeRenames += "com.google.common.**" → "org.hammerlab.guava.@1"
shadeRenames += "com.google.thirdparty.**" → "org.hammerlab.guava.@1"
```

([The `guava` alias refers to `com.google.guava:guava:19.0`](src/main/scala/org/hammerlab/sbt/plugin/Parent.scala))

Publish a "thin" assembly JAR with `shadedDeps` above instead of the usual unshaded JAR or a full assembly/"uber"-JAR:

```scala
publishThinShadedJar
``` 

Slightly-shorter setting for specifying a JAR's "main" class:

```scala
main := "org.foo.Main"
```

### Publishing to Maven Central

[Various settings are added automatically](src/main/scala/org/hammerlab/sbt/plugin/Maven.scala):

- necessary POM settings
- add Sonatype "releases" and "snapshots" resolvers

`sbt +publish` will publish a snapshot to Sonatype for each Scala cross-version, and `sbt +publishSigned sonatypeRelease` will publish to Sonatype-staging and then release to Maven Central.

Additionally, `sbt mavenLocal` wraps `publishM2`, but only operates on `-SNAPSHOT`-versioned projects. 

### TravisCI / Coveralls

`travisCoverageScalaVersion`: only compute coverage and send a report to Coveralls if `TRAVIS_SCALA_VERSION` matches this value (default: `scala211Version`) and `coveralls.disable` system property is not set
- `coverageTest`: command wrapping `test` and, if scoverage is enabled, `coverageReport` for preparing reports
- `travis-report` command suitable for `.travis.yml` `after_success`:
	- if coverage is enabled, send report to Coveralls
	- if this is a multi-module project, run `coverageAggregate` first 

### Spark-related configs
[The `Spark` plugin](src/main/scala/org/hammerlab/sbt/plugin/Spark.scala) offers helpers for projects that depend on [Apache Spark](http://spark.apache.org/):

```scala
addSparkDeps
```

This adds `provided` dependencies on Spark and Hadoop, a `test`-dep on [hammerlab/spark-tests](https://github.com/hammerlab/spark-tests), and a regular dep on [Kryo]().

`spark` above is shorthand for `"org.apache.spark" %% "spark-core" % "2.2.0"`; the version can be set separately:

```scala
sparkVersion := "2.1.1"
```

### Multi-module-project configs

[The `Root` plugin](src/main/scala/org/hammerlab/sbt/plugin/Root.scala) provides the `rootProject` helper for aggregating a project's modules and no-op'ing various settings/tasks that should not operate directly on the root wrapper-module:

```scala
lazy val    base = rootProject(module1, module2)
lazy val module1 = project.settings(…)
lazy val module2 = project.settings(…)
```

### Scala settings

Scala-library and -version settings are available via [`Scala`](src/main/scala/org/hammerlab/sbt/plugin/Scala.scala):

- `isScala21x`
- `scala21xOnly`
- `addScala212` (default: 2.11 only)
- `scala21xVersion`

## Examples

[hammerlab](https://github.com/hammerlab) projects demonstrating use of sbt-parent's features:

- [math-utils](https://github.com/hammerlab/math-utils/blob/master/build.sbt)
  - multi-module project with classpath-dependencies between modules
  - cross-published for Scala 2.11 and 2.12
  - wildcard-imports at `sbt console` startup
- [io-utils](https://github.com/hammerlab/io-utils/blob/master/build.sbt)
- [spark-bam](https://github.com/hammerlab/spark-bam/blob/master/build.sbt)
  - multiple modules
  - shading+renaming of dependencies
  - many different dependency-configurations
  - inter-module test-scoped dependencies

## Using

Add to `project/plugins.sbt` of an existing project:

```scala
addSbtPlugin("org.hammerlab" % "sbt-parent" % "3.5.1")
```

or create a new project using the `create-scala-project` script:

```bash
# Create a project called "proj" at /path/to/proj
create-scala-project /path/to/proj

# Create a project called "foo" at /path/to/proj
create-scala-project /path/to/proj foo

# Create a project called "baz" at ./baz, and set Maven "group" and github-user to "org.foo" and "bar", resp.
GROUP=org.foo GITHUB_USER=bar create-scala-project baz
```

This will create stubs for the following files:

- `build.sbt`: initialize `organization`, `name`, `version`, `libraryDependencies` settings.
- `project/plugins.sbt`: adding a dependency on this plugin.
- `.gitignore`
- `LICENSE` (Apache 2)
- `.travis.yml`
