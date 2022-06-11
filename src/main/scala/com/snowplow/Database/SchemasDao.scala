package com.snowplow.Database

import io.circe.Json
import io.circe.parser.parse
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


case class Schema(id: String, jsonSchema: String)

class SchemaTable(tag: Tag) extends Table[Schema](tag, None, "json_schema") {
  val id: Rep[String] = column[String]("id", O.PrimaryKey)
  val jsonSchema: Rep[String] = column[String]("jsonschema")

  override def * = (id, jsonSchema) <> (Schema.tupled, Schema.unapply)
}

class SchemasDao {
  val playerTable = TableQuery[SchemaTable]
  val db = Database.forConfig("postgres")

  def getSchemaById(id: String): Future[Option[Json]] = {
    db.run(playerTable.filter(_.id === id).map(_.jsonSchema).result.headOption).collect {
      case Some(value) => parse(value).toOption
      case None => None
    }
  }

  // The test instructions did not mention what to do if the id is already there so I decided to do an update in those cases.
  def insertSchema(schema: Schema): Future[Int] = db.run(playerTable.insertOrUpdate(schema))
}