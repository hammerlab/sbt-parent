# org.hammerlab.sbt:spark

```scala
addSbtPlugin("org.hammerlab.sbt" % "spark" % "4.0.0")
```

SBT configs for projects that use [Apache Spark](http://spark.apache.org/):

```scala
addSparkDeps
```

This adds `provided` dependencies on Spark and Hadoop, a `test`-dep on [hammerlab/spark-tests](https://github.com/hammerlab/spark-tests), and a regular dep on [Kryo](https://github.com/EsotericSoftware/kryo).

The Spark, Hadoop, and Kryo versions can be set separately:

```scala
sparkVersion := "2.1.1"
hadoopVersion := "2.6.0"
kryoVersion := "2.21.0"
```

