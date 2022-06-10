package com.snowplow.Endpoints

import cats.effect.IO
import cats.effect.unsafe.implicits.global
import com.snowplow.SchemaEndpoint
import org.http4s.dsl.io.POST
import org.http4s.implicits.{http4sKleisliResponseSyntaxOptionT, http4sLiteralsSyntax}
import org.http4s.{Request, Status, Uri}
import org.scalatest.funspec.AnyFunSpecLike
import org.scalatest.matchers.must.Matchers
import org.scalatest.matchers.should.Matchers.convertToAnyShouldWrapper

class SchemaEndpointSpec extends AnyFunSpecLike with Matchers {

  val validJson =
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

  val schemaId = "SomeId"

  describe("SchemaEndpoint") {
    val endpoint = SchemaEndpoint[IO]().schemaRoutes
    it("should accept valid json") {
      val request =
        Request[IO](POST, Uri.fromString(schemaId).getOrElse(uri""))
          .withEntity(validJson)

      endpoint.orNotFound(request).unsafeRunSync().status shouldBe Status.Created
    }

    it("should return bad request for invalid json") {
      val request =
        Request[IO](POST, Uri.fromString(schemaId).getOrElse(uri""))
          .withEntity(validJson.replace('"', '&'))

      endpoint.orNotFound(request).unsafeRunSync().status shouldBe Status.BadRequest
      println(endpoint.orNotFound(request).unsafeRunSync())
    }
  }
}
