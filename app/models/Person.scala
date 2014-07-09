package models

import play.api.libs.functional.syntax._
import play.api.libs.json.Reads._
import play.api.libs.json.{Writes, JsPath, Reads}
//import com.tinkerpop.blueprints. {Vertex, Edge }
//import com.tinkerpop.gremlin.scala._

/**
 * Created by david on 7/07/2014.
 */
case class Person (name: String)


//object Vertex {
//  implicit val vertexReads: Reads[Vertex] = (
//    (JsPath \ "name").read[String](minLength[String](2))
//    )(Vertex.apply _)
//
//  implicit val vertexWrites: Writes[Vertex] = (
//    (JsPath \ "name").write[String]
//    )(unlift(Vertex.unapply))
//}
