package com.snowplow.Endpoints

import cats.effect.kernel.{Async, Sync}
import cats.implicits.catsSyntaxOptionId
import cats.syntax.flatMap._
import cats.syntax.functor._
import com.snowplow.Database.SchemasDao
import com.snowplow.Domain.HttpResponses.Result
import com.snowplow.JsonSchema.JsonSchema
import com.typesafe.scalalogging.LazyLogging
import io.circe.parser.parse
import io.circe.syntax.EncoderOps
import org.http4s.HttpRoutes
import org.http4s.circe._
import org.http4s.dsl.Http4sDsl

case class ValidateEndpoint[F[_]: Sync]()(implicit F: Async[F]) extends Http4sDsl[F] with LazyLogging {

  def routes: HttpRoutes[F] = {
    lazy val dao = new SchemasDao
    HttpRoutes.of[F] {
      case req @ POST -> (Root / schemaId) =>
        Async[F].fromFuture(F.delay(dao.getSchemaById(schemaId))) flatMap {
          case None =>
            NotFound(Result("validateDocument", schemaId, "error", s"Schema with id $schemaId not found!".some).asJson)
          case Some(schema) =>
            req
              .as[String]
              .map(parse)
              .flatMap {
                case Left(value) => BadRequest(Result("validateDocument", schemaId, "error", value.message.some).asJson)
                case Right(json) =>
                  JsonSchema
                    .validate(schema.toString(), json)
                    .fold(Ok(Result("validateDocument", schemaId, "success").asJson.deepDropNullValues))(
                      validationResult =>
                        BadRequest(Result("validateDocument", schemaId, "success", validationResult.some).asJson)
                    )
              }
        }
    }
  }
}
