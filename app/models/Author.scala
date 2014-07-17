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

  var list2: List[Author] = {

    var g = IcebergGraph.getTitanConnection
    val authorsGraph = g.V.has("name").toList()
    val authors = for(graphItem <- authorsGraph) yield {
      println( "graphitem: " + graphItem.out("friends").tree )

      Author(graphItem.getProperty("name"), graphItem.getPropertyKeys.toString, None)
    }
    authors
  }


  var list: List[Author] = {

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

  var listJson: ListBuffer[JsValue] = {

    var g = IcebergGraph.getTitanConnection
    val authorsGraph = g.V.has("name").toList()
    //val gremlinPipeline = new GremlinScalaPipeline[Vertex, Vertex]
    //val vertex = gremlinPipeline.start(g.getVertices("name")).next
    //val row = gremlinPipeline.next


    val jsonFriends = new ListBuffer[JsValue]()

    val authors = for(graphItem <- authorsGraph) yield {
      //println( "graphitem: " + graphItem.out("friends").tree )
      //println(graphItem.toString())

      val authorFriends = for(friend <- graphItem.out("friends").toList) yield {
        Json.obj(
          "name" -> JsString(friend.getProperty("name")),
          "values" -> JsString(friend.getPropertyKeys.toString)
        )
      }

      var friendAsJson = Json.obj(
        "name" -> JsString(graphItem.getProperty("name")),
        "values" -> JsString(graphItem.getPropertyKeys.toString)
      )

      //val jsonObject = Json.toJson(friendAsJson)


      //if (!authorFriends.isEmpty) {
      //jsonObject.as[JsObject] + ("friendsXX" -> Json.toJson("friends"))
      friendAsJson += ("friends" -> Json.toJson(authorFriends))
      //}

      friendAsJson += ("friendsXX" -> Json.toJson("friends"))

      jsonFriends += friendAsJson


      //Author(graphItem.getProperty("name"), graphItem.getPropertyKeys.toString, None)
    }
    jsonFriends
  }

  var listWorks: List[Author] = {

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