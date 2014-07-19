package controllers


import scala.concurrent.duration._
import scala.concurrent.Future


import play.api._
import play.api.data._
import play.api.data.Forms._

import play.api.mvc._
import play.api.Play.current
import play.api.mvc.BodyParsers._
import play.api.libs.json._
import play.api.libs.json.Reads._
import play.api.libs.functional.syntax._
import play.api.libs.concurrent._

import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.Play.current

import models._

object AuthorController extends Controller{

  //  def listStories = Action {
  //    val json = Json.toJson(Story.list)
  //    Ok(json)
  //  }

  def getAuthor(id: Long) = Action {
    val author = Author.get(id)
    val json = Json.obj("person" -> Json.toJson(author))
//    json.transform(Author.outputPerson).map { jsonp =>
//      //Ok(Json.obj("person" -> jsonp))
//      Ok(jsonp)
//
//    }
    Ok(json)
  }

  def getAuthors = Action {
    val json = Json.toJson(Author.list)
    Ok(json)
  }

  def getAuthorsJson = Action {
    Ok(Json.toJson(Author.listJson))
  }

//  def getPerson(id: String) = Action{
//    // builds a query from ID
//    val q = id
//    //Async {
//      persons.find[JsValue](q).headOption.map{
//        case None => NotFound(Json.obj("res" -> "KO", "error" -> s"person with ID $id not found"))
//        case Some(p) =>
//          p.transform(outputPerson).map{ jsonp =>
//            Ok( resOK(Json.obj("person" -> jsonp)) )
//          }.recoverTotal{ e =>
//            BadRequest( resKO(JsError.toFlatJson(e)) )
//          }
//      }
//    //}
//  }


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
