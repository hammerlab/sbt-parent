package org.hammerlab.sbt.deps

trait SimpleToString {
  override def toString: String =
    getClass
      .getSimpleName
      .toLowerCase
}

sealed trait Classifier
  extends SimpleToString

object Classifier {
  object Compile extends Classifier
  object Tests extends Classifier
}
