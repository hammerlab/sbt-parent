# sbt-parent

[![Maven Central](https://img.shields.io/maven-central/v/org.hammerlab/sbt-parent.svg)](http://search.maven.org/#search%7Cga%7C1%7Csbt-parent)

SBT plugin factoring out boilerplate for publishing to Maven Central, optionally cross-building against Apache Spark 1.x and 2.x versions, and building+publishing thin shaded JARs.

## Usage

Add to `project/plugins.sbt` of an existing project:

```scala
addSbtPlugin("org.hammerlab" % "sbt-parent" % "3.3.1")
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

This will set create stubs for the following files:

- `build.sbt`: initialize `organization`, `name`, `version`, `libraryDependencies` settings.
- `project/plugins.sbt`: adding a dependency on this plugin.
- `.gitignore`
- `LICENSE` (Apache 2)
- `.travis.yml`

## Examples

Project-ID fields work as they normally do:

### Dependency-management configs

[The bundled `Deps` plugin](src/main/scala/org/hammerlab/sbt/plugin/Deps.scala) offers settings for easier dependency-configuration:

```scala
testDeps           += scalatest  // like "org.scalatest" %% "scalatest" % "3.0.0"; change version via scalatestVersion
testTestDeps       += spark      // test-scoped dependency on Spark's test JAR
compileAndTestDeps += spark      // compile->compile and test->test dependencies on Spark
providedDeps       += hadoop     // "provided"-scope dependency on Hadoop
```

Global excludes can be added like:

```scala
excludes += "javax.servlet" % "servlet-api"
excludes += "org.scalatest" %% "scalatest"   // cross-version syntax works!
```

Additionally, many aliases for popular libraries, along with default versions, can be found in [`Parent`](src/main/scala/org/hammerlab/sbt/plugin/Parent.scala), with additional "default" versions appendable to the `versions` key defined in [`Versions`](src/main/scala/org/hammerlab/sbt/plugin/Versions.scala).

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
shadedDeps += guava
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
lazy val base = rootProject(module1, module2)
lazy val module1 = project.settings(…)
lazy val module2 = project.settings(…)
```


### Scala settings

Scala-library and -version settings are available via [`Scala`](src/main/scala/org/hammerlab/sbt/plugin/Scala.scala):

- `isScala21x`
- `scala21xOnly`
- `addScala212` (default: 2.11 only)
- `scala21xVersion`
