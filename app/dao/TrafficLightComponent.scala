package dao

import enumeratum.SlickEnumSupport
import play.api.db.slick.HasDatabaseConfigProvider
import slick.jdbc.JdbcProfile

/** A component that creates Table having Enum as columns
  * In our case, lights is a column of type Color
  * https://github.com/lloydmeta/enumeratum/#slick-integration
  * */
trait TrafficLightComponent extends SlickEnumSupport { self: HasDatabaseConfigProvider[JdbcProfile] =>
//  val profile: slick.jdbc.JdbcProfile
//  import profile.api._
//  implicit lazy val traf1ficLightMapper = mappedColumnTypeForEnum(Color) // For implicitly write Enum
//  class TrafficLightsTable(tag: Tag) extends Table[TrafficLight](tag, "TrafficLights") {
//    def id = column[Int]("id", O.PrimaryKey)
//    def lights = column[Color]("color")
//    def * = (id, lights) <> ((TrafficLight.apply _).tupled, TrafficLight.unapply)
//  }
}