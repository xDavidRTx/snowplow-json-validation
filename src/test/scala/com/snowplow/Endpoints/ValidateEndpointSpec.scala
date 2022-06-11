package com.snowplow.Endpoints

import cats.effect.IO
import cats.effect.unsafe.implicits.global
import cats.implicits.catsSyntaxOptionId
import com.snowplow.Common.{json, schema, schemaId}
import com.snowplow.Database.SchemasDao
import io.circe.Json
import io.circe.parser.parse
import org.http4s.dsl.io.POST
import org.http4s.implicits.{http4sKleisliResponseSyntaxOptionT, http4sLiteralsSyntax}
import org.http4s.{Request, Status, Uri}
import org.scalamock.scalatest.MockFactory
import org.scalatest.funspec.AnyFunSpecLike
import org.scalatest.matchers.must.Matchers
import org.scalatest.matchers.should.Matchers.convertToAnyShouldWrapper

import scala.concurrent.Future

class ValidateEndpointSpec extends AnyFunSpecLike with Matchers with MockFactory {
  implicit val ec: scala.concurrent.ExecutionContext = scala.concurrent.ExecutionContext.global

  describe("ValidateEndpoint") {
    val db       = stub[SchemasDao]
    val endpoint = ValidateEndpoint[IO](db).routes

    it("should handle a valid json") {
      (db.getSchemaById _).when(schemaId).returns(Future[Option[Json]](parse(schema).getOrElse(Json.Null).some))

      val request =
        Request[IO](POST, Uri.fromString(s"http://localhost/$schemaId").getOrElse(uri""))
          .withEntity(json)

      val response = endpoint.orNotFound(request).unsafeRunSync()

      response.status shouldBe Status.Ok
    }

    it("should handle an invalid json") {
      (db.getSchemaById _)
        .when(schemaId)
        .returns(Future[Option[Json]](parse(schema.replace("source", "timeout")).getOrElse(Json.Null).some))

      val request =
        Request[IO](POST, Uri.fromString(s"http://localhost/$schemaId").getOrElse(uri""))
          .withEntity(json)

      val response = endpoint.orNotFound(request).unsafeRunSync()

      response.status shouldBe Status.BadRequest
    }

    it("should handle non json payload") {
      (db.getSchemaById _)
        .when(schemaId)
        .returns(Future[Option[Json]](parse(schema.replace("source", "timeout")).getOrElse(Json.Null).some))

      val request =
        Request[IO](POST, Uri.fromString(s"http://localhost/$schemaId").getOrElse(uri""))
          .withEntity("randomPayload")

      val response = endpoint.orNotFound(request).unsafeRunSync()

      response.status shouldBe Status.NotAcceptable
    }
  }
}
