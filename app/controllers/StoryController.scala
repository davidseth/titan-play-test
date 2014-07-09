package controllers

import models._
import play.api._
import play.api.data._
import play.api.data.Forms._
import play.api.mvc._
import play.api.Play.current
import play.api.mvc.BodyParsers._
import play.api.libs.json._

object StoryController extends Controller{

//  def listStories = Action {
//    val json = Json.toJson(Story.list)
//    Ok(json)
//  }

  def getStories = Action {
    val json = Json.toJson(Story.list)
    Ok(json)
  }

  def getStory = Action {

//    var json = com.tinkerpop.blueprints.util.io.graphson.GraphSONUtility.jsonFromElement(vertex, null, com.tinkerpop.blueprints.util.io.graphson.GraphSONMode.EXTENDED);
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
