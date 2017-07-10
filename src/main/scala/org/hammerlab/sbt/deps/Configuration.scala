package org.hammerlab.sbt.deps

sealed trait Configuration {
  def scope: Scope
  def classifier: Classifier
  override def toString: String =
    s"$scope->$classifier"
}

object Configuration {

  case object Default
    extends Configuration {
    def scope = Scope.Compile
    def classifier = Classifier.Compile
  }

  case object Test
    extends Configuration {
    def scope = Scope.Test
    def classifier = Classifier.Compile
  }

  case object TestTest
    extends Configuration {
    def scope = Scope.Test
    def classifier = Classifier.Tests
  }

  case object Provided
    extends Configuration {
    override def scope: Scope = Scope.Provided
    override def classifier: Classifier = Classifier.Compile
  }

  implicit def metadataToScope(metadata: Configuration): Scope = metadata.scope
  implicit def metadataToClassifier(metadata: Configuration): Classifier = metadata.classifier

}
