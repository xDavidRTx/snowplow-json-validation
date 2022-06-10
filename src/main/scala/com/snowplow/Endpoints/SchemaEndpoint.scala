package com.snowplow.Endpoints

import cats.effect.kernel.{Async, Sync}
import cats.syntax.flatMap._
import cats.syntax.functor._
import com.snowplow.Domain.HttpResponses.Result
import io.circe.parser.parse
import io.circe.syntax.EncoderOps
import org.http4s.HttpRoutes
import org.http4s.circe._
import org.http4s.dsl.Http4sDsl

case class SchemaEndpoint[F[_] : Sync]()(implicit F: Async[F]) extends Http4sDsl[F] {

  def routes: HttpRoutes[F] = {
    HttpRoutes.of[F] {
      case req@POST -> (Root / schemaId) =>
        for {
          requestBody <- req.as[String]
          result <- parse(requestBody) match {
            case Left(_) =>
              BadRequest(Result("uploadSchema", schemaId, "error", Some("Invalid JSON")).asJson.deepDropNullValues)
            case Right(json) =>
              //Save in DB
              Created(Result("uploadSchema", schemaId, "success").asJson.deepDropNullValues)
          }
        } yield result
    }
  }

}
