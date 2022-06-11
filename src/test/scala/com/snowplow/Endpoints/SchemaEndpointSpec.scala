package com.snowplow.Endpoints

import cats.effect.IO
import cats.effect.unsafe.implicits.global
import com.snowplow.Database.{Schema, SchemasDao}
import io.circe.Json
import io.circe.parser.parse
import org.http4s.Method.GET
import org.http4s.dsl.io.POST
import org.http4s.implicits.{http4sKleisliResponseSyntaxOptionT, http4sLiteralsSyntax}
import org.http4s.{Request, Status, Uri}
import org.scalamock.scalatest.MockFactory
import org.scalatest.funspec.AnyFunSpecLike
import org.scalatest.matchers.must.Matchers
import org.scalatest.matchers.should.Matchers.convertToAnyShouldWrapper

import scala.concurrent.Future

class SchemaEndpointSpec extends AnyFunSpecLike with Matchers with MockFactory{

  val validJson: String =
    """
      |{
      |  "$schema": "http://json-schema.org/draft-04/schema#",
      |  "type": "object",
      |  "properties": {
      |    "source": {
      |      "type": "string"
      |    },
      |    "destination": {
      |      "type": "string"
      |    },
      |    "timeout": {
      |      "type": "integer",
      |      "minimum": 0,
      |      "maximum": 32767
      |    },
      |    "chunks": {
      |      "type": "object",
      |      "properties": {
      |        "size": {
      |          "type": "integer"
      |        },
      |        "number": {
      |          "type": "integer"
      |        }
      |      },
      |      "required": ["size"]
      |    }
      |  },
      |  "required": ["source", "destination"]
      |}
      |""".stripMargin

  val schemaId = "someId"
  implicit val ec: scala.concurrent.ExecutionContext = scala.concurrent.ExecutionContext.global

  describe("SchemaEndpoint") {
    val endpoint = SchemaEndpoint[IO]().routes
    val db = stub[SchemasDao]
    it("should accept valid json") {
      (db.insertSchema _).when(Schema(schemaId, validJson)).returns(Future[Int](1))
      val request =
        Request[IO](POST,Uri.fromString(s"http://localhost/$schemaId").getOrElse(uri""))
          .withEntity(validJson)

      endpoint.orNotFound(request).unsafeRunSync().status shouldBe Status.Created
    }

    it("should return bad request for invalid json") {

      (db.getSchemaById _).when(schemaId).returns(Future[Option[Json]](Some(parse(validJson).getOrElse(Json.Null))))

      val request =
        Request[IO](POST,Uri.fromString(s"http://localhost/$schemaId").getOrElse(uri""))
          .withEntity(validJson.replace('"', '&'))

      endpoint.orNotFound(request).unsafeRunSync().status shouldBe Status.BadRequest
    }

    it("should return the correct Json Schema") {
      val request =
        Request[IO](GET,Uri.fromString(s"http://localhost/$schemaId").getOrElse(uri""))

      val response = endpoint.orNotFound(request).unsafeRunSync()


      response.status shouldBe Status.Ok
    }
  }
}
