package controllers

import javax.inject._
import play.api._
import play.api.mvc._
import play.api.libs.json._
import play.api.libs.functional.syntax._
import TrafficLight._
import views.html.defaultpages.error
import scala.collection.mutable
import scala.concurrent.Future
import scala.concurrent.java8.FuturesConvertersImpl.P
// import play.api.libs.ws._
import scala.concurrent.duration._
/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class HomeController @Inject()( val controllerComponents: ControllerComponents) extends BaseController {

  /**
   * Create an Action to render an HTML page.
   *
   * The configuration in the `routes` file means that this method
   * will be called when the application receives a `GET` request with
   * a path of `/`.
   */

  def index() = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.index())
  }
  def toTrafficLight(map: mutable.Map[Int, TrafficLight]): List[TrafficLight] = {
    map.toList.map(x => x._2 )
  }
  def all = Action { request => 
    val json = Json.toJson(toTrafficLight(list))
    Ok(json)   
  }

  def show(someId: Int) = Action { request => 
    TrafficLight.list
    .find(_._1 == someId)
    .map { tl =>
      Ok(Json.toJson( tl._2))
    }
    .getOrElse(NotFound("No JSON found"))
  }

  def save(tl: mutable.Map[Int, TrafficLight]) = { list = list ++ tl } // to show lights in order
  
  def update = Action(parse.json) { request => 
    val result = request.body.validate[TrafficLight]
    result.fold(
      errors => { 
        BadRequest(Json.obj("message" -> JsError.toJson(errors)))
      },
      light => {
        if (list.keySet.contains(light.id)) { // to check if the id was created
          if (light.color == "Red") {
            if (list(light.id).color == "Orange") {
              save(mutable.Map(light.id -> light))
              Ok(Json.toJson(toTrafficLight(list)))
            }
            else BadRequest(Json.obj("message" -> "Request forbidden"))
          }
          else if (light.color == "Orange") {
            if (list(light.id).color == "Green") { 
              save(mutable.Map(light.id -> light))
              Ok(Json.toJson(toTrafficLight(list)))
            }
            else BadRequest(Json.obj("message" -> "Request forbidden"))
          }
          else {
            if (list(light.id).color == "Red") {
              save(mutable.Map(light.id -> light))
              Ok(Json.toJson(toTrafficLight(list)))
            }
            else BadRequest(Json.obj("message" -> "Request forbidden"))
              
          }
        }
        else save(mutable.Map(light.id -> light)) 
        Ok(Json.toJson(toTrafficLight(list)))
        
      }
      
    )
  }
  // def saveAsync = Action.async(parse.json) { request => 
  //   val response: Future[WSRequest] = 
  //     ws.url("http://localhost:9000")
  //       .addHttpHeaders("Accept" -> "application/json")
  //       .addQueryStringParameters("search" -> "play")
  //       .withRequestTimeout(10000.millis)
  //       .get()

  //   val json: Future[JsValue] = response.map(_.json)

  //   val result: Future[]
      
  // }
  // def toGreen(fromLight: String): Future[TrafficLight] = {
  //   val request: WSRequest = 
  //     request
  //     .addHttpHeaders("Accept" -> "application/json")
  //     .addQueryStringParameters("search" -> "play")
  //     .withRequestTimeout(10000.millis)
        
  //   val json = Future[TrafficLight]
  // }

/*   def saveAsync = Action.async(parse.json) { request => 
    val result = request.body.validate[TrafficLight]// request.body.validate[TrafficLight]
    result.fold(
      errors => {
        BadRequest(Json.obj("message" -> JsError.toJson(errors)))
      },
      light => {
        if (light.color == "Red") {
          if (list.last._2 == "Orange") {
            list.save(light.id, light.color)
            Ok(Json.toJson(list))
          }
          else if (list.last._2 == "Green") { 
            list.save(light.id, "Orange")
            Future{ Thread.sleep(5*1000)}
            list.save((light.id, light.color))
            Ok(Json.toJson(list))
          }
          else BadRequest(Json.obj("message" -> "Request forbidden"))
        }
        
      }
    )
  } */

}
