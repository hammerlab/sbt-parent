# sbt-parent

SBT plugin taking care of Scala library boilerplate.

Usage:

In `project/plugins.sbt`:

```
addSbtPlugin("org.hammerlab" % "sbt-parent" % "1.0.0")
```

Then release snapshots, for Scala 2.10 and 2.11, with:

```
sbt +publish
```

Publish signed releases (again for Scala 2.10 and 2.11) with:

```
sbt +publishSigned releaseSonatype
```

