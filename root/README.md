# org.hammerlab.sbt:root

[![org.hammerlab.sbt:root](https://img.shields.io/badge/org.hammerlab.sbt:root-4.6.6-green.svg)](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22org.hammerlab.sbt%22%20a%3A%22root%22)

```scala
addSbtPlugin("org.hammerlab.sbt" % "root" % "4.6.6")
```

## `root`: top-level no-op aggregator project
[The `Root` plugin](src/main/scala/org/hammerlab/sbt/plugin/Root.scala) provides the `root` helper for aggregating a project's modules and no-op'ing various settings/tasks that should not operate directly on the root wrapper-module:

```scala
lazy val    base = root(module1, module2)
lazy val module1 = project.settings(…)
lazy val module2 = project.settings(…)
```

## `parent`: no-op aggregator project

A useful mid-level aggregator, e.g. for grouping JS and JVM versions of a module:

```scala
lazy val  foo      = crossProject.settings(…)
lazy val `foo-js`  = foo.js
lazy val `foo-jvm` = foo.jvm

// useful mid-level aggregator-project for e.g. running all JS+JVM tests for a module
lazy val `foo-x`   = parent(`foo-js`, `foo-jvm`)

// reduces boilerplate in top-level aggregator module as well
lazy val all = root(`foo-x`, `bar-x`, …)
```

## `default`: set default setting-values for submodules

`default` is a wrapper for applying settings `in ThisBuild`, which will apply to all subprojects in a project:

```scala
default(
  v"1.2.3",                      // version-setting shorthand
  github("my-org", "repo-name")  // fills in scmInfo setting, e.g. for corresponding POM field
)

// Each of these will inherit the version and scmInfo settings defined above
lazy val subproject1 = project.settings(…)
lazy val subproject2 = project.settings(…)
```
