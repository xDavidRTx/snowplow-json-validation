import sbt._

object Version {
  final val Scala = "2.13.8"
  final val ScalaTest = "3.2.12"
  final val Http4sVersion = "1.0.0-M21"
  final val CirceVersion = "0.14.1"
  final val JsonValidator =  "2.2.14"
}

object Library {
  val Http4sBlazeServer = "org.http4s"      %% "http4s-blaze-server" % Version.Http4sVersion
  val Http4sCirce = "org.http4s"      %% "http4s-circe"        % Version.Http4sVersion
  val Http4sDsl = "org.http4s"      %% "http4s-dsl"      % Version.Http4sVersion
  val Circe = "io.circe"        %% "circe-generic"       % Version.CirceVersion
  val CirceParser = "io.circe" %% "circe-parser" % Version.CirceVersion
  val ScalaTest = "org.scalatest" %% "scalatest" % Version.ScalaTest % "test"
  val JsonValidator = "com.github.java-json-tools" % "json-schema-validator" % Version.JsonValidator

}