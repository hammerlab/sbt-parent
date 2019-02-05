import com.github.daniel.shuy.sbt.scripted.scalatest._
import org.scalatest._
import org.hammerlab.sbt.deps.Dep

enablePlugins(JS, ScalaJSBundlerPlugin)

import scalajs.{ css, dom, react, time }
react
dep(time)

scriptedScalaTestSpec := Some(new FunSuite with Matchers with ScriptedScalaTestSuiteMixin { override val sbtState: State = state.value
  deps.value should be(Seq[Dep](css, react, dom, time))
})
