# org.hammerlab.sbt:assembly

```scala
addSbtPlugin("org.hammerlab.sbt" % "assembly" % "4.0.0")
```

Plugin providing shading / executable-JAR configs:

## Examples

Shade and rename some Guava classes:

```scala
shadedDeps   += guava
shadeRenames += "com.google.common.**" → "org.hammerlab.guava.@1"
shadeRenames += "com.google.thirdparty.**" → "org.hammerlab.guava.@1"
```

([The `guava` alias refers to `com.google.guava:guava:19.0`](../parent/src/main/scala/org/hammerlab/sbt/plugin/Parent.scala))

Publish a "thin" assembly JAR with `shadedDeps` above instead of the usual unshaded JAR or a full assembly/"uber"-JAR:

```scala
publishThinShadedJar
``` 

Slightly-shorter setting for specifying a JAR's "main" class:

```scala
main := "org.foo.Main"
```

