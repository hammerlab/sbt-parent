# org.hammerlab.sbt:spark

[![org.hammerlab.sbt:spark](https://img.shields.io/badge/org.hammerlab.sbt:spark-5.0.0-green.svg)](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22org.hammerlab.sbt%22%20a%3A%22spark%22)

```scala
addSbtPlugin("org.hammerlab.sbt" % "spark" % "5.0.0")
```

SBT configs for projects that use [Apache Spark](http://spark.apache.org/); ([source](src/main/scala/org/hammerlab/sbt/plugin/Spark.scala)):

```scala
spark
```

This adds:
- `provided` dependencies on Spark (2.4.0) and Hadoop (2.7.3)
- a `test`-dep on [hammerlab/spark-tests](https://github.com/hammerlab/spark-tests) (2.4.0)
- a dep on [Kryo](https://github.com/EsotericSoftware/kryo) (2.24.0)
- adds a Scala 2.11 cross-build to the default 2.12-only build inherited from [the `org.hammerlab.sbt:scala` plugin](../scala) 

## Version DSL

Relevant versions can be set via dedicated settings:

```scala
 spark      .version := "2.2.1"
 spark.tests.version := "2.3.3"
hadoop      .version := "2.7.3"
  kryo      .version := "2.24.0"
```

(these values are the defaults)

Versions can also be set via [the `versions` helper](../versions):

```scala
versions(
   spark       → "2.4.0",
   spark.tests → "2.4.0",
  hadoop       → "2.7.3",
    kryo       → "2.24.0"
)
```

The Spark and Hadoop versions can also be overriden via the system parameters `spark.version` and `hadoop.version`.

## Spark submodules

In the above, `spark` defaults to `spark.core`; other modules are also available:

```scala
dep(
  spark.graphx,
  spark.mllib,
  spark.sql
)
```
