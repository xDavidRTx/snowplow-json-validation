import sbt._

object Version {
  final val Scala         = "2.13.8"
  final val ScalaTest     = "3.2.12"
  final val Http4sVersion = "1.0.0-M21"
  final val CirceVersion  = "0.14.1"
  final val JsonValidator = "2.2.14"
  final val Hsqldb        = "2.6.1"
  final val Postgresql    = "42.3.6"
  final val Slick         = "3.3.3"
  final val ScalaMock     = "5.2.0"
  final val Logback       = "1.2.11"
  final val ScalaLogging  = "3.9.4"
}

object Library {
  val Http4sBlazeServer = "org.http4s"                 %% "http4s-blaze-server"  % Version.Http4sVersion
  val Http4sCirce       = "org.http4s"                 %% "http4s-circe"         % Version.Http4sVersion
  val Http4sDsl         = "org.http4s"                 %% "http4s-dsl"           % Version.Http4sVersion
  val Circe             = "io.circe"                   %% "circe-generic"        % Version.CirceVersion
  val CirceParser       = "io.circe"                   %% "circe-parser"         % Version.CirceVersion
  val ScalaTest         = "org.scalatest"              %% "scalatest"            % Version.ScalaTest % Test
  val JsonValidator     = "com.github.java-json-tools" % "json-schema-validator" % Version.JsonValidator
  val Hsqldb            = "org.hsqldb"                 % "hsqldb"                % Version.Hsqldb
  val Postgresql        = "org.postgresql"             % "postgresql"            % Version.Postgresql
  val Slick             = "com.typesafe.slick"         %% "slick"                % Version.Slick
  val SlickHikaricp     = "com.typesafe.slick"         %% "slick-hikaricp"       % Version.Slick
  val ScalaMock         = "org.scalamock"              %% "scalamock"            % Version.ScalaMock % Test
  val Logback           = "ch.qos.logback"             % "logback-classic"       % Version.Logback % Runtime
  val ScalaLogging      = "com.typesafe.scala-logging" %% "scala-logging"        % Version.ScalaLogging
}
