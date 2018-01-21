# org.hammerlab.sbt:spark

[![Maven Central](https://img.shields.io/badge/maven%20central-4.1.1-green.svg)](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22org.hammerlab.sbt%22%20a%3A%spark%22)

```scala
addSbtPlugin("org.hammerlab.sbt" % "spark" % "4.1.1")
```

SBT configs for projects that use [Apache Spark](http://spark.apache.org/):

```scala
addSparkDeps
```

This adds:
- `provided` dependencies on Spark and Hadoop
- a `test`-dep on [hammerlab/spark-tests](https://github.com/hammerlab/spark-tests)
- a regular dep on [Kryo](https://github.com/EsotericSoftware/kryo)
- sets the Scala version to 2.11

The Spark, Hadoop, and Kryo versions can be set via dedicated settings:

```scala
 sparkVersion := "2.1.1"
hadoopVersion := "2.6.0"
  kryoVersion := "2.21.0"
```

or via [the `versions` helper](../versions):

```scala
versions(
        spark → "2.1.1",
       hadoop → "2.6.0",
  kryoVersion → "2.21.0"
)
```
