# org.hammerlab.sbt:test

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
