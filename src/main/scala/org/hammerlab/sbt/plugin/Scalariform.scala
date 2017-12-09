package org.hammerlab.sbt.plugin

import com.typesafe.sbt.SbtScalariform.ScalariformKeys.preferences

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
}
