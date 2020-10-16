package controllers

import javax.inject._
import json.TrafficLightJson._
import model.{ Color, TrafficLight }
import play.api.libs.concurrent.Futures
import play.api.libs.json._
import play.api.libs.ws._
import play.api.mvc._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration._
import scala.language.postfixOps

/**
  * This controller creates an `Action` to handle HTTP requests to the
  * application's home page.
  */
@Singleton
class HomeController @Inject() (val ws: WSClient, val controllerComponents: ControllerComponents, val futures: Futures)
  extends BaseController {

  /**
    * Create an Action to render an HTML page.
    *
    * The configuration in the `routes` file means that this method
    * will be called when the application receives a `GET` request with
    * a path of `/`.
    */
  def toTrafficLight(map: Map[Int, TrafficLight]): List[TrafficLight] = {
    map.toList.map(x => x._2)
  }

  def all = Action { request =>
    val json = Json.toJson(toTrafficLight(TrafficLight.trafficLightsMap))
    Ok(json)
  }

  def show(someId: Int) = Action { request =>
    TrafficLight.trafficLightsMap
      .find(_._1 == someId)
      .map { tl =>
        Ok(Json.toJson(tl._2))
      }
      .getOrElse(NotFound("No JSON found"))
  }

  def save(tl: Map[Int, TrafficLight]) = {
    TrafficLight.trafficLightsMap = TrafficLight.trafficLightsMap ++ tl
  } // to show lights in order

  def update = Action(parse.json) { request =>
    val result = request.body.validate[TrafficLight]
    result.fold(
      errors => {
        BadRequest(Json.obj("message" -> JsError.toJson(errors)))
      },
      newTrafficLight => {
        val allowedColors = Map(
          Color.Red -> Color.Green,
          Color.Orange -> Color.Red,
          Color.Green -> Color.Orange
        )
        val currentTrafficLightOpt = TrafficLight.trafficLightsMap.get(newTrafficLight.id)
        currentTrafficLightOpt match {
          case Some(currentTrafficLight) =>
            val expectedNewColor = allowedColors.get(currentTrafficLight.color)
            val newColor = newTrafficLight.color
            val currentColor = currentTrafficLight.color
            if ((expectedNewColor contains newColor) || (currentColor == newColor)) {
              save(Map(newTrafficLight.id -> newTrafficLight))
              Ok(Json.toJson(newTrafficLight))
            } else {
              BadRequest(Json.obj("message" -> "Request forbidden"))
            }
          case None =>
            save(Map(newTrafficLight.id -> newTrafficLight)) // create a new light
            Ok(Json.toJson(newTrafficLight))
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

  def updateAsync = Action.async(parse.json) { request =>
    val result = request.body.validate[TrafficLight]

    result.fold(
      errors => {
        Future.successful(BadRequest(Json.obj("message" -> JsError.toJson(errors))))
      },
      light => {
        if (TrafficLight.trafficLightsMap.keySet.contains(light.id)) {
          Future {
            if (light.color == Color.Red) {
              if (TrafficLight.trafficLightsMap(light.id).color == Color.Orange && TrafficLight.trafficLightsMap(light.id).color == Color.Red) {
                save(Map(light.id -> light))
                Ok(Json.toJson(toTrafficLight(TrafficLight.trafficLightsMap)))
              } else {
                val temp: TrafficLight = TrafficLight(light.id, Color.Orange)
                save(Map(light.id -> temp))
                futures.delayed(10000 millis) { // This is non-blocking
                  Ok(Json.toJson(toTrafficLight(TrafficLight.trafficLightsMap)))
                  Future.successful(save(Map(light.id -> light)))
                }
                Ok(Json.toJson(toTrafficLight(TrafficLight.trafficLightsMap)))

              }
            } else if (light.color == Color.Orange) {
              if (TrafficLight.trafficLightsMap(light.id).color == Color.Green) {
                save(Map(light.id -> light))
                Ok(Json.toJson(toTrafficLight(TrafficLight.trafficLightsMap)))
              } else BadRequest(Json.obj("message" -> "Request forbidden"))
            } else {
              if (TrafficLight.trafficLightsMap(light.id).color == Color.Red) {
                save(Map(light.id -> light))
                Ok(Json.toJson(toTrafficLight(TrafficLight.trafficLightsMap)))
              } else BadRequest(Json.obj("message" -> "Request forbidden"))

            }
          }
        } else {
          Future {
            save(Map(light.id -> light)) // create a new light
            Ok(Json.toJson(toTrafficLight(TrafficLight.trafficLightsMap)))
          }
        }
      }
    )
  }
  def showColor(someColor: String) = Action { request =>
    if (someColor == null) BadRequest("Wrong Color")
    TrafficLight.trafficLightsMap
      .find(_._2.color == someColor)
      .map { tl =>
        Ok(Json.toJson(tl._2))
      }
      .getOrElse(NotFound("No Color founded"))
  }
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
