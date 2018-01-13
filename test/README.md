# org.hammerlab.sbt:test

[![Maven Central](https://img.shields.io/badge/maven%20central-4.0.0-green.svg)](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22org.hammerlab.sbt%22%20a%3A%test%22)

```scala
addSbtPlugin("org.hammerlab.sbt" % "test" % "4.0.0")
```

Offers some test-configurations:

- ScalaTest framework with full stack-traces and a `test`-scoped dependency
	- default version: `3.0.0`
	- configurable as `scalatestVersion`
- Configure adding a `-tests` artifact to Maven-Central publishing:

  ```scala
  publishTestJar
  ```
