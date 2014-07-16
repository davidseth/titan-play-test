package controllers

import com.thinkaurelius.titan.core.Multiplicity
import com.thinkaurelius.titan.core.TitanFactory
import com.thinkaurelius.titan.core.TitanGraph
import com.thinkaurelius.titan.core.attribute.Geoshape
import com.tinkerpop.blueprints.util.ElementHelper
import controllers. { Application, StoryController }
import models._
import org.apache.commons.configuration.BaseConfiguration
import play.api._

import play.api.data._
import play.api.data.Forms._
import play.api.mvc._
import play.api.Play.current
import play.api.mvc.BodyParsers._
import play.api.libs.json._
import play.api.libs.json.Json._

object StoryController extends Controller{

//  def listStories = Action {
//    val json = Json.toJson(Story.list)
//    Ok(json)
//  }

  def getStories = Action {
    val json = Json.toJson(Story.list)
    Ok(json)
  }

  def getStory(id: Long) = Action {

    val g: TitanGraph = IcebergGraph.getTitanConnection
    val vertex = g.getVertex(id);

    val story = Story(vertex.getProperty("title"), vertex.getProperty("body"), None, None)
    Ok(Json.toJson(story))
    //vertex.addPropert("name", "david")
    //var json = com.tinkerpop.blueprints.util.io.graphson.GraphSONUtility.jsonFromElement(vertex, null, com.tinkerpop.blueprints.util.io.graphson.GraphSONMode.EXTENDED);

    //json
//
//    val result = com.tinkerpop.blueprints.util.io.graphson.GraphSONUtility.jsonFromElement(vertex, null, com.tinkerpop.blueprints.util.io.graphson.GraphSONMode.NORMAL);

  }

  def insertStory = Action(BodyParsers.parse.json) { request =>
    val storyResult = request.body.validate[Story]
    storyResult.fold(
      errors => {
        BadRequest(Json.obj("status" ->"KO", "message" -> JsError.toFlatJson(errors)))
      },
      story => {
        Story.save(story)
        Ok(Json.obj("status" ->"OK", "message" -> ("Story '"+story.title+"' saved.") ))
      }
    )
  }

}
