package controllers

import com.thinkaurelius.titan.core.Multiplicity
import com.thinkaurelius.titan.core.TitanFactory
import com.thinkaurelius.titan.core.TitanGraph
import com.thinkaurelius.titan.core.attribute.Geoshape
import com.tinkerpop.blueprints.util.ElementHelper
import controllers.StoryController._
import models._
import org.apache.commons.configuration.BaseConfiguration
import play.api._

import play.api.data._
import play.api.data.Forms._
import play.api.mvc._
import play.api.Play.current
import play.api.mvc.BodyParsers._
import play.api.libs.json.Json
import play.api.libs.json.Json._

object IcebergGraph {
  var g: TitanGraph = null

  def getTitanConf = {
    val conf = new BaseConfiguration();
    conf.setProperty("storage.backend","cassandra");
    conf.setProperty("storage.hostname","127.0.0.1");
    conf.setProperty("cache.db-cache","false");
//    conf.setProperty("cache.db-cache-clean-wait","20");
//    conf.setProperty("cache.db-cache-time","0");
//    conf.setProperty("cache.db-cache-size","0.25");

    conf.setProperty("storage.index.search.backend", "elasticsearch");
    conf.setProperty("storage.index.search.hostname", "127.0.0.1");
    conf.setProperty("storage.index.search.client-only", "true");

    conf
  }

  def getTitanConnection = {
    if (g == null || !g.isOpen()) {
      g = TitanFactory.open(getTitanConf);
    }
    g
  }
}

object Application extends Controller {

  object VertexTypes {
    val Person = "human"
    val Media = "media"
    val Story = "story"
  }


  def titanManagement = {
    var g = IcebergGraph.getTitanConnection
    var mgmt = g.getManagementSystem();


    //var nameType = g.getType("name");
    //if (nameType == null) {
    //namei = mgmt.buildIndex("name",Vertex.class).indexKey(name).unique().buildCompositeIndex();
    //g.makeKey("name").dataType(classOf[String]).indexed(classOf[Vertex]).make();




    // Create Edge Labelsdfcvvvvvvvvvvvvvvvvvvvvv
    if (mgmt.getEdgeLabel("place") == null) {
      mgmt.makeEdgeLabel("place").multiplicity(Multiplicity.MULTI)
    }


    if (mgmt.getEdgeLabel("married") == null) {
      mgmt.makeEdgeLabel("married").unidirected().multiplicity(Multiplicity.ONE2ONE)
    }

    // Create Vertext Labels
    if (!mgmt.containsVertexLabel("person")) {
      mgmt.makeVertexLabel("person").make()
    }
    if (!mgmt.containsVertexLabel("story")) {
      mgmt.makeVertexLabel("story").make()
    }
    if (!mgmt.containsVertexLabel("media")) {
      mgmt.makeVertexLabel("media").make()
    }

    mgmt.commit()


  }

  def graph = Action {
    val g = IcebergGraph.getTitanConnection

    val juno = g.addVertex("person")
    juno.setProperty("class", "person")
    juno.setProperty("quote", "I like milk")
    juno.setProperty("name", "David Vector")
    juno.setProperty("city", "bismarck")
    juno.setProperty("content", "why won't this work???")
    juno.setProperty("tags", Array("a", "b", "c", Array("ca", "cb", "cc")))
    ElementHelper.setProperties(juno, "surname", "peterson", "age", 37: java.lang.Integer, "birthdate", "17-10-1976", "likes Johnny Cash", true: java.lang.Boolean)
    val shape = Geoshape.point(-26.68584, 152.96135)

    juno.setProperty("location", shape)

    //juno.


    val story = g.addVertex("story")
    story.setProperty("title", "this is the tale of two windows")
    story.setProperty("content", "it was the best of times, it was the worst of times")
    story.addEdge("author", juno)

    //juno.setProperty("birthdate", com.github.nscala_time.time.Imports.DateTime.now)

    //val map = Map("Colour" -> "Red", "Phone" -> "iPhone")
    //juno.setProperty("preferences", map)

    val jupiter = g.addVertex("person")
    jupiter.setProperty("name", "jupiter")
    val friends = g.addEdge(null, juno, jupiter, "friends")
    //friends.setProperty("became_friends", com.github.nscala_time.time.Imports.DateTime.now.toString) // https://github.com/nscala-time/nscala-time
    val family = g.addEdge(null, juno, jupiter, "family")

    val turnus = g.addVertex(null)
    turnus.setProperty("name", "turnus")
    val hercules = g.addVertex(null)
    hercules.setProperty("name", "hercules")
    val dido = g.addVertex(null)
    dido.setProperty("name", "dido")
    val troy = g.addVertex(null)
    troy.setProperty("name", "troy")
    jupiter.setProperty("name", "jupiter")

    val friends2 = g.addEdge(null, juno, turnus, "friends")


    g.commit()

    val json = Json.toJson(Story.list)
    Ok(json)
  }

  def index = Action {
    Ok(views.html.index(Story.list))
  }


  
}
