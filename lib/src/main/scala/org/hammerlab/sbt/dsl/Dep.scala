package org.hammerlab.sbt.dsl

import org.hammerlab.sbt.deps
import sbt.SettingKey

class Dep(d: deps.Dep)(implicit name: sourcecode.FullName) {
  val dep = d
  val version =
    SettingKey[String](
      s"${
        name
          .value
          .split("\\.")
          .dropWhile(_ != "autoImport")
          .mkString("-")
      }-version",
      s"Version of $dep to use"
    )
}

object Dep {
  implicit def toDep(dep: Dep): deps.Dep = dep.dep
}
