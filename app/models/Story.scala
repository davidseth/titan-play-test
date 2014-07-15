package models

import scala.collection.mutable.ArrayBuffer
import play.api.libs.json._
import play.api.libs.json.Reads._
import play.api.libs.functional.syntax._

case class Story(var title: String, var text: String, var tags: Option[Set[Tag]], var author: Option[Author])

object Story {

  var list: List[Story] = {
    List(
      Story("Hello My Darling", "Once upon a time...", Some(Set(Tag("fantasy"), Tag("fiction"), Tag("dumb"))),
        Some(Author("David Peterson", "david@davidseth.net"))
      ),
      Story("Another World", "The war raged...", None, Some(Author("Matisse", "matisse@wow.com")))
    )
  }

  def save(story: Story) = {
    list = list ::: List(story)
  }

//  def save2(story: Story): Try[List[String]] = {
//    Try(io.Source.fromFile(filename).getLines.toList)
//  }
//
//  val filename = "/etc/passwd"
//
//  save2(Story("test", "lots of text")) match {
//    case Success(lines) => lines.foreach(println)
//    case Failure(f) => println(f)
//  }

//  implicit val storyReads = Json.reads[Story]

  implicit val storyReads: Reads[Story] = (
    (JsPath \ "title").read[String](minLength[String](2)) and
    (JsPath \ "text").read[String](minLength[String](4)) and
    (JsPath \ "tags").readNullable[Set[Tag]] and
    (JsPath \ "author").readNullable[Author]
  )(Story.apply _)

  implicit val storyWrites: Writes[Story] = (
    (JsPath \ "title").write[String] and
    (JsPath \ "text").write[String] and
    (JsPath \ "tags").writeNullable[Set[Tag]] and
    (JsPath \ "author").writeNullable[Author]
  )(unlift(Story.unapply))
}