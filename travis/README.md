# org.hammerlab.sbt:travis

[![org.hammerlab.sbt:travis](https://img.shields.io/badge/org.hammerlab.sbt:travis-5.0.0-green.svg)](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22org.hammerlab.sbt%22%20a%3A%22travis%22)

```scala
addSbtPlugin("org.hammerlab.sbt" % "travis" % "5.0.0")
```

SBT settings for interfacing with Coveralls and TravisCI:

- `travisCoverageScalaVersion`: only compute coverage and send a report to Coveralls if `TRAVIS_SCALA_VERSION` matches this value (default: `scala211Version`) and `coveralls.disable` system property is not set
- `coverageTest`: command wrapping `test` and, if scoverage is enabled, `coverageReport` for preparing reports
- `travisReport` command suitable for `.travis.yml` `after_success`:
	- if coverage is enabled, send report to Coveralls
	- if this is a multi-module project, run `coverageAggregate` first 

See [hammerlab/math-utils](https://github.com/hammerlab/math-utils/blob/stats-1.3.2/.travis.yml#L13-L14) for an example.
