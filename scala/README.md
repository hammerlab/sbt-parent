# org.hammerlab.sbt:scala

[![Maven Central](https://img.shields.io/badge/maven%20central-4.2.0-green.svg)](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22org.hammerlab.sbt%22%20a%3A%scala%22)

```scala
addSbtPlugin("org.hammerlab.sbt" % "scala" % "4.2.0")
```

Exposes Scala-library and -version settings:

- `isScala21x`
- `scala21xOnly`: only build for a specific Scala minor-version (by default, projects cross-build against 2.11 and 2.12)
- `addScala21x`: cross-build against an additional Scala minor-version (e.g. 2.10, or 2.11/2.12 if they were otherwise excluded) 
- `scala21xVersion`: full version to be used with Scala 2.10, 2.11, or 2.12
