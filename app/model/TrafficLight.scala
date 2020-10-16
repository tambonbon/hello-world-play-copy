package model

import model.Color.Color

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

case class TrafficLight(id: Int, color: Color)
object TrafficLight {
  var trafficLightsMap: Map[Int, TrafficLight] = {
    Map(
      1 -> TrafficLight(1, Color.Green),
      2 -> TrafficLight(2, Color.Orange),
      3 -> TrafficLight(3, Color.Red)
    )
  }

  var ongoingRequests: Map[Int, Future[TrafficLight]] = Map.empty

  def trafficLightsList: List[TrafficLight] =
    trafficLightsMap.values.toList

  def changeToGreen(id: Int): Future[TrafficLight] = Future {
    // Step 1. Make the light Green
    val greenTrafficLight = TrafficLight(id, Color.Green)
    trafficLightsMap += id -> greenTrafficLight

    greenTrafficLight
  }

  def changeToRedFromGreen(id: Int): Future[TrafficLight] = {
    val request = Future {
      // Step 1. Make the light Orange
      val orangeTrafficLight = TrafficLight(id, Color.Orange)
      trafficLightsMap += id -> orangeTrafficLight

      // Step 2. Wait 15 seconds
      Thread.sleep(15 * 1000)

      // Step 3. Make the light Red
      val redTrafficLight = TrafficLight(id, Color.Red)
      trafficLightsMap += id -> redTrafficLight

      // Step 4. Finish the request.
      ongoingRequests -= id

      redTrafficLight
    }

    ongoingRequests += id -> request

    request
  }


  def changeToRedFromOrange(id: Int): Future[TrafficLight] = {
    val ongoingRequestOpt = ongoingRequests.get(id)
    ongoingRequestOpt match {
      case Some(ongoingRequest) =>
        ongoingRequest
      case None =>
        Future.successful(TrafficLight(id, Color.Red))
    }
  }
}
