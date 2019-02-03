package org.hammerlab.sbt.keys

import sbt.{ Setting, SettingKey, TaskKey, ThisBuild }
import syntax._

trait syntax {
  @inline implicit def    makeTaskOptionOps[T](key:    TaskKey[Option[T]]):    TaskOptionOps[T] =    TaskOptionOps(key)
  @inline implicit def makeSettingOptionOps[T](key: SettingKey[Option[T]]): SettingOptionOps[T] = SettingOptionOps(key)
}

object syntax {
  implicit class TaskOps[T](val key: TaskKey[T]) extends AnyVal {
    def  *(t: T): Setting[_] = key in ThisBuild := t
  }
  implicit class SettingOps[T](val key: SettingKey[T]) extends AnyVal {
    def  *(t: T): Setting[_] = key in ThisBuild := t
  }

  implicit class TaskOptionOps[T](val key: TaskKey[Option[T]]) extends AnyVal {
    def :=(t: T): Setting[_] = key              := Some(t)
    def  *(t: T): Setting[_] = key in ThisBuild := Some(t)
  }
  implicit class SettingOptionOps[T](val key: SettingKey[Option[T]]) extends AnyVal {
    def :=(t: T): Setting[_] = key              := Some(t)
    def  *(t: T): Setting[_] = key in ThisBuild := Some(t)
  }
}
