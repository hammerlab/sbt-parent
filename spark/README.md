# org.hammerlab.sbt:spark

[![org.hammerlab.sbt:spark](https://img.shields.io/badge/org.hammerlab.sbt:spark-4.6.7-green.svg)](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22org.hammerlab.sbt%22%20a%3A%22spark%22)

```scala
addSbtPlugin("org.hammerlab.sbt" % "spark" % "4.6.7")
```

SBT configs for projects that use [Apache Spark](http://spark.apache.org/); ([source](src/main/scala/org/hammerlab/sbt/plugin/Spark.scala)):

```scala
spark
```

This adds:
- `provided` dependencies on Spark and Hadoop
- a `test`-dep on [hammerlab/spark-tests](https://github.com/hammerlab/spark-tests)
- a dep on [Kryo](https://github.com/EsotericSoftware/kryo)
- sets the Scala version to 2.11 only

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
   spark       → "2.1.1",
   spark.tests → "2.3.3",
  hadoop       → "2.6.0",
    kryo       → "2.21.0"
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
