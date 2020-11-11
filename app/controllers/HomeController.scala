package controllers

import dao.TrafficLightDAOComponent
import javax.inject._
import json.TrafficLightJson._
import model.Color.Color
import model.{Color, TrafficLight}
import play.api.libs.json._
import play.api.libs.ws.{WSClient, WSResponse}
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
                                 trafficLightDAO: TrafficLightDAOComponent,
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
//  val trafficLightService: TrafficLightService
//  val trafficLightDAO: TrafficLightDAO
//  val ws: WSClient
  def index(): Action[AnyContent] = Action {
    Ok(views.html.index())
  }

  /** From now on there are 2 versions per request,
    * the first one is for in-memory service
    * the second one is for slick
    * */
  def all = Action {
    val json = Json.toJson(trafficLightService.all)
    Ok(json)
  }
  def all_DB = Action.async { request =>
    trafficLightDAO.all().map { tl => Ok(Json.toJson(tl))}
  }

  def get(someId: Int) = Action {
    trafficLightService.get(someId)
      .map(Json.toJson(_))
      .fold[Result](NotFound)(Ok(_))
  }

  def get_DB(someId: Int) = Action.async { request =>
    trafficLightDAO.get(someId).map{tl => Ok(Json.toJson(tl))}
  }

  def update = Action.async(parse.json)  { request =>
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

  def update_DB = Action.async(parse.json) { implicit request =>
    val result = request.body.validate[TrafficLight]
    result.fold(
      errors => {
        Future.successful(BadRequest(Json.obj("message" -> JsError.toJson(errors))))
      },
      newTrafficLight => {
        val currentTrafficLightOpt = trafficLightDAO.get(newTrafficLight.id)

        def updateLights(newTrafficLightColor: Color , transition: Int => Future[TrafficLight]): Future[Result] = {
          if (newTrafficLight.color == newTrafficLightColor) {
            val updatedTrafficLight = transition(newTrafficLight.id) // will return a future of TL
            trafficLightDAO.save(newTrafficLight)
            updatedTrafficLight.map(Json.toJson[TrafficLight]).map(Ok(_))

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
          Color.Red -> trafficLightDAO.changeToGreenFromRed,
          Color.Green -> trafficLightDAO.changeToRedFromGreen,
          Color.Orange -> trafficLightDAO.changeToRedFromOrange
        )
        currentTrafficLightOpt flatMap  {

          case Some(currentTrafficLight) => {
            val newTL: Color = transitionsColorOnly(currentTrafficLight.color)
            val transition: Int => Future[TrafficLight] = transitions(currentTrafficLight.color)
            val test = updateLights(newTL, transition)
            test
          }
          case None => {
            trafficLightDAO.save(newTrafficLight) // create a new light
            Future.successful(Ok(Json.toJson(newTrafficLight)))
          }
        }
      }
    )
  }
  // Playing with WS
  def availability = Action.async {
    val response: Future[WSResponse] = ws.url("http://localhost:9000/traffic-light").get()
    val siteAvailable: Future[Boolean] = response.map { r =>
      r.status == 200
    }
    siteAvailable.map { isAvailable =>
      if (isAvailable) Ok("The Play site is up")
      else Ok("The Play site is down")
    }
  }

}
