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