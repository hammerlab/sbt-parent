package org.hammerlab.sbt

import org.hammerlab.sbt.deps.Group.groupFromString

/**
 * A few aliases that are used in other plugins, and so must live deeper in the dependency-tree than the `Parent`
 * plugin, where most dependency-aliases are defined.
 */
trait aliases {
  val log4j = "org.slf4j"  ^ "slf4j-log4j12"
  val test_logging = "uk.org.lidalia" ^ "slf4j-test" tests

  object hadoop extends Lib(("org.apache.hadoop" ^ "hadoop-client" ^ "2.7.3") - log4j)
  object   kryo extends Lib("com.esotericsoftware.kryo" ^ "kryo" ^ "2.24.0")
}
