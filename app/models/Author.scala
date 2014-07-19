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


case class Author(var name: String, var email: String, var likes: Option[List[Author]] = None)

object Author {

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

      Author(graphItem.getProperty("name"), graphItem.getPropertyKeys.toString, None)
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

      Author(graphItem.getProperty("name"), graphItem.getPropertyKeys.toString, None)
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

    for(graphItem <- authorsGraph) {
      var friendAsJson = get(graphItem)

      val authorFriends = for(friend <- graphItem.out("friends").toList) yield {
        get (friend)
      }

      if (!authorFriends.isEmpty) {
        friendAsJson += ("friends" -> Json.toJson(authorFriends))
      }

      jsonFriends += friendAsJson
    }
    jsonFriends
  }

  def listWorks: List[Author] = {

    var g = IcebergGraph.getTitanConnection
    val authorsGraph = g.V.has("name").toList()
    val authorsGraph2 = g.getVertices()

    //debug(authorGraph2)

    //println(GremlinScalaPipeline(g.getVertices("name")))

    //println(GremlinScalaPipeline(graph).V)
    //print(pipeline: GremlinScalaPipeline[_, _]) = println(pipeline.toList)
    //val pipe = new GremlinScalaPipeline[]


    val authors = for(graphItem <- authorsGraph) yield {
      println( "graphitem: " + graphItem.out("friends").tree )
      //      val friends: List[Author] = for(friend <- graphItem.out("friends").has("name").toList) yield {
      //        println("friend: " + friend.getProperty("name"))
      //        Author(friend.getProperty("name"), friend.getPropertyKeys.toString )
      //      }

      //      for(friend <- graphItem.out("friends").has("name").toSet) {
      //        println("friend: " + friend.getProperty("name"))
      //        //Author(friend.getProperty("name"), friend.getPropertyKeys.toString )
      //      }

      //      if (friends != null) {
      //        Author(graphItem.getProperty("name"), graphItem.getPropertyKeys.toString, Some(friends))
      Author(graphItem.getProperty("name"), graphItem.getPropertyKeys.toString, None)
      //        //Author("david", "stuff")
      //      }


    }

    //    val authors = for(graphItem <- authorsGraph) yield {
    //
    //
    //      Author(graphItem.getProperty("name"), graphItem.getPropertyKeys.toString, None )
    //
    //
    //    }


    authors
  }

  val fromCreated = __.json.update((__ \ 'created).json.copyFrom( (__ \ 'created \ '$date).json.pick ))

  /** prunes _id
    * and then prunes pw
    */
  val outputPerson =
    fromCreated andThen
    (__ \ 'name).json.prune andThen
      (__ \ 'email).json.prune



    
  implicit val authorReads: Reads[Author] = (
    (JsPath \ "name").read[String] and
    (JsPath \ "email").read[String] and
    (JsPath \ "likes").readNullable[List[Author]]
  )(Author.apply _)

  implicit val authorWrites: Writes[Author] = (
    (JsPath \ "name").write[String] and
    (JsPath \ "email").write[String] and
      (JsPath \ "likes").writeNullable[List[Author]]
  )(unlift(Author.unapply))
}