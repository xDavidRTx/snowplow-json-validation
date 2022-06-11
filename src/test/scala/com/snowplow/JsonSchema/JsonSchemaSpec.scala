package com.snowplow.JsonSchema

import com.snowplow.Common.{json, schema}
import com.typesafe.scalalogging.LazyLogging
import io.circe.Json
import io.circe.parser.parse
import org.scalamock.scalatest.MockFactory
import org.scalatest.funspec.AnyFunSpecLike
import org.scalatest.matchers.must.Matchers
import org.scalatest.matchers.should.Matchers.convertToAnyShouldWrapper

class JsonSchemaSpec extends AnyFunSpecLike with Matchers with MockFactory with LazyLogging {

  describe("JsonSchema") {
    it("should validate json without errors") {
      JsonSchema.validate(schema, parse(json).getOrElse(Json.Null)) shouldBe None
    }

    it("should handle an invalid json") {
      JsonSchema.validate(
        schema.replace("source", "timeout"),
        parse(json)
          .getOrElse(Json.Null)
      ) shouldBe Some("object has missing required properties ([\"timeout\"])")
    }
  }
}
