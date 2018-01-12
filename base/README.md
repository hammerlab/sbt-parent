# org.hammerlab.sbt:base

```scala
addSbtPlugin("org.hammerlab.sbt" % "base" % "1.0.0")
```

Adds hammerlab-specific configurations to builds:

- `test`-dependency on [hammerlab/test-utils](https://github.com/hammerlab/test-utils)
  - default version: `1.5.1` (with ScalaTest `3.0.0` from [`test` plugin](../test)
  - configurable as `testUtilsVersion`
