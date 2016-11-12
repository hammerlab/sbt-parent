# sbt-parent

[![Maven Central](https://img.shields.io/maven-central/v/org.hammerlab/sbt-parent.svg)](http://search.maven.org/#artifactdetails%7Corg.hammerlab%7Csbt-parent%7C1.0.0%7Cjar)

SBT plugin taking care of Scala library boilerplate.

## Usage

In `project/plugins.sbt`:

```
addSbtPlugin("org.hammerlab" % "sbt-parent" % "1.0.0")
```

## Release snapshots
For both Scala 2.10 and 2.11:

```
sbt +publish
```

## Publish signed releases
Again, for Scala 2.10 and 2.11:

```
sbt +publishSigned releaseSonatype
```

