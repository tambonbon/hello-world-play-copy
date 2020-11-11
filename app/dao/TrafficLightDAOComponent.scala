package dao

import com.google.inject.ImplementedBy
import model.Color.Color
import model.{Color, TrafficLight}
import play.api.db.slick.HasDatabaseConfigProvider
import slick.jdbc.JdbcProfile

import scala.util.Try
//import slick.jdbc.JdbcProfile

import scala.concurrent.Future

@ImplementedBy(classOf[TrafficLightDAO])
trait TrafficLightDAOComponent { self : HasDatabaseConfigProvider[JdbcProfile] => // import Jdbc first
  import profile.api._

  implicit lazy val myColorMapper = MappedColumnType.base[Color, String](
    e => e.toString,
    s =>
      Try(Color.withName(s)).getOrElse(
        throw new IllegalArgumentException(s"enumeration $s doesn't exist $Color[${Color.values.mkString(",")}]")
      )
  )

  class TrafficLightsTable(tag: Tag) extends Table[TrafficLight](tag, "TrafficLights") {
    def id = column[Int]("id", O.PrimaryKey)

    def lights = column[Color]("color")

    def * = (id, lights) <> ((TrafficLight.apply _).tupled, TrafficLight.unapply)
  }

  def all(): Future[Seq[TrafficLight]]
  def get(id: Int): Future[Option[TrafficLight]]
  def save(tl: TrafficLight): Future[Unit]
  def changeToGreenFromRed(id: Int): Future[TrafficLight]
  def changeToRedFromGreen(id: Int): Future[TrafficLight]
  def changeToRedFromOrange(id: Int): Future[TrafficLight]


}
