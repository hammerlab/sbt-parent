sys.props.get("plugin.version") match {
  case Some(v) ⇒ addSbtPlugin("org.hammerlab.sbt" % "travis" % v)
  case _ ⇒ sys.error("""|The system property 'plugin.version' is not defined.
                        |Specify this property using the scriptedLaunchOpts -D.""".stripMargin)
}
