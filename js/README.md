# org.hammerlab.sbt:js

[![org.hammerlab.sbt:github](https://img.shields.io/badge/org.hammerlab.sbt:js-1.2.2-green.svg)](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22org.hammerlab.sbt%22%20a%3A%22js%22)

Shorthands and dependency-aliases for [ScalaJS](https://www.scala-js.org/) projects

[Scope cross-project dependencies](src/main/scala/org/hammerlab/sbt/plugin/JS.scala#L111-L113):

```scala
lazy val a = crossProject
lazy val b = crossProject.dependsOn(a test)
lazy val c = crossProject.dependsOn(a testtest)
```

Depend on scalajs-react:

```scala
react
```

Short for:

```scala
dep(
  react.core,
  css.core,
  scalajs.dom
)
scalaJSUseMainModuleInitializer := true
npmDependencies in Compile ++= Seq(
  "react"     → jsVersion.value,
  "react-dom" → jsVersion.value
)
```

etc.
