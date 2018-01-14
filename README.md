# sbt-parent

[![Maven Central](https://img.shields.io/badge/maven%20central-4.0.0-green.svg)](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22org.hammerlab.sbt%22%20a%3A%22parent%22)
[![](https://travis-ci.org/hammerlab/sbt-parent.svg?branch=master)](https://travis-ci.org/hammerlab/sbt-parent)

[SBT](http://www.scala-sbt.org/) plugins reducing boilerplate for a variety of commmon tasks:
- [`assembly`](assembly): building+publishing thin shaded JARs
- [`deps`](deps): dependency-management DSL and helpers
- [`maven`](maven): publishing to Maven Central, filling POM fields, adding resolvers
- [`github`](github): filling in `scmInfo` information (and populating relevant POM fields)
- [`parent`](parent): wrapper for all the plugins here, including aliases for common dependencies
- [`root`](root): configuring multi-module projects
- [`scala`](scala): managing/setting Scala versions
- [`spark`](spark): configuring projects that use [Apache Spark](http://spark.apache.org/)
- [`test`](test): using [ScalaTest](http://www.scalatest.org/), publishing `-tests` JARs
- [`travis`](travis): interfacing with [Coveralls](https://coveralls.io/) and [TravisCI](https://travis-ci.org/)
- [`versions`](versions): managing dependency-versions

To depend on all of them, add to `project/plugins.sbt`:

```scala
addSbtPlugin("org.hammerlab.sbt" % "parent" % "4.0.0")
```

They are also available individually:

```scala
// Settings for publishing to Maven Central 
addSbtPlugin("org.hammerlab.sbt" % "maven" % "4.0.0")

// Adding GitHub-repo info
addSbtPlugin("org.hammerlab.sbt" % "github" % "4.0.0")

// etc.
```

Subprojects' READMEs contain more info about their functionality.

A derivative of [`parent`](parent) with hammerlab-specific configs can also be found in [the `base` module](base).

## Examples

[hammerlab](https://github.com/hammerlab) projects demonstrating use of various plugins from this repo :

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
addSbtPlugin("org.hammerlab.sbt" % "parent" % "4.0.0")
```

or create a new project using [giter8](http://www.foundweekends.org/giter8/):

```bash
g8 hammerlab/sbt-parent.g8
```

(This template lives at [hammerlab/sbt-parent.g8](https://github.com/hammerlab/sbt-parent.g8))
