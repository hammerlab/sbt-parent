//addSbtPlugin("com.github.daniel-shuy" % "sbt-scripted-scalatest" % "1.1.0")
//libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.5"
addSbtPlugin("org.hammerlab.sbt" % "scripted" % "1.0.0-SNAPSHOT")

sys.props.get("plugin.version") match {
  case Some(v) ⇒ addSbtPlugin("org.hammerlab.sbt" % "base" % v)
  case _ ⇒ sys.error("""|The system property 'plugin.version' is not defined.
                         |Specify this property using the scriptedLaunchOpts -D.""".stripMargin)
}
