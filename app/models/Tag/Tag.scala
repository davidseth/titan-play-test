package models

import play.api.libs.json._
import play.api.libs.json.Reads._
import play.api.libs.functional.syntax._

/**
 * Created by david on 7/07/2014.
 */
case class Tag (name: String)

object Tag {

  implicit val tagReads = Json.reads[Tag]

//  implicit val tagReads: Reads[Tag] = (
//    (JsPath \ "name").read[String]
//    )(Tag.apply _)

  implicit val tagWrites = Json.writes[Tag]

//  implicit val tagWrites: Writes[Tag] = (
//    (JsPath \ "name").write[String]
//    )(unlift(Tag.unapply))
}


