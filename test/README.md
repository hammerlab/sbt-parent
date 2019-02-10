# org.hammerlab.sbt:test

[![org.hammerlab.sbt:test](https://img.shields.io/badge/org.hammerlab.sbt:test-5.0.0-green.svg)](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22org.hammerlab.sbt%22%20a%3A%22test%22)

Add [ScalaTest](http://www.scalatest.org/) or [utest](https://github.com/lihaoyi/utest) to a project, and other useful testing controls

```scala
addSbtPlugin("org.hammerlab.sbt" % "test" % "5.0.0")
```

Declare a project (or build) to use ScalaTest:

```scala
scalatest
```

- adds a test-dep on ScalaTest
- sets ScalaTest as the only TestFramework
- configures full stack-trace display

`utest` similarly adds the corresponding test-dep and framework.

Both support version helpers:

```scala
scalatest.version := "3.0.5"
utest.version := "0.6.6"
```  

Other test-configurations are available ([source](src/main/scala/org/hammerlab/sbt/plugin/Test.scala)):

- Configure adding a `-tests` artifact to Maven-Central publishing:

  ```scala
  publishTestJar
  ```
- Clear all TestFrameworks

  ```scala
  testing.framework := None
  ```
- Disable testing altogether:

  ```scala
  testing.disable
  ```
