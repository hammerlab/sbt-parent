# org.hammerlab.sbt:deps

[![org.hammerlab.sbt:deps](https://img.shields.io/badge/org.hammerlab.sbt:deps-4.5.3-green.svg)](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22org.hammerlab.sbt%22%20a%3A%22deps%22)

SBT plugin providing dependency-management helpers:

```scala
addSbtPlugin("org.hammerlab.sbt" % "deps" % "4.5.3")
``` 

## Dependency-DSL

Several syntaxes allow for easier dependency-configuration:

```scala
dep(
  guava             // regular library dependency
  scalatest tests,  // test-scoped dependency
  spark provided,   // `provided` dependency
  hadoop testtest,  // test-scoped dependency on -tests JAR
  kryo  +testtest   // combined `compile`- and `test->test`-scoped dependency
)
```

This uses aliases defined in [`parent`](../parent/src/main/scala/org/hammerlab/sbt/plugin/Parent.scala), but inline coordinates can be used:

```scala
dep(
  "org.scalatest" ^^ "scalatest" ^ "3.0.0" tests
)
```

`^` replaces the usual `%` because we are using the `Dep` DSL defined in this project's `lib` module instead of SBT's `ModuleID`s (the former maintains some structure to the metadata that is lost by conversion to `ModuleID`).

## Configuration-specific settings

Dedicated settings are available for common dependency-configurations as well:

```scala
              deps += guava
          testDeps += scalatest
      providedDeps += spark
      testTestDeps += hadoop
compileAndTestDeps += kryo
```

## Global excludes

Exclude coordinates from all dependencies:

```scala
excludes += "javax.servlet" % "servlet-api"
excludes += "org.scalatest" %% "scalatest"   // cross-version syntax works!
```
