# sbt-parent

[![Maven Central](https://img.shields.io/badge/maven%20central-3.5.2-green.svg)](http://search.maven.org/#search%7Cga%7C1%7Csbt-parent)
[![](https://travis-ci.org/hammerlab/sbt-parent.svg?branch=master)](https://travis-ci.org/hammerlab/sbt-parent)

SBT plugins reducing boilerplate for a variety of commmon tasks:
- [`assembly`](assembly): building+publishing thin shaded JARs
- [`deps`](deps): managing dependencies
- [`maven`](maven): publishing to Maven Central
- [`github`](github): filling in `scmInfo` information (and populating relevant POM fields)
- [`root`](root): configuring multi-module projects
- [`scala`](scala): managing Scala versions
- [`spark`](spark): configuring projects that use [Apache Spark](http://spark.apache.org/)
- [`test`](test): using [calaTest](http://www.scalatest.org/), publishing `-tests` JARs
- [`travis`](travis): interfacing with [Coveralls](https://coveralls.io/) on [TravisCI](https://travis-ci.org/)
- [`versions`](versions): managing dependency-versions
- and more!

Add to `project/plugins.sbt`:

```scala
addSbtPlugin("org.hammerlab.sbt" % "parent" % "1.0.0")
```

Subsets of functionality also available separately:

```scala
// Settings for publishing to Maven Central 
addSbtPlugin("org.hammerlab.sbt" % "maven" % "1.0.0")

// Adding GitHub-repo info
addSbtPlugin("org.hammerlab.sbt" % "github" % "1.0.0")
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

Dedicated settings are available for common dependency configurations as well:

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

Shorthands for many popular libraries, along with default versions, can be found in [`Parent`](parent/src/main/scala/org/hammerlab/sbt/plugin/Parent.scala), with additional "default" versions appendable to the `versions` key defined in [`Versions`](src/main/scala/org/hammerlab/sbt/plugin/Versions.scala).

### Test configs

[The bundled `Test` plugin](parent/src/main/scala/org/hammerlab/sbt/plugin/Test.scala) adds test-configurations:

- ScalaTest framework with full stack-traces and a `test`-scoped dependency
	- default version: `3.0.0`
	- configurable as `scalatestVersion`
- `test`-dependency on [hammerlab/test-utils](https://github.com/hammerlab/test-utils)
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

([The `guava` alias refers to `com.google.guava:guava:19.0`](parent/src/main/scala/org/hammerlab/sbt/plugin/Parent.scala))

Publish a "thin" assembly JAR with `shadedDeps` above instead of the usual unshaded JAR or a full assembly/"uber"-JAR:

```scala
publishThinShadedJar
``` 

Slightly-shorter setting for specifying a JAR's "main" class:

```scala
main := "org.foo.Main"
```

### Publishing to Maven Central

[Various settings are added automatically](maven/src/main/scala/org/hammerlab/sbt/plugin/Maven.scala):

- necessary POM settings
- add Sonatype "releases" and "snapshots" resolvers

`sbt +publish` will publish a snapshot to Sonatype for each Scala cross-version, and `sbt +publishSigned sonatypeRelease` will publish to Sonatype-staging and then release to Maven Central.

Additionally, `sbt mavenLocal` wraps `publishM2`, but only operates on `-SNAPSHOT`-versioned projects.

This functionality can be added to projects independently via:

```scala
addSbtPlugin("org.hammerlab.sbt" % "maven" % "1.0.0")
``` 

### GitHub configs

Some helpers for setting projects' `scmInfo` key:

```scala
	github("my-org", "repo-name")
```

If an org-level plugin sets the `githubUser` key:

```scala
githubUser := "my-org"
// or:
github.user("my-org")
```

then downstream projects may just set the repository portion:

```scala
github.repo("repo-name")
```

These can be added to projects independently of the above 

### TravisCI / Coveralls

`travisCoverageScalaVersion`: only compute coverage and send a report to Coveralls if `TRAVIS_SCALA_VERSION` matches this value (default: `scala211Version`) and `coveralls.disable` system property is not set
- `coverageTest`: command wrapping `test` and, if scoverage is enabled, `coverageReport` for preparing reports
- `travis-report` command suitable for `.travis.yml` `after_success`:
	- if coverage is enabled, send report to Coveralls
	- if this is a multi-module project, run `coverageAggregate` first 

### Spark-related configs
[The `Spark` plugin](parent/src/main/scala/org/hammerlab/sbt/plugin/Spark.scala) offers helpers for projects that depend on [Apache Spark](http://spark.apache.org/):

```scala
addSparkDeps
```

This adds `provided` dependencies on Spark and Hadoop, a `test`-dep on [hammerlab/spark-tests](https://github.com/hammerlab/spark-tests), and a regular dep on [Kryo](https://github.com/EsotericSoftware/kryo).

`spark` above is shorthand for `"org.apache.spark" %% "spark-core" % "2.2.0"`; the version can be set separately:

```scala
sparkVersion := "2.1.1"
```

### Multi-module-project configs

[The `Root` plugin](parent/src/main/scala/org/hammerlab/sbt/plugin/Root.scala) provides the `rootProject` helper for aggregating a project's modules and no-op'ing various settings/tasks that should not operate directly on the root wrapper-module:

```scala
lazy val    base = rootProject(module1, module2)
lazy val module1 = project.settings(…)
lazy val module2 = project.settings(…)
```

Additionally, `build` is a wrapper for applying settings `in ThisBuild`, which will apply to all subprojects in a project:

```scala
build(
  v"1.2.3",                          // version-setting shorthand
  github("my-org", "repo-name")  // fills in scmInfo setting, e.g. for corresponding POM field
)

// Each of these will inherit the version and scmInfo settings defined above
lazy val subproject1 = project.settings(…)
lazy val subproject2 = project.settings(…)
```

### Scala settings

Scala-library and -version settings are available via [`Scala`](parent/src/main/scala/org/hammerlab/sbt/plugin/Scala.scala):

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
addSbtPlugin("org.hammerlab.sbt" % "parent" % "1.0.0")
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
