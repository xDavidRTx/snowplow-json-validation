package com.snowplow.JsonSchema

import cats.implicits.catsSyntaxOptionId
import com.github.fge.jackson.JsonLoader
import com.github.fge.jsonschema.main.JsonSchemaFactory
import io.circe.Json

object JsonSchema {
  type ValidationError = String
  val factory: JsonSchemaFactory = JsonSchemaFactory.byDefault

  def validate(schema: String, json: Json): Option[ValidationError] = {
    val jsonToValidate = JsonLoader.fromString(json.deepDropNullValues.toString())
    factory.getJsonSchema(JsonLoader.fromString(schema)).validate(jsonToValidate) match {
      case report if report.isSuccess          => None
      case report if report.iterator().hasNext => report.iterator().next().getMessage.some
      case _                                   => "Unknown error".some
    }
  }
}
