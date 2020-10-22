package services
import model.TrafficLight
import play.api.libs.json.JsValue
import com.google.inject.ImplementedBy
import play.api.mvc.Result

import scala.concurrent.Future

@ImplementedBy(classOf[InMemoryTrafficLightService])
trait TrafficLightService {
  def all: Seq[TrafficLight]
  def get(id: Int): Option[TrafficLight]
  def getFuture(id: Int): Future[Option[TrafficLight]]
  def save(tl: TrafficLight): Unit
  def changeToGreenFromRed(id: Int): Future[TrafficLight]
  def changeToRedFromGreen(id: Int): Future[TrafficLight]
  def changeToRedFromOrange(id: Int): Future[TrafficLight]

}
