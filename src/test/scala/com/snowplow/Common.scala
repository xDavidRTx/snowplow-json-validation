package com.snowplow

object Common {
  val schemaId = "someId"

  val schema: String = """
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

  val json: String = """{
      |  "source": "/home/alice/image.iso",
      |  "destination": "/mnt/storage",
      |  "timeout": null,
      |  "chunks": {
      |    "size": 999024,
      |    "number": null
      |  }
      |}""".stripMargin

}
