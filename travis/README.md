# org.hammerlab.sbt:travis

[![Maven Central](https://img.shields.io/badge/maven%20central-4.1.0-green.svg)](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22org.hammerlab.sbt%22%20a%3A%travis%22)

```scala
addSbtPlugin("org.hammerlab.sbt" % "travis" % "4.1.0")
```

SBT settings for interfacing with Coveralls and TravisCI:

- `travisCoverageScalaVersion`: only compute coverage and send a report to Coveralls if `TRAVIS_SCALA_VERSION` matches this value (default: `scala211Version`) and `coveralls.disable` system property is not set
- `coverageTest`: command wrapping `test` and, if scoverage is enabled, `coverageReport` for preparing reports
- `travis-report` command suitable for `.travis.yml` `after_success`:
	- if coverage is enabled, send report to Coveralls
	- if this is a multi-module project, run `coverageAggregate` first 

See [hammerlab/math-utils](https://github.com/hammerlab/math-utils/blob/stats-1.1.1/.travis.yml) for an example.
