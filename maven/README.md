# org.hammerlab.sbt:maven

```scala
addSbtPlugin("org.hammerlab.sbt" % "maven" % "1.0.0")
```

[Various settings for publishing to Maven repositories (especially Maven Central)](src/main/scala/org/hammerlab/sbt/plugin/Maven.scala):

- necessary POM settings
- add Sonatype "releases" and "snapshots" resolvers

`sbt +publish` will publish a snapshot to Sonatype for each Scala cross-version, and `sbt +publishSigned sonatypeRelease` will publish to Sonatype-staging and then release to Maven Central.

Additionally, `sbt mavenLocal` wraps `publishM2`, but only operates on `-SNAPSHOT`-versioned projects.
