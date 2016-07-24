# sbt-scalaprops

[![Build Status](https://travis-ci.org/scalaprops/sbt-scalaprops.svg?branch=master)](https://travis-ci.org/scalaprops/sbt-scalaprops)

sbt plugin for [scalaprops](https://github.com/scalaprops/scalaprops)

sbt-scalaprops provides `scalapropsOnly: InputKey[Unit]` command like `testOnly` but more powerful.
__powerful__ means not only test class names but also test method names.

### latest stable version

`project/scalaprops.sbt`

```scala
addSbtPlugin("com.github.scalaprops" % "sbt-scalaprops" % "0.1.1")
```

#### JVM

`build.sbt`

```scala
scalapropsSettings

scalapropsVersion := "0.3.3"
```

or

```scala
scalapropsWithScalazlaws

scalapropsVersion := "0.3.3"
```

#### Scala.js

`build.sbt`

```scala
scalapropsCoreSettings

libraryDependencies += "com.github.scalaprops" %%% "scalaprops" % "0.3.3" % "test"
```

or

```scala
scalapropsCoreSettings

val scalapropsVersion = "0.3.3"

libraryDependencies += "com.github.scalaprops" %%% "scalaprops" % scalapropsVersion % "test"
libraryDependencies += "com.github.scalaprops" %%% "scalaprops-scalazlaws" % scalapropsVersion % "test"
```


- [API Documentation](https://oss.sonatype.org/service/local/repositories/releases/archive/com/github/scalaprops/sbt-scalaprops_2.10_0.13/0.1.1/sbt-scalaprops-0.1.1-javadoc.jar/!/index.html)


![screencast](https://raw.githubusercontent.com/scalaprops/sbt-scalaprops/master/screencast.gif)
