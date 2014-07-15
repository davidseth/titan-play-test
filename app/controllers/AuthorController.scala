package controllers

import models._
import play.api._
import play.api.data._
import play.api.data.Forms._
import play.api.mvc._
import play.api.Play.current
import play.api.mvc.BodyParsers._
import play.api.libs.json._

object AuthorController extends Controller{

  //  def listStories = Action {
  //    val json = Json.toJson(Story.list)
  //    Ok(json)
  //  }

  def getAuthors = Action {
    val json = Json.toJson(Author.list)
    Ok(json)
  }

  def getAuthorsJson = Action {
    Ok(Json.toJson(Author.listJson))
  }

//  def insertSto = Action(BodyParsers.parse.json) { request =>
//    val storyResult = request.body.validate[Story]
//    storyResult.fold(
//      errors => {
//        BadRequest(Json.obj("status" ->"KO", "message" -> JsError.toFlatJson(errors)))
//      },
//      story => {
//        Story.save(story)
//        Ok(Json.obj("status" ->"OK", "message" -> ("Story '"+story.title+"' saved.") ))
//      }
//    )
//  }

}
