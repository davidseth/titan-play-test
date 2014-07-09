package models

import controllers.Application._
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


case class Author(name: String, email: String)

object Author {

  var list: List[Author] = {

    var g = getTitanConnection
    val authorsGraph = g.V.has("name").toList

    val authors = for(graphItem <- authorsGraph) yield {

      Author(graphItem.getProperty("name"), graphItem.getPropertyKeys.toString)
    }

    authors

  }
    
    implicit val authorReads: Reads[Author] = (
      (JsPath \ "name").read[String] and
      (JsPath \ "email").read[String]
    )(Author.apply _)
    
    implicit val authorWrites: Writes[Author] = (
      (JsPath \ "name").write[String] and
      (JsPath \ "email").write[String]
    )(unlift(Author.unapply))
}