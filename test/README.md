# org.hammerlab.sbt:test

[![org.hammerlab.sbt:test](https://img.shields.io/badge/org.hammerlab.sbt:test-4.5.3-green.svg)](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22org.hammerlab.sbt%22%20a%3A%22test%22)

```scala
addSbtPlugin("org.hammerlab.sbt" % "test" % "4.5.3")
```

Offers some test-configurations ([source](src/main/scala/org/hammerlab/sbt/plugin/Test.scala)):

- ScalaTest framework with full stack-traces and a `test`-scoped dependency
  - default version: `3.0.4`
  - configurable as `scalatest.version`
- Configure adding a `-tests` artifact to Maven-Central publishing:

  ```scala
  publishTestJar
  ```
