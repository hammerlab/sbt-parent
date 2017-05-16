package org.hammerlab.sbt

import sbt.{ Command, Project, ScopedKey, State, Task }

object Util {
  /**
   * Helper for running [[Task]]s from within [[Command]]s.
   */
  def runTask[T](taskKey: ScopedKey[Task[T]], state: State): State =
    Project
      .runTask(taskKey, state)
      .map(_._1)
      .getOrElse(state)

  def propOrElse(keys: String*)(default: String): String =
    prop(keys: _*).getOrElse(default)

  def prop(keys: String*): Option[String] =
    keys
      .flatMap(key ⇒ Option(System.getProperty(key)).toSeq)
      .headOption
}
