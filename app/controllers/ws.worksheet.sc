case class TrafficLight (id: Int, color: String)
var list: Set[TrafficLight] = {
      Set (  
        TrafficLight(1, "Green"),
        TrafficLight(2, "Green"),
        TrafficLight(3, "Orange"),
        TrafficLight(4, "Red"),
        TrafficLight(2, "Green")
      )
    }

val map1 = list.zipWithIndex.map{case(v,i) => (v, i+1)}
// list.foldLeft(Map.empty[Int,String])()
val map2 = Set(
    TrafficLight(1,"Green"),
    TrafficLight(5, "Red")
)
val map3 = map2.zipWithIndex.map{case(v,i) => (v, i+1)}.toMap
val map4 = map1 ++ map2

import TrafficLight._
import scala.collection.mutable

    var mapp: mutable.Map[Int, String] = {
      mutable.Map (  
        1 -> "Green",
        2 -> "Green",
        3 -> "Orange",
        4 -> "Red"
        // TrafficLight(1, "Green"),
        // TrafficLight(2, "Green"),
        // TrafficLight(3, "Orange"),
        // TrafficLight(4, "Red")
      )
    }

    mapp.keySet.contains(5)

import play.api._
import play.api.mvc._
import play.api.libs.json._
import play.api.libs.functional.syntax._
import scala.collection.mutable
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}

val a = Future{ Thread.sleep(1*1000); 42}
val b = a.map(_*2)
b
b

b
