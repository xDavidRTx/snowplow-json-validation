package com.snowplow.Domain

import io.circe.Encoder
import io.circe.generic.semiauto.deriveEncoder

object HttpResponses {
  implicit val jsonEncoder: Encoder[Result] = deriveEncoder[Result]

  case class Result(action: String, id: String, status: String, message: Option[String] = None)
}
