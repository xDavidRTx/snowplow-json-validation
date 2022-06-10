package com.snowplow.JsonSchema

import com.github.fge.jsonschema.main.JsonSchemaFactory

object JsonSchema {
  val factory: JsonSchemaFactory = JsonSchemaFactory.byDefault
}
