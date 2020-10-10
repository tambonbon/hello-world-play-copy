package controllers

import javax.inject._
import play.api._
import play.api.mvc._
import play.api.libs.json._
import play.api.libs.functional.syntax._
import TrafficLight._
import views.html.defaultpages.error
import scala.collection.mutable

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class HomeController @Inject()(val controllerComponents: ControllerComponents) extends BaseController {

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

  def all = Action { request => 
    val json = Json.toJson(TrafficLight.list)
    Ok(json)   
  }

  def show(someId: Int) = Action { request => 
    TrafficLight.list
    .find(_._1 == someId)
    .map { tl =>
      Ok(Json.obj("message" -> tl))
    }
    .getOrElse(NotFound("No JSON found"))
  }

  def update = Action(parse.json) { request => 
    val result = request.body.validate[TrafficLight]
    result.fold(
      errors => { 
        BadRequest(Json.obj("message" -> JsError.toJson(errors)))
      },
      light => {
        if (light.color == "Red") {
          if (list.last._2 == "Green") {
            list.update(light.id, light.color)
            Ok(Json.toJson(list))
          }
          else BadRequest(Json.obj(("message" -> "Request forbidden")))
        }
        else if (light.color == "Orange") {
          if (list.last._2 == "Red") {
            list.update(light.id, light.color)
            Ok(Json.toJson(list))
          }
          else BadRequest(Json.obj(("message" -> "Request forbidden")))
          
        }
        else {
          if (list.last._2 == "Orange") {
            list.update(light.id, light.color)
            Ok(Json.toJson(list))
          }
          else BadRequest(Json.obj(("message" -> "Request forbidden")))
        }
        // list.update(light.id, light.color)
        // val json = Json.toJson(list)
        // Ok(json)
      }
    )
  }

}
