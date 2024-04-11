# sbt-scalaprops

sbt plugin for [scalaprops](https://github.com/scalaprops/scalaprops)

sbt-scalaprops provides `scalapropsOnly: InputKey[Unit]` command like `testOnly` but more powerful.
__powerful__ means not only test class names but also test method names and [params](https://github.com/scalaprops/sbt-scalaprops/commit/c50e82740382eac812be2647e3d1d0f2c192e113).

### latest stable version

`project/scalaprops.sbt`

```scala
addSbtPlugin("com.github.scalaprops" % "sbt-scalaprops" % "0.5.0")
```

#### JVM

`build.sbt`

```scala
scalapropsSettings

scalapropsVersion := "0.9.1"
```

or

```scala
scalapropsWithScalaz

scalapropsVersion := "0.9.1"
```

#### Scala.js

`build.sbt`

```scala
scalapropsCoreSettings

libraryDependencies += "com.github.scalaprops" %%% "scalaprops" % "0.9.1" % "test"
```

or

```scala
scalapropsCoreSettings

val scalapropsVersion = "0.9.1"

libraryDependencies += "com.github.scalaprops" %%% "scalaprops" % scalapropsVersion % "test"
libraryDependencies += "com.github.scalaprops" %%% "scalaprops-scalaz" % scalapropsVersion % "test"
```


- [API Documentation](https://oss.sonatype.org/service/local/repositories/releases/archive/com/github/scalaprops/sbt-scalaprops_2.12_1.0/0.5.0/sbt-scalaprops-0.5.0-javadoc.jar/!/scalaprops/index.html)


![screencast](https://raw.githubusercontent.com/scalaprops/sbt-scalaprops/master/screencast.gif)
