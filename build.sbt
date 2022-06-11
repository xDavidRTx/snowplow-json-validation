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
  Library.JsonValidator,
  Library.Hsqldb,
  Library.Postgresql,
  Library.Slick,
  Library.SlickHikaricp,
  Library.ScalaMock,
  Library.Logback,
  Library.ScalaLogging,
)

enablePlugins(FlywayPlugin)

flywayUrl := "jdbc:postgresql://localhost:5432/postgres"
flywayUser := "postgres"
flywayPassword := "docker"
flywayLocations += "db/migration"
