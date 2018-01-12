package org.hammerlab.sbt.deps

sealed trait Configuration {
  def scope: Scope
  def classifier: Classifier
  override def toString: String =
    s"$scope->$classifier"
}

object Configuration {

  def unapply(configuration: Configuration): Option[(Scope, Classifier)] =
    Some(
      configuration.scope →
        configuration.classifier
    )

  case object Default
    extends Configuration {
    def scope = Scope.Compile
    def classifier = Classifier.Default
  }

  case object Test
    extends Configuration {
    def scope = Scope.Test
    def classifier = Classifier.Default
  }

  case object TestTest
    extends Configuration {
    def scope = Scope.Test
    def classifier = Classifier.Tests
  }

  case object Provided
    extends Configuration {
    override def scope: Scope = Scope.Provided
    override def classifier: Classifier = Classifier.Default
  }

  implicit def metadataToScope(metadata: Configuration): Scope = metadata.scope
  implicit def metadataToClassifier(metadata: Configuration): Classifier = metadata.classifier

}
