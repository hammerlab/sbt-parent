# `org.hammerlab.sbt:parent`

[![org.hammerlab.sbt:parent](https://img.shields.io/badge/org.hammerlab.sbt:parent-5.0.0-green.svg)](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22org.hammerlab.sbt%22%20a%3A%22parent%22)

```scala
addSbtPlugin("org.hammerlab.sbt" % "parent" % "5.0.0")
```

- combines all the plugins in this project
- adds [aliases and default versions for many popular library-dependencies](src/main/scala/org/hammerlab/sbt/plugin/Parent.scala).

[giter8](https://github.com/foundweekends/giter8) template at [hammerlab/sbt-parent.g8](https://github.com/hammerlab/sbt-parent.g8):

```
$ sbt new hammerlab/sbt-parent.g8
```
