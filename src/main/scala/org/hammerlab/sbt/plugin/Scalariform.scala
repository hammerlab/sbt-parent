package org.hammerlab.sbt.plugin

import com.typesafe.sbt.SbtScalariform
import com.typesafe.sbt.SbtScalariform.ScalariformKeys

import scalariform.formatter.preferences._

object Scalariform
  extends Plugin {

  object autoImport {
    val enableScalariform = (
      SbtScalariform.defaultScalariformSettings ++
        Seq(
          ScalariformKeys.preferences :=
            ScalariformKeys.preferences.value
              .setPreference(AlignParameters, true)
              .setPreference(CompactStringConcatenation, false)
              .setPreference(AlignSingleLineCaseStatements, true)
              .setPreference(DoubleIndentClassDeclaration, true)
              .setPreference(PreserveDanglingCloseParenthesis, true)
        )
      )
  }
}
