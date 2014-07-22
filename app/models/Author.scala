package models

import scala.collection.mutable.ListBuffer

import controllers.Application._
import controllers.IcebergGraph
import models._

import com.thinkaurelius.titan.graphdb.blueprints.TitanBlueprintsGraph
import com.thinkaurelius.titan.core. { EdgeLabel, TitanGraph, TitanFactory, Multiplicity }
import com.thinkaurelius.titan.core.schema. { ConsistencyModifier, TitanGraphIndex, TitanManagement }
import com.tinkerpop.blueprints. {Vertex, Edge }
import com.tinkerpop.blueprints.util.ElementHelper
import com.thinkaurelius.titan.core.attribute.Geoshape
import com.tinkerpop.gremlin.scala._

import play.api.libs.json._
import play.api.libs.functional.syntax._


import com.tinkerpop.blueprints. {Vertex, Edge }
import com.tinkerpop.blueprints.util.ElementHelper

import com.github.nscala_time.time.Imports._


class Author(var name: String, var email: String, var likes: Option[List[Author]] = None) {
  val timestamp = DateTime.now
}

object Author {

  def apply() = new Author("none", "none", None)

  def apply(name: String) = new Author(name, "none", None)

  def apply(name: String, email: String): Author = {
    val a = new Author(name, email, None)
    a
  }

  def apply(name: String, email: String, likes: Option[List[Author]]): Author = {
    val a = new Author(name, email, None)
    a
  }

  def unapply(a: Author) : Option[(String, String, Option[List[Author]])] = {
    println("in unapply, name = " + a.name + " zip = " + a.email);
    return Some((a.name, a.email, a.likes))
  }



  val json: JsValue = Json.obj(
    "name" -> "Watership Down",
    "location" -> Json.obj("lat" -> 51.235685, "long" -> -1.309197),
    "residents" -> Json.arr(
      Json.obj(
        "name" -> "Fiver",
        "age" -> 4,
        "role" -> JsNull
      ),
      Json.obj(
        "name" -> "Bigwig",
        "age" -> 6,
        "role" -> "Owsla"
      )
    )
  )

  def create(name: String, email: String): Vertex = {
    val g = IcebergGraph.getTitanConnection

    val author = g.addVertex("author")
    author.setProperty("name", "name")
    author.setProperty("email", email)

    author
  }


  def get(id: Long): Author  = {
    val g = IcebergGraph.getTitanConnection
    val authorVertex = g.v(id)

    val author = Author(authorVertex.getProperty("name"), authorVertex.getProperty("email"))

    author
  }

  def get(vertex: Vertex): JsObject = {
    val authorJson = Json.obj(
      "name" -> JsString(vertex.getProperty("name")),
      "email" -> JsString(vertex.getProperty("email")),
      "values" -> JsString(vertex.getPropertyKeys.toString),
      "id" -> vertex.getProperty("id").toString
    )

    authorJson
  }

  def list2: List[Author] = {

    val g = IcebergGraph.getTitanConnection
    val authorsGraph = g.V.has("name").toList()
    val authors = for(graphItem <- authorsGraph) yield {
      println( "graphitem: " + graphItem.out("friends").tree )

      Author(graphItem.getProperty("name"), graphItem.getPropertyKeys.toString)
    }
    authors
  }


  def list: List[Author] = {

    var g = IcebergGraph.getTitanConnection
    val authorsGraph = g.V.has("name").toList()
    //val gremlinPipeline = new GremlinScalaPipeline[Vertex, Vertex]
    //val vertex = gremlinPipeline.start(g.getVertices("name")).next
    //val row = gremlinPipeline.next



    val jsonFriends = new ListBuffer[JsValue]()

    val authors = for(graphItem <- authorsGraph) yield {
      //println( "graphitem: " + graphItem.out("friends").tree )
      //println(graphItem.toString())

      jsonFriends += Json.obj(
        "name" -> JsString(graphItem.getProperty("name")),
        "values" -> JsString(graphItem.getPropertyKeys.toString)
      )

      Author(graphItem.getProperty("name"), graphItem.getPropertyKeys.toString)
    }
    authors
  }

  def listJson: ListBuffer[JsValue] = {

    var g = IcebergGraph.getTitanConnection
    val authorsGraph = g.V.has("name").toList()
    //val gremlinPipeline = new GremlinScalaPipeline[Vertex, Vertex]
    //val vertex = gremlinPipeline.start(g.getVertices("name")).next
    //val row = gremlinPipeline.next


    val jsonFriends = new ListBuffer[JsValue]()

    for(author <- authorsGraph) {
      var friendAsJson = get(author)

      val authorFriends = for(friend <- author.out("friends").toList) yield {
        get (friend)
      }

      if (!authorFriends.isEmpty) {
        friendAsJson += ("friends" -> Json.toJson(authorFriends))
      }

      jsonFriends += friendAsJson
    }
    jsonFriends
  }

  val fromCreated = __.json.update((__ \ 'created).json.copyFrom( (__ \ 'created \ '$date).json.pick ))

  /** prunes _id
    * and then prunes pw
    */
  val outputPerson =
    fromCreated andThen
    (__ \ 'name).json.prune andThen
      (__ \ 'email).json.prune

//  implicit object authorFormat extends Format[Author] {
//    def reads(json: JsValue) = Author(
//      (json \ "name").as[String],
//      (json \ "email").as[String]
//    )

  implicit val authorReads: Reads[Author] = (
    (__ \ 'name).read[String] and
      (__ \ 'email).read[String] and
      (__ \ 'likes).read[Option[List[Author]]]
    )(Author)

  implicit val authorWrites = new Writes[Author] {
    def writes(author: Author) = play.api.libs.json.Json.obj(
      "name" -> author.name,
      "email.Cool" -> author.email,
      "likes" -> author.likes
    )
  }


}