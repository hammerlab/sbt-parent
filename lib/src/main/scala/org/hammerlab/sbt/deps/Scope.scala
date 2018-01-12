package org.hammerlab.sbt.deps

sealed trait Scope
  extends SimpleToString

object Scope {
  object Compile extends Scope
  object Test extends Scope
  object Provided extends Scope
}
