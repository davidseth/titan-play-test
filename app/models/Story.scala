package models

import scala.collection.mutable.ArrayBuffer
import play.api.libs.json._
import play.api.libs.json.Reads._
import play.api.libs.functional.syntax._

import argonaut._, Argonaut._

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

  //argonaut
  implicit def StoryEncodeJson: EncodeJson[Story] =
    EncodeJson((p: Story) =>
      ("title" := p.title) ->: ("text" := p.text) ->: ("tags" := Option[Set[p.tags]]) ->: ("author" := Some(p.author)) ->: jEmptyObject)

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

  implicit val storyReads = (
    (__ \ 'title).read[String] and
    (__ \ 'text).read[String] and
    (__ \ 'tags).read[Option[Set[Tag]]] and
    (__ \ 'text).read[Option[Author]]
  )(Story)


  implicit val storyWrites = new Writes[Story] {
    def writes(story: Story) = play.api.libs.json.Json.obj(
      "title" -> story.title,
      "text" -> story.text,
      "tags" -> story.tags,
      "author" -> story.author
    )
  }
}