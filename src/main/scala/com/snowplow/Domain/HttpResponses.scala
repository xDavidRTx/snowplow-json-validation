package com.snowplow.Domain

import io.circe.Encoder
import io.circe.generic.semiauto.deriveEncoder

trait Response {
  val action: String
  val id: String
  val status: String
  val message: Option[String]
}

case class SuccessResponse(action: String, id: String, status: String = "success", message: Option[String] = None)
    extends Response

case class ErrorResponse(action: String, id: String, status: String, message: Option[String]) extends Response

object ErrorResponse {
  implicit val errorResponseEncoder: Encoder[ErrorResponse] = deriveEncoder[ErrorResponse]
  def apply(action: String, id: String, message: String): ErrorResponse =  ErrorResponse(action, id, "error", Some(message))
}

object SuccessResponse {
  implicit val successResponseEncoder: Encoder[SuccessResponse] = deriveEncoder[SuccessResponse]
}
