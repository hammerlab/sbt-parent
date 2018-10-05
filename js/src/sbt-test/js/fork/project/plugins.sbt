sys.props.get("plugin.version") match {
  case Some(v) => addSbtPlugin("org.hammerlab.sbt" % "js" % v)
  case _ => sys.error("""|The system property 'plugin.version' is not defined.
                         |Specify this property using the scriptedLaunchOpts -D.""".stripMargin)
}

addSbtPlugin("org.hammerlab.sbt" % "parent" % "4.6.4-SNAPSHOT")
