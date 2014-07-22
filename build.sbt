name := """play-slick-quickstart"""

version := "1.0-SNAPSHOT"

libraryDependencies ++= {
  val titanV = "0.5.0-M1"
  val tinkerpopV = "2.5.0"
  Seq(
    "org.webjars" %% "webjars-play" % "2.2.2",
    "com.thinkaurelius.titan" % "titan-cassandra" % titanV,
    "com.thinkaurelius.titan" % "titan-es" % titanV,
    "com.tinkerpop.blueprints" % "blueprints-core" % tinkerpopV,
    "com.michaelpollmeier" %% "gremlin-scala" % tinkerpopV,
    "com.github.nscala-time" %% "nscala-time" % "1.2.0",
    "io.argonaut" %% "argonaut" % "6.0.4",
  "org.reflections" % "reflections" % "0.9.8" notTransitive ()
  )
}

fork in Test := false

lazy val root = (project in file(".")).enablePlugins(PlayScala)