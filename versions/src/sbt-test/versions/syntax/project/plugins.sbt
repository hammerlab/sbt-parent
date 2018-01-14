sys.props.get("plugin.version") match {
  case Some(v) ⇒ addSbtPlugin("org.hammerlab.sbt" % "versions" % v)
  case _ ⇒ sys.error("""|The system property 'plugin.version' is not defined.
                        |Specify this property using the scriptedLaunchOpts -D.""".stripMargin)
}

addSbtPlugin("org.hammerlab.sbt" % "root" % "4.0.1")
addSbtPlugin("com.jsuereth" % "sbt-pgp" % "1.1.0")
