ThisBuild / version := "0.1.0"

ThisBuild / scalaVersion := Version.Scala

lazy val root = (project in file("."))
  .settings(
    name := "snowplow-json-validation"
  )

libraryDependencies ++= Seq(
  Library.ScalaTest,
  Library.Circe,
  Library.Http4sDsl,
  Library.Http4sCirce,
  Library.CirceParser,
  Library.Http4sBlazeServer,
  Library.JsonValidator
)

enablePlugins(FlywayPlugin)

libraryDependencies += "org.hsqldb" % "hsqldb" % "2.6.1"
libraryDependencies += "org.postgresql" % "postgresql" % "42.3.6"
libraryDependencies += "com.typesafe.slick" %% "slick" % "3.3.3"
libraryDependencies += "org.postgresql" % "postgresql" % "42.3.6"
libraryDependencies +="com.typesafe.slick" %% "slick-hikaricp" % "3.3.3"
libraryDependencies += "org.scalamock" %% "scalamock" % "5.2.0" % Test

flywayUrl := "jdbc:postgresql://localhost:5432/postgres"
flywayUser := "postgres"
flywayPassword := "docker"
flywayLocations += "db/migration"