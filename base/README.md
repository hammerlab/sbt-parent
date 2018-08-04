# org.hammerlab.sbt:base

[![org.hammerlab.sbt:base](https://img.shields.io/badge/org.hammerlab.sbt:base-4.6.3-green.svg)](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22org.hammerlab.sbt%22%20a%3A%22base%22)

```scala
addSbtPlugin("org.hammerlab.sbt" % "base" % "4.6.3")
```

Inherits all plugins in this repo (via [`parent`](../parent)), and adds hammerlab-specific configs:

- `test`-dependency on [hammerlab/test-utils](https://github.com/hammerlab/test-utils)
  - `org.hammerlab.test:::suite:1.0.2` in JS projects
  - `org.hammerlab.test:::base:1.0.2` in JVM projects
  - versions configurable like:
    ```scala
    hammerlab.test.suite.version := "1.0.3".snapshot,
    hammerlab.test. base.version := "1.0.3".snapshot
    ```
  - also picks up ScalaTest `3.0.4` from [`test` plugin](../test)
- [aliases for common `org.hammerlab` deps](src/main/scala/org/hammerlab/sbt/plugin/HammerLab.scala#L29)
- sets an Apache 2.0 license and relevant GitHub metadata

This plugin has [its own giter8 template](https://github.com/hammerlab/sbt-base.g8):

```bash
g8 hammerlab/sbt-base.g8
```
