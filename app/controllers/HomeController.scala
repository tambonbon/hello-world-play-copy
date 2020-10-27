package controllers

import dao.TrafficLightDAO
import javax.inject._
import json.TrafficLightJson._
import model.{Color, TrafficLight}
import play.api.libs.json._
import play.api.libs.ws._
import play.api.mvc._
import services.TrafficLightService

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.language.postfixOps

/**
  * This controller creates an `Action` to handle HTTP requests to the
  * application's home page.
  */
class HomeController @Inject() (
  trafficLightService: TrafficLightService,
  trafficLightDAO: TrafficLightDAO,
  ws: WSClient,
  controllerComponents: ControllerComponents) extends AbstractController(controllerComponents) {
//  import dbConfig.profile.api._
  /**
    * Create an Action to render an HTML page.
    *
    * The configuration in the `routes` file means that this method
    * will be called when the application receives a `GET` request with
    * a path of `/`.
    */
  def index(): Action[AnyContent] = Action {
    Ok(views.html.index())
  }

  def all = Action {
    val json = Json.toJson(trafficLightService.all)
    Ok(json)
  }
  def all1 = Action.async { request =>
    trafficLightDAO.all().map { tl => Ok(Json.toJson(tl))}
  }

  def get(someId: Int) = Action {
    trafficLightService.get(someId)
      .map(Json.toJson(_))
      .fold[Result](NotFound)(Ok(_))
  }

  def update = Action.async(parse.json) { request =>
    val result = request.body.validate[TrafficLight]
    result.fold(
      errors => {
        Future.successful(BadRequest(Json.obj("message" -> JsError.toJson(errors))))
      },
      newTrafficLight => {
        val currentTrafficLightOpt = trafficLightService.get(newTrafficLight.id)

        def updateLights(newTrafficLightColor: Color , transition: Int => Future[TrafficLight]): Future[Result] = {
          if (newTrafficLight.color == newTrafficLightColor) {
            val updatedTrafficLight = transition(newTrafficLight.id) //trafficLightService.getFuture(newTrafficLight.id) // will return a future of TL
            updatedTrafficLight.map(Json.toJson[TrafficLight]).map(Ok(_))
            //val test1 = test.map(Ok(_))
//            test1
            //  map(Ok(_)) // for returning from future to json
          } else {
            Future.successful(BadRequest(Json.obj("message" -> "Request forbidden")))
          }
        }

        val transitionsColorOnly: Map[Color , Color ] = Map(
          Color.Red -> Color.Green,
          Color.Green -> Color.Red,
          Color.Orange -> Color.Red
        )
        val transitions: Map[Color , Int => Future[TrafficLight]] = Map(
          Color.Red -> trafficLightService.changeToGreenFromRed,
          Color.Green -> trafficLightService.changeToRedFromGreen,
          Color.Orange -> trafficLightService.changeToRedFromOrange
        )
        currentTrafficLightOpt match {

          case Some(currentTrafficLight) => {
            val newTL: Color = transitionsColorOnly(currentTrafficLight.color)
            val transition: Int => Future[TrafficLight] = transitions(currentTrafficLight.color)
            val test = updateLights(newTL, transition)
            test
          }
          case None => {
            trafficLightService.save(newTrafficLight) // create a new light
            Future.successful(Ok(Json.toJson(newTrafficLight)))
          }
        }
      }
    )
  }

  /* To create a Future[Result] we need another future first:
    the future that will give us the actual value we need to compute the result: */
//  def toRed(from: TrafficLight): Future[TrafficLight] = from match {
//    case TrafficLight(_, "Red")    => Future.successful(from)
//    case TrafficLight(_, "Orange") => Future.successful(TrafficLight(from.id, Color.Red))
//    case TrafficLight(_, "Green")  => Future.successful(TrafficLight(from.id, "Red"))
//    case _                         => Future.failed(new Exception("Failed"))
//  }

//  def updateAsync = Action.async(parse.json) { request =>
//    val result = request.body.validate[TrafficLight]
//
//    result.fold(
//      errors => {
//        Future.successful(BadRequest(Json.obj("message" -> JsError.toJson(errors))))
//      },
//      light => {
//        if (TrafficLight.trafficLightsMap.keySet.contains(light.id)) {
//          Future {
//            if (light.color == Color.Red) {
//              if (TrafficLight.trafficLightsMap(light.id).color == Color.Orange && TrafficLight.trafficLightsMap(light.id).color == Color.Red) {
//                save(Map(light.id -> light))
//                Ok(Json.toJson(toTrafficLight(TrafficLight.trafficLightsMap)))
//              } else {
//                val temp: TrafficLight = TrafficLight(light.id, Color.Orange)
//                save(Map(light.id -> temp))
//                futures.delayed(10000 millis) { // This is non-blocking
//                  Ok(Json.toJson(toTrafficLight(TrafficLight.trafficLightsMap)))
//                  Future.successful(save(Map(light.id -> light)))
//                }
//                Ok(Json.toJson(toTrafficLight(TrafficLight.trafficLightsMap)))
//
//              }
//            } else if (light.color == Color.Orange) {
//              if (TrafficLight.trafficLightsMap(light.id).color == Color.Green) {
//                save(Map(light.id -> light))
//                Ok(Json.toJson(toTrafficLight(TrafficLight.trafficLightsMap)))
//              } else BadRequest(Json.obj("message" -> "Request forbidden"))
//            } else {
//              if (TrafficLight.trafficLightsMap(light.id).color == Color.Red) {
//                save(Map(light.id -> light))
//                Ok(Json.toJson(toTrafficLight(TrafficLight.trafficLightsMap)))
//              } else BadRequest(Json.obj("message" -> "Request forbidden"))
//
//            }
//          }
//        } else {
//          Future {
//            save(Map(light.id -> light)) // create a new light
//            Ok(Json.toJson(toTrafficLight(TrafficLight.trafficLightsMap)))
//          }
//        }
//      }
//    )
//  }
//  def showColor(someColor: String) = Action { request =>
//    if (someColor == null) BadRequest("Wrong Color")
//    TrafficLight.trafficLightsMap
//      .find(_._2.color == someColor)
//      .map { tl =>
//        Ok(Json.toJson(tl._2))
//      }
//      .getOrElse(NotFound("No Color founded"))
//  }
  // Playing with WS
  def availability = Action.async {
    val response: Future[WSResponse] = ws.url("http://localhost:9000/traffic-light1").get()
    val siteAvailable: Future[Boolean] = response.map { r =>
      r.status == 200
    }
    siteAvailable.map { isAvailable =>
      if (isAvailable) Ok("The Play site is up")
      else Ok("The Play site is down")
    }
  }

}
