package com.snowplow

import cats.effect._
import com.snowplow.Endpoints.SchemaEndpoint
import org.http4s.implicits._
import org.http4s.server._
import org.http4s.server.blaze.BlazeServerBuilder

import scala.util.Try

object ValidationService extends IOApp {
  override def run(args: List[String]): IO[ExitCode] = {
    BlazeServerBuilder[IO](runtime.compute)
      .bindHttp(Try(sys.env("PORT").toInt).getOrElse(8080), "0.0.0.0")
      .withHttpApp(Router(
        "/schema" -> SchemaEndpoint[IO].routes
      ).orNotFound)
      .resource
      .use(_ => IO.never)
      .as(ExitCode.Success)
  }
}
