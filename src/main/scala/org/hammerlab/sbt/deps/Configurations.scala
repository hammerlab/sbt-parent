package org.hammerlab.sbt.deps

trait Configurations {
  def configurations: Seq[Configuration]
  override def toString: String =
    configurations
      .mkString(",")
}

object Configurations {

  // Short-hand for a commonly-used pair of configurations: compile->compile,test->test
  object CompileTest
    extends Configurations {
    def configurations =
      Seq(
        Configuration.Default,
        Configuration.Test
      )
  }

  implicit def apply(configs: Seq[Configuration]): Configurations =
    new Configurations {
      def configurations = configs
    }

  implicit def apply(config: Configuration): Configurations =
    apply(Seq(config))

  implicit def unwrapConfigurations(configurations: Configurations): Seq[Configuration] =
    configurations.configurations
}
