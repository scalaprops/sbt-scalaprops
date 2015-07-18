# sbt-scalaprops

[![Build Status](https://secure.travis-ci.org/scalaprops/sbt-scalaprops.png)](http://travis-ci.org/scalaprops/sbt-scalaprops)

sbt plugin for [scalaprops](https://github.com/scalaprops/scalaprops)

sbt-scalaprops provides `scalapropsOnly: InputKey[Unit]` command like `testOnly` but more powerful.
__powerful__ means not only test class names but also test method names.

### latest stable version

`project/scalaprops.sbt`

```scala
addSbtPlugin("com.github.scalaprops" % "sbt-scalaprops" % "0.1.0")
```

`build.sbt`

```scala
scalapropsSettings

scalapropsVersion := "0.1.11"
```

or

```scala
scalapropsWithScalazlaws

scalapropsVersion := "0.1.11"
```


- [API Documentation](https://oss.sonatype.org/service/local/repositories/releases/archive/com/github/scalaprops/sbt-scalaprops_2.10_0.13/0.1.0/sbt-scalaprops-0.1.0-javadoc.jar/!/index.html)


![screencast](https://raw.githubusercontent.com/scalaprops/sbt-scalaprops/master/screencast.gif)
