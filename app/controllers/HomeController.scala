package controllers

import javax.inject._
import play.api._
import play.api.mvc._
import play.api.libs.json._
import play.api.libs.functional.syntax._
import TrafficLight._
import views.html.defaultpages.error
// sealed class TrafficLight
// case object Red  extends TrafficLight
// case object Orange extends TrafficLight
// case object Green   extends TrafficLight

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
    .find(_.id == someId)
    .map { tl => 
      Ok(Json.obj("message" -> tl))
    }
    .getOrElse(NotFound("No JSON found"))
    
  }
  def traffic(id: Option[Int]) = Action(parse.json) { request => 
    val idReads: Reads[TrafficLight] = (JsPath \ "id").read[TrafficLight]
    val json = Json.toJson(TrafficLight.list)
    val result: JsResult[TrafficLight] = json.validate[TrafficLight](idReads)
    result match {
      case JsError(errors) => BadRequest(Json.obj("message" -> JsError.toJson(errors)))
      case JsSuccess(value, path) => Ok(Json.obj("message" -> value.id))
    }
    // result.fold(
    //   errors => { BadRequest(Json.obj("message" -> JsError.toJson(errors)))},
    //   light => {
    //     Ok(Json.obj(light))
    //   }
    // )
    
  }
  // def update1 = Action(parse.json) { request => 
  //   val result = request.body.validate[TrafficLight]
  //   result match {
  //     case JsError(errors) => {
  //       val err = JsError.toJson(errors)
        
  //       TrafficLight.save(errors)
  //       Ok(Json.obj("message" -> ("Traffic '" +  + "' created.")))
  //     }
  //     case JsSuccess(value, path) => {
  //       TrafficLight.update(value.id, value.color)
  //       Ok(Json.obj("message" -> ("Traffic '" + value.id + "' updated.")))
  //     }
  //   }
  // }
  def update = Action(parse.json) { request => 
    val result = request.body.validate[TrafficLight]
    result.fold(
      errors => {
        BadRequest(Json.obj("message" -> JsError.toJson(errors)))
      },
      light => { 
        // if (TrafficLight.list.foreach{ i => 
        //   val id = i._1}.contains(light.id)) {
        //   // val updated = light.asInstanceOf[] ++ Json.obj("id" -> light.id)
        //   // Ok(Json.toJson(light.(tl => TrafficLight.update(tl, light.id))))
        //   val someId = light.id
        //   TrafficLight.update(light, someId)
        //   Ok(Json.obj("message" -> ("Traffic '" + light.id + "' updated.")))

        // }
        // else {
          TrafficLight.save(light)
          Ok(Json.obj("message" -> ("Traffic '" + light.id + "' created.")))
          val json = Json.toJson(list)
          Ok(json)
        }
        
      // }
    )
  }
  def traffic_light = Action { request => 
    request.body.asJson match {
      case Some(value) => {
        val json = request.body.asJson.get
        val traffic = json.as[TrafficLight]
        println(traffic)
        Ok
      }
      case None => BadRequest("No JSON found")
    }
  }

  
}
