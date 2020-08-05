package org.hammerlab.sbt.deps

class Configuration(val scope: Scope, val classifier: Classifier) {
  override def toString: String =
    s"$scope->$classifier"
}

object Configuration {

  def unapply(configuration: Configuration): Option[(Scope, Classifier)] =
    Some(
      configuration.scope →
        configuration.classifier
    )

  case object Default extends Configuration(Scope.Compile, Classifier.Default)
  case object Test extends Configuration(Scope.Test, Classifier.Default)
  case object TestTest extends Configuration(Scope.Test, Classifier.Tests)
  case object Provided extends Configuration(Scope.Provided, Classifier.Default)

  implicit def metadataToScope(metadata: Configuration): Scope = metadata.scope
  implicit def metadataToClassifier(metadata: Configuration): Classifier = metadata.classifier

}
