package org.hammerlab.sbt.keys

import sbt.{ Setting, SettingKey, TaskKey }
import syntax._

trait syntax {
  @inline implicit def makeOptionTaskOps   [T](key:    TaskKey[Option[T]]):    OptionTaskOps[T] =    OptionTaskOps(key)
  @inline implicit def makeOptionSettingOps[T](key: SettingKey[Option[T]]): OptionSettingOps[T] = OptionSettingOps(key)
}

object syntax {
  implicit class OptionTaskOps[T](val key: TaskKey[Option[T]]) extends AnyVal {
    def :=(t: T): Setting[_] = key := Some(t)
  }
  implicit class OptionSettingOps[T](val key: SettingKey[Option[T]]) extends AnyVal {
    def :=(t: T): Setting[_] = key := Some(t)
  }
}
