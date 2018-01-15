# org.hammerlab.sbt:base

[![Maven Central](https://img.shields.io/badge/maven%20central-4.1.0-green.svg)](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22org.hammerlab.sbt%22%20a%3A%base%22)

```scala
addSbtPlugin("org.hammerlab.sbt" % "base" % "4.1.0")
```

Inherits all plugins in this repo (via [`parent`](..parent)), and adds hammerlab-specific configs:

- `test`-dependency on [hammerlab/test-utils](https://github.com/hammerlab/test-utils)
  - default version: `1.5.1` (with ScalaTest `3.0.0` from [`test` plugin](../test)
  - configurable as `testUtilsVersion`
- aliases for common `org.hammerlab` deps
- sets an Apache 2.0 license and relevant GitHub metadata

This plugin has [its own giter8 template](https://github.com/hammerlab/sbt-base.g8):

```bash
g8 hammerlab/sbt-base.g8
```
