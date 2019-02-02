package org.hammerlab.sbt

import org.hammerlab.sbt.deps.Group.groupFromString

/**
 * A few aliases that are used in other plugins, and so must live deeper in the dependency-tree than the `Parent`
 * plugin, where most dependency-aliases are defined.
 */
object aliases
  extends Container {

  object slf4j
    extends Libs(
      "org.clapper" ^^ "grizzled-slf4j" ^ "1.3.1"
    ) {
    val grizzled = lib
    val   simple = lib("org.slf4j"      ^          "slf4j-simple"  ^ "1.7.25"      )
    val    log4j = lib("org.slf4j"      ^          "slf4j-log4j12" ^ "1.7.21"      )
    val     test = lib("uk.org.lidalia" ^          "slf4j-test"    ^ "1.1.0" tests )
    val slogging = lib("biz.enef"      ^^ "slogging-slf4j"         ^ "0.6.1"       )
  }

  val hadoop = Lib(("org.apache.hadoop" ^ "hadoop-client" ^ "2.7.3") - slf4j.log4j)
  val   kryo = Lib("com.esotericsoftware.kryo" ^ "kryo" ^ "2.24.0")
}
