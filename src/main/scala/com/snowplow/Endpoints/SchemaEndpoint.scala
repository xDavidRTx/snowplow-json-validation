package com.snowplow.Endpoints

import cats.effect.kernel.{Async, Sync}
import cats.implicits.catsSyntaxOptionId
import cats.syntax.flatMap._
import cats.syntax.functor._
import com.snowplow.Database.{Schema, SchemasDao}
import com.snowplow.Domain.HttpResponses.Result
import com.typesafe.scalalogging.LazyLogging
import io.circe.parser.parse
import io.circe.syntax.EncoderOps
import org.http4s.HttpRoutes
import org.http4s.circe._
import org.http4s.dsl.Http4sDsl

case class SchemaEndpoint[F[_]: Sync]()(implicit F: Async[F]) extends Http4sDsl[F] with LazyLogging {

  def routes: HttpRoutes[F] = {
    lazy val dao = new SchemasDao
    HttpRoutes.of[F] {
      case GET -> (Root / schemaId) =>
        Async[F].fromFuture(F.delay(dao.getSchemaById(schemaId))) flatMap {
          case None =>
            NotFound(Result("downloadSchema", schemaId, "error", s"Schema with id $schemaId not found!".some).asJson)
          case Some(value) => Ok(value)
        }
      case req @ POST -> (Root / schemaId) =>
        for {
          requestBody <- req.as[String]
          result <- parse(requestBody) match {
            case Left(_) =>
              BadRequest(Result("uploadSchema", schemaId, "error", "Invalid JSON".some).asJson.deepDropNullValues)
            case Right(json) =>
              val schema = Schema(schemaId, json.toString())
              Async[F].fromFuture(F.delay(dao.insertSchema(schema))) flatMap {
                case 1 => Created(Result("uploadSchema", schemaId, "success").asJson.deepDropNullValues)
                case _ =>
                  logger.error(s"The Schema $schema was not stored in the DB")
                  InternalServerError(
                    Result("uploadSchema", schemaId, "error", "Something when wrong when saving the schema".some).asJson
                  )
              }
          }
        } yield result
    }
  }
}
