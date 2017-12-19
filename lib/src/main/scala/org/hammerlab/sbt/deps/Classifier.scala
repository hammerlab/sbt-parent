package org.hammerlab.sbt.deps

trait SimpleToString {
  override def toString: String =
    getClass
      .getSimpleName
      .toLowerCase match {
      case x
        if x.endsWith("$") ⇒
          x.dropRight(1)
      case x ⇒ x
    }
}

sealed trait Classifier
  extends SimpleToString

object Classifier {
  object Default extends Classifier
  object Compile extends Classifier
  object Tests extends Classifier
}
