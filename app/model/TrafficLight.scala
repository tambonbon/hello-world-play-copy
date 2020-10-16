package model

import model.Color.Color

import scala.concurrent.Future

case class TrafficLight(id: Int, color: Color)
object TrafficLight {
  var trafficLightsMap: Map[Int, TrafficLight] = {
    Map(
      1 -> TrafficLight(1, Color.Green),
      2 -> TrafficLight(2, Color.Orange),
      3 -> TrafficLight(3, Color.Red)
    )
  }

  def trafficLightsList: List[TrafficLight] =
    trafficLightsMap.values.toList

  def changeToGreen(id: Int): Unit = {
    // Step 1. Make the light Green
    trafficLightsMap += id -> TrafficLight(id, Color.Green)
  }

  def changeToRed(id: Int): Unit = {
    // Step 1. Make the light Orange
    trafficLightsMap += id -> TrafficLight(id, Color.Orange)

    // Step 2. Wait 15 seconds
    Thread.sleep(15 * 1000)

    // Step 3. Make the light Red
    trafficLightsMap += id -> TrafficLight(id, Color.Red)
  }
}
