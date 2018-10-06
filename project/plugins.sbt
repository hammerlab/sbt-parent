addSbtPlugin("org.xerial.sbt" % "sbt-sonatype" % "2.0")
addSbtPlugin("com.jsuereth" % "sbt-pgp" % "1.1.1")
addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "0.14.6")
addSbtPlugin("com.typesafe.sbt" % "sbt-git" % "0.9.3")
addSbtPlugin("io.get-coursier" % "sbt-coursier" % "1.0.2")

// Bootstrap ourselves; this is a useful plugin for publishing plugins!
addSbtPlugin("org.hammerlab.sbt" % "base" % "4.6.4")
