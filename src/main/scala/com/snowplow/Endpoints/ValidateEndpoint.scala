package com.snowplow.Endpoints

import cats.effect.kernel.{Async, Sync}
import cats.syntax.flatMap._
import cats.syntax.functor._
import com.snowplow.Database.SchemasDao
import com.snowplow.Domain.{ErrorResponse, SuccessResponse}
import com.snowplow.JsonSchema.JsonSchema
import com.typesafe.scalalogging.LazyLogging
import io.circe.parser.parse
import io.circe.syntax.EncoderOps
import org.http4s.HttpRoutes
import org.http4s.circe._
import org.http4s.dsl.Http4sDsl

case class ValidateEndpoint[F[_]: Sync](dao: SchemasDao)(implicit F: Async[F]) extends Http4sDsl[F] with LazyLogging {

  def routes: HttpRoutes[F] =
    HttpRoutes.of[F] {
      case req @ POST -> (Root / schemaId) =>
        Async[F].fromFuture(F.delay(dao.getSchemaById(schemaId))) flatMap {
          case None =>
            NotFound(ErrorResponse("validateDocument", schemaId, s"Schema with id $schemaId not found!").asJson)
          case Some(schema) =>
            req
              .as[String]
              .map(parse)
              .flatMap {
                case Left(e) =>
                  logger.error("Invalid Json", e)
                  NotAcceptable(ErrorResponse("validateDocument", schemaId, "Invalid Json payload").asJson)
                case Right(json) =>
                  JsonSchema
                    .validate(schema.toString(), json)
                    .fold(Ok(SuccessResponse("validateDocument", schemaId).asJson.deepDropNullValues))(
                      validationResult =>
                        BadRequest(ErrorResponse("validateDocument", schemaId, validationResult).asJson)
                    )
              }
        }
    }
}
