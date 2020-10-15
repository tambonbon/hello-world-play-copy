package controllers
import play.api._
import play.api.mvc._
import play.api.libs.json._
import play.api.libs.functional.syntax._
import scala.collection.mutable
import scala.concurrent.Future
import scala.concurrent.Await
import scala.concurrent.duration._
import scala.util.Success
import scala.util.Failure
case class TrafficLight (id: Int, color: String)
object TrafficLight {
    implicit val ec: scala.concurrent.ExecutionContext = scala.concurrent.ExecutionContext.global

    var list: Map[Int, TrafficLight] = {
      Map (  
        1 -> TrafficLight(1, "Green"),
        // 2 -> "Green",
        2 -> TrafficLight(2, "Orange"),
        3 -> TrafficLight(3, "Red")
      )
    }
    
// I prefer the following syntax of de-serialize and serialize
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

    implicit val ops = scala.language.postfixOps
    // implicit object TrafficLightFutureFormat extends Format[Future[TrafficLight]] {
    //     def reads(json: JsValue): JsResult[Future[TrafficLight]] = {
    //         val id = (json \ "id").as[Int]
    //         val color = (json \ "color").as[String]
    //         JsSuccess(Future(TrafficLight(id,color))) 
    //     }


    //     def writes(o: Future[TrafficLight]): JsValue = {
    //         val traffic = Seq(
    //             "id" ->   o.map(value => JsNumber(value.id)),
    //             "color" -> o.map(value=> JsString(value.color))
                
    //         )
            
    //         JsObject(traffic)
    //     }
    // }
    

}
