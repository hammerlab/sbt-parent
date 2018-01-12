# org.hammerlab.sbt:github

```scala
addSbtPlugin("org.hammerlab.sbt" % "github" % "4.0.0")
```

Some helpers for setting projects' `scmInfo` key:

```scala
github("my-org", "repo-name")
```

If an org-level plugin sets the `githubUser` key:

```scala
githubUser := "my-org"
// or:
github.user("my-org")
```

then downstream projects may just set the repository portion:

```scala
github.repo("repo-name")
```

