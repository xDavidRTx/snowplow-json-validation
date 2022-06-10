package com.snowplow

import cats.effect.{Async, Sync}
import cats.implicits._
import com.snowplow.Domain.HttpResponses.Result
import com.snowplow.JsonSchema.JsonSchema.parseJson
import io.circe.syntax.EncoderOps
import org.http4s.circe.CirceEntityCodec.circeEntityEncoder
import org.http4s.dsl.Http4sDsl
import org.http4s.{EntityDecoder, HttpRoutes}

case class SchemaEndpoint[F[_] : Sync]()(implicit F: Async[F]) extends Http4sDsl[F] {

  def schemaRoutes: HttpRoutes[F] = {
    HttpRoutes.of[F] {
      case req@POST -> schemaId =>
        for {
          requestBody <- EntityDecoder.decodeText(req)
          result <- parseJson(requestBody) match {
            case None => BadRequest(Result("uploadSchema", schemaId.toString(), "error", Some("Invalid JSON")).asJson)
            case Some(json) =>
              //Save in DB
              Created(Result("uploadSchema", schemaId.toString(), "success"))
          }
        } yield result
    }
  }

}
