package com.snowplow.JsonSchema

import com.github.fge.jsonschema.main.JsonSchemaFactory
import io.circe.Json
import io.circe.parser._

object JsonSchema {
  val factory: JsonSchemaFactory = JsonSchemaFactory.byDefault

  def parseJson(jsonString: String) :  Option[Json] =
    parse(jsonString) match {
      case Left(ex) =>
        println(ex.message) //TODO Replace with logger
        None
      case Right(json) => Some(json)
    }
}
