package com.snowplow.Endpoints

import cats.effect.kernel.{Async, Sync}
import cats.syntax.flatMap._
import cats.syntax.functor._
import com.snowplow.Database.{Schema, SchemasDao}
import com.snowplow.Domain.{ErrorResponse, SuccessResponse}
import com.typesafe.scalalogging.LazyLogging
import io.circe.parser.parse
import io.circe.syntax.EncoderOps
import org.http4s.HttpRoutes
import org.http4s.circe._
import org.http4s.dsl.Http4sDsl

case class SchemaEndpoint[F[_]: Sync](dao: SchemasDao)(implicit F: Async[F]) extends Http4sDsl[F] with LazyLogging {

  def routes: HttpRoutes[F] =
    HttpRoutes.of[F] {
      case GET -> (Root / schemaId) =>
        Async[F].fromFuture(F.delay(dao.getSchemaById(schemaId))) flatMap {
          case None =>
            logger.warn(s"$schemaId is not present in the DB")
            NotFound(ErrorResponse("downloadSchema", schemaId, s"Schema with id $schemaId not found!").asJson)
          case Some(value) => Ok(value)
        }
      case req @ POST -> (Root / schemaId) =>
        for {
          requestBody <- req.as[String]
          result <- parse(requestBody) match {
            case Left(e) =>
              logger.error(s"Unable to parse $requestBody", e)
              BadRequest(ErrorResponse("uploadSchema", schemaId, "Invalid JSON").asJson.deepDropNullValues)
            case Right(_) =>
              val schema = Schema(schemaId, requestBody)
              Async[F].fromFuture(F.delay(dao.insertSchema(schema))) flatMap {
                case 1 => Created(SuccessResponse("uploadSchema", schemaId).asJson.deepDropNullValues)
                case _ =>
                  logger.error(s"The Schema $schema was not stored in the DB")
                  InternalServerError(
                    ErrorResponse("uploadSchema", schemaId, "Something when wrong when saving the schema").asJson
                  )
              }
          }
        } yield result
    }
}
