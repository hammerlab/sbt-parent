# sbt-parent

[![](https://travis-ci.org/hammerlab/sbt-parent.svg?branch=master)](https://travis-ci.org/hammerlab/sbt-parent)

[SBT](http://www.scala-sbt.org/) plugins reducing boilerplate for a variety of commmon tasks

## Modules

### [`assembly`](assembly) [![org.hammerlab.sbt:assembly](https://img.shields.io/badge/org.hammerlab.sbt:assembly-4.6.5-green.svg)](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22org.hammerlab.sbt%22%20a%3A%22assembly%22)

- `sbt-assembly` wrapper
- building+publishing thin shaded JARs

### [`base`] [![org.hammerlab.sbt:base](https://img.shields.io/badge/org.hammerlab.sbt:base-4.6.6-green.svg)](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22org.hammerlab.sbt%22%20a%3A%22base%22)

- wraps all modules in this project
- adds many [hammerlab](https://github.com/hammerlab/) library-aliases default configs

### [`deps`](deps) [![org.hammerlab.sbt:deps](https://img.shields.io/badge/org.hammerlab.sbt:deps-4.5.5-green.svg)](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22org.hammerlab.sbt%22%20a%3A%22deps%22)

Dependency-management DSL and helpers

### [`github`](github) [![org.hammerlab.sbt:github](https://img.shields.io/badge/org.hammerlab.sbt:github-4.1.0-green.svg)](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22org.hammerlab.sbt%22%20a%3A%22github%22)

Fill in `scmInfo` information, populate other relevant POM fields

### [`js`](js) [![org.hammerlab.sbt:github](https://img.shields.io/badge/org.hammerlab.sbt:js-1.3.2-green.svg)](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22org.hammerlab.sbt%22%20a%3A%22js%22)

Shorthands and dependency-aliases for [ScalaJS](https://www.scala-js.org/) projects

### [`maven`](maven) [![org.hammerlab.sbt:maven](https://img.shields.io/badge/org.hammerlab.sbt:maven-4.2.1-green.svg)](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22org.hammerlab.sbt%22%20a%3A%22maven%22)

Publish to Maven Central, fill POM fields, add default resolvers

### [`parent`] [![org.hammerlab.sbt:parent](https://img.shields.io/badge/org.hammerlab.sbt:parent-4.6.6-green.svg)](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22org.hammerlab.sbt%22%20a%3A%22parent%22)

- wrapper for all the plugins here
- includes aliases for common dependencies

### [`root`](root) [![org.hammerlab.sbt:root](https://img.shields.io/badge/org.hammerlab.sbt:root-4.6.5-green.svg)](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22org.hammerlab.sbt%22%20a%3A%22root%22)

Configure multi-module projects

### [`scala`](scala) [![org.hammerlab.sbt:scala](https://img.shields.io/badge/org.hammerlab.sbt:scala-4.6.5-green.svg)](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22org.hammerlab.sbt%22%20a%3A%22scala%22)

DSL and settings for managing Scala versions

### [`spark`](spark) [![org.hammerlab.sbt:spark](https://img.shields.io/badge/org.hammerlab.sbt:spark-4.6.5-green.svg)](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22org.hammerlab.sbt%22%20a%3A%22spark%22)

Configure projects that use [Apache Spark](http://spark.apache.org/)

### [`test`](test) [![org.hammerlab.sbt:test](https://img.shields.io/badge/org.hammerlab.sbt:test-4.5.5-green.svg)](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22org.hammerlab.sbt%22%20a%3A%22test%22)

Use [ScalaTest](http://www.scalatest.org/), publish `-tests` JARs

### [`travis`](travis) [![org.hammerlab.sbt:travis](https://img.shields.io/badge/org.hammerlab.sbt:travis-4.6.5-green.svg)](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22org.hammerlab.sbt%22%20a%3A%22travis%22)

Interface with [Coveralls](https://coveralls.io/) and [TravisCI](https://travis-ci.org/)

### [`versions`](versions) [![org.hammerlab.sbt:versions](https://img.shields.io/badge/org.hammerlab.sbt:versions-4.5.5-green.svg)](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22org.hammerlab.sbt%22%20a%3A%22versions%22)

Manage dependency-versions

## Usage

### All modules: [`parent`]/[`base`]

To depend on all the modules above, add the `parent` plugin to `project/plugins.sbt`:

```scala
addSbtPlugin("org.hammerlab.sbt" % "parent" % "4.6.6")
```

The `base` module also wraps that and adds many [hammerlab](https://github.com/hammerlab/) library-aliases:

```scala
addSbtPlugin("org.hammerlab.sbt" % "base" % "4.6.6")
```

### Individual modules

The modules above are also all available individually:

```scala
// Settings for publishing to Maven Central 
addSbtPlugin("org.hammerlab.sbt" % "maven" % "4.2.1")

// Adding GitHub-repo info
addSbtPlugin("org.hammerlab.sbt" % "github" % "4.1.0")

// etc.
```

Subprojects' READMEs contain more info about their functionality.

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
addSbtPlugin("org.hammerlab.sbt" % "parent" % "4.6.6")
```

or create a new project using [giter8](http://www.foundweekends.org/giter8/):

```bash
g8 hammerlab/sbt-parent.g8
```

(This template lives at [hammerlab/sbt-parent.g8](https://github.com/hammerlab/sbt-parent.g8))


[`parent`]: parent
[`base`]: base
