package org.hammerlab.sbt.deps

case class Configurations(values: Configuration*) {
  override def toString = values.mkString(";")
}

object Configurations {
  implicit def make(configuration: Configuration): Configurations = Configurations(configuration)
  implicit def make(values: Seq[Configuration]): Configurations = Configurations(values: _*)
  implicit def unmake(configurations: Configurations): Seq[Configuration] = configurations.values
  val default = Configurations(Configuration.Default)
}
