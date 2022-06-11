package com.snowplow.Endpoints

import cats.effect.IO
import cats.effect.unsafe.implicits.global
import com.snowplow.Common.{schema, schemaId}
import com.snowplow.Database.{Schema, SchemasDao}
import org.http4s.Method.GET
import org.http4s.dsl.io.POST
import org.http4s.implicits.{http4sKleisliResponseSyntaxOptionT, http4sLiteralsSyntax}
import org.http4s.{Request, Status, Uri}
import org.scalamock.scalatest.MockFactory
import org.scalatest.funspec.AnyFunSpecLike
import org.scalatest.matchers.must.Matchers
import org.scalatest.matchers.should.Matchers.convertToAnyShouldWrapper

import scala.concurrent.Future

class SchemaEndpointSpec extends AnyFunSpecLike with Matchers with MockFactory {
  implicit val ec: scala.concurrent.ExecutionContext = scala.concurrent.ExecutionContext.global

  describe("SchemaEndpoint") {
    val endpoint = SchemaEndpoint[IO]().routes
    val db       = stub[SchemasDao]
    it("should accept valid json") {
      (db.insertSchema _).when(Schema(schemaId, schema)).returns(Future[Int](1))
      val request =
        Request[IO](POST, Uri.fromString(s"http://localhost/$schemaId").getOrElse(uri""))
          .withEntity(schema)

      endpoint.orNotFound(request).unsafeRunSync().status shouldBe Status.Created
    }

    it("should return bad request for invalid json") {
      val request =
        Request[IO](POST, Uri.fromString(s"http://localhost/$schemaId").getOrElse(uri""))
          .withEntity(schema.replace('"', '&'))

      endpoint.orNotFound(request).unsafeRunSync().status shouldBe Status.BadRequest
    }

    it("should return the correct Json Schema") {
      val request =
        Request[IO](GET, Uri.fromString(s"http://localhost/$schemaId").getOrElse(uri""))

      val response = endpoint.orNotFound(request).unsafeRunSync()

      response.status shouldBe Status.Ok
    }
  }
}
