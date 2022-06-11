package com.snowplow.Database

import com.typesafe.scalalogging.LazyLogging
import io.circe.Json
import io.circe.parser.parse
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

case class Schema(id: String, jsonSchema: String)

class SchemaTable(tag: Tag) extends Table[Schema](tag, None, "json_schema") {
  val id: Rep[String]     = column[String]("id", O.PrimaryKey)
  val schema: Rep[String] = column[String]("schema")

  override def * = (id, schema) <> (Schema.tupled, Schema.unapply)
}

class SchemasDao extends LazyLogging {
  val playerTable = TableQuery[SchemaTable]
  val db          = Database.forConfig("postgres")

  def getSchemaById(id: String): Future[Option[Json]] =
    db.run(playerTable.filter(_.id === id).map(_.schema).result.headOption).collect {
      case Some(value) => parse(value).toOption
      case None =>
        logger.warn(s"Failed to find Schema with id $id")
        None
    }

  // The test instructions did not mention what to do if the id is already there so I decided to do an update in those cases.
  def insertSchema(schema: Schema): Future[Int] = db.run(playerTable.insertOrUpdate(schema))
}
