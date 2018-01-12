package org.hammerlab.sbt.plugin

import com.typesafe.sbt.SbtScalariform.autoImport.scalariformAutoformat
import com.typesafe.sbt.SbtScalariform.ScalariformKeys._
import sbt.Def

import scalariform.formatter.preferences._

object Scalariform
  extends Plugin {

  object autoImport {
    val enableScalariform =
      Seq(
          preferences :=
            preferences
              .value
              .setPreference(AlignParameters, true)
              .setPreference(CompactStringConcatenation, false)
              .setPreference(AlignSingleLineCaseStatements, true)
              .setPreference(DoubleIndentConstructorArguments, true)
              .setPreference(DanglingCloseParenthesis, Preserve)
      )
  }

  override def projectSettings: Seq[Def.Setting[_]] =
    Seq(
      scalariformAutoformat := false
    )
}
