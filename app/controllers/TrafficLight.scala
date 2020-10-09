package controllers
import play.api._
import play.api.mvc._
import play.api.libs.json._
import play.api.libs.functional.syntax._

case class TrafficLight (id: Int, color: String)

object TrafficLight {
    var list: List[TrafficLight] = {
      List (  
        TrafficLight(1, "Green"),
        TrafficLight(2, "Green"),
        TrafficLight(3, "Orange"),
        TrafficLight(4, "Red")
      )
    }

    // c.getClass.getDeclaredFields.map(_.getName)
    // list.getClass.getDeclaredField.map(_.getName)
    
    def save(tl: TrafficLight) = { list = list ++ Set(tl) }
//     val transformer = (__ \ "id").json.update(
//          __.read[TrafficLight].map(_ => Json.toJson("updated value"))
// )
    def update(tl: TrafficLight, someId: Int): TrafficLight = { tl.copy(id = someId)}

    // def update(id: Int, color: String): JsObject = { list.[JsObject] ++  ("id" -> id) }
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
    // val p = TrafficLight.find(_.id == 1)


/*   implicit val trafficWrites: Writes[TrafficLight]  = new Writes[TrafficLight] {
    def writes(o: TrafficLight): JsValue = Json.obj(
      "id" -> o.id,
      "color" -> o.color
    )
  } */
}
