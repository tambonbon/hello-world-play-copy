package controllers
import play.api._
import play.api.mvc._
import play.api.libs.json._
import play.api.libs.functional.syntax._
import scala.collection.mutable

case class TrafficLight (id: Int, color: String)
object TrafficLight {
    var list: mutable.Map[Int, String] = {
      mutable.Map (  
        1 -> "Green",
        2 -> "Green",
        3 -> "Red",
        4 -> "Orange"
      )
    }

    implicit object TrafficLightFormat extends Format[TrafficLight] {
        // De-serialize (JSON string -> TrafficLight object)
        def reads(json: JsValue): JsResult[TrafficLight] = {
            val id = (json \ "id").as[Int]
            val color = (json \ "color").as[String]
            JsSuccess(TrafficLight(id,color))
        }
        // Serialize (TrafficLight object -> JSON string)
        def writes(o: TrafficLight): JsValue = {
            val traffic = Seq(
                "id" -> JsNumber(o.id),
                "color" -> JsString(o.color)
            )
            JsObject(traffic)
        }
    }

}
