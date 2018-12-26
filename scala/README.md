# org.hammerlab.sbt:scala

[![org.hammerlab.sbt:scala](https://img.shields.io/badge/org.hammerlab.sbt:scala-4.6.5-green.svg)](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22org.hammerlab.sbt%22%20a%3A%22scala%22)

```scala
addSbtPlugin("org.hammerlab.sbt" % "scala" % "4.6.5")
```

[Scala-library and -version settings](src/main/scala/org/hammerlab/sbt/plugin/Scala.scala):

Major-version predicates:

```scala
`2.10`.?
`2.11`.?
`2.12`.?
```

Minor-version get/set DSL:

```scala
`2.10`.version
`2.11`.version
`2.12`.version

`2.10`.version = "2.10.5"
`2.11`.version = "2.11.12"
`2.12`.version = "2.12.8"
```

Restrict a project to one Scala major version:

```scala
`2.10`.only
`2.11`.only
`2.12`.only
```

Add a Scala major version to cross-build:

```scala
`2.10`.add
`2.11`.add
`2.12`.add
```

Enable [Scalameta](https://scalameta.org/) or [paradise macros](https://docs.scala-lang.org/overviews/macros/paradise.html), or macro-debugging:

```scala
scalameta
enableMacroParadise
debugMacros
```

Add wildcard- or specific imports to `console`-startup commands:

```scala
consolePkgs += "shapeless"
consoleImports += "shapeless._"
```

These are each equivalent to:

```scala
initialCommands += "import shapeless._
```

