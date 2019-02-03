# org.hammerlab.sbt:scala

[![org.hammerlab.sbt:scripted](https://img.shields.io/badge/org.hammerlab.sbt:scripted-1.0.0-green.svg)](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22org.hammerlab.sbt%22%20a%3A%22scripted%22)

```scala
addSbtPlugin("org.hammerlab.sbt" % "scripted" % "1.0.0")
```

Wrapper around [daniel-shuy/scripted-scalatest-sbt-plugin](https://github.com/daniel-shuy/scripted-scalatest-sbt-plugin) that reduces some boilerplate in setting up ScalaTest assertions in [sbt scripted tests](https://www.scala-sbt.org/release/docs/Testing-sbt-plugins.html#scripted+test+framework).

In a scripted test-case dir:

Add this to `project/plugins.sbt`:

```scala
addSbtPlugin("org.hammerlab.sbt" % "scripted" % "1.0.0")
```

(`project/plugins.sbt` should also include the plugin you are testing)

`test` file:

```
> scriptedScalatest
```

`build.sbt`:

```scala
name := "foo"  // example setting to test
spec := new ScriptedSuite(state.value) {
  // ScalaTest-style assertions go here; this is a [[FunSuite with Matchers]]
  name.value should be("foo")
}
```

The equivalent `build.sbt` with the upstream [scripted-scalatest-sbt-plugin](https://github.com/daniel-shuy/scripted-scalatest-sbt-plugin) would look like:

```scala
import com.github.daniel.shuy.sbt.scripted.scalatest._
import org.scalatest._
name := "foo"  // example setting to test
scriptedScalaTestSpec := Some(
  new FunSuite 
    with Matchers 
    with ScriptedScalaTestSuiteMixin { 
    override val sbtState: State = state.value
    // ScalaTest-style assertions go here; this is a [[FunSuite with Matchers]]
    name.value should be("foo")
  }
)
``` 
