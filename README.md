# sbt-scalaprops

[![Build Status](https://travis-ci.org/scalaprops/sbt-scalaprops.svg?branch=master)](https://travis-ci.org/scalaprops/sbt-scalaprops)

sbt plugin for [scalaprops](https://github.com/scalaprops/scalaprops)

sbt-scalaprops provides `scalapropsOnly: InputKey[Unit]` command like `testOnly` but more powerful.
__powerful__ means not only test class names but also test method names and [params](https://github.com/scalaprops/sbt-scalaprops/commit/c50e82740382eac812be2647e3d1d0f2c192e113).

### latest stable version

`project/scalaprops.sbt`

```scala
addSbtPlugin("com.github.scalaprops" % "sbt-scalaprops" % "0.3.2")
```

#### JVM

`build.sbt`

```scala
scalapropsSettings

scalapropsVersion := "0.8.0"
```

or

```scala
scalapropsWithScalaz

scalapropsVersion := "0.8.0"
```

#### Scala.js

`build.sbt`

```scala
scalapropsCoreSettings

libraryDependencies += "com.github.scalaprops" %%% "scalaprops" % "0.8.0" % "test"
```

or

```scala
scalapropsCoreSettings

val scalapropsVersion = "0.8.0"

libraryDependencies += "com.github.scalaprops" %%% "scalaprops" % scalapropsVersion % "test"
libraryDependencies += "com.github.scalaprops" %%% "scalaprops-scalaz" % scalapropsVersion % "test"
```


- [API Documentation](https://oss.sonatype.org/service/local/repositories/releases/archive/com/github/scalaprops/sbt-scalaprops_2.12_1.0/0.3.2/sbt-scalaprops-0.3.2-javadoc.jar/!/scalaprops/index.html)


![screencast](https://raw.githubusercontent.com/scalaprops/sbt-scalaprops/master/screencast.gif)
