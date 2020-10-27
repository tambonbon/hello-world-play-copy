package dao
import javax.inject.{Inject, Singleton}
import model.{Color, TrafficLight}
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

@Singleton()
class TrafficLightDAO @Inject() (protected val dbConfigProvider: DatabaseConfigProvider)(implicit executionContext: ExecutionContext)
  extends TrafficLightComponent with HasDatabaseConfigProvider[JdbcProfile]{
//  private val dbConfig = dbConfigProvider.get[JdbcProfile]
  /*
  * Creating a schema
  * */
//  val profile: slick.jdbc.H2Profile
//  import dbConfig._
  import profile.api._

  val trafficLights = TableQuery[TrafficLightsTable] // This is the actual query

  /*
  * Populating a database
  * */
  val setup = DBIO.seq(
    // Create the table
    trafficLights.schema.create,
    // Insert some lights
    trafficLights ++= Seq(
      TrafficLight(1, Color.Green ),
      TrafficLight(2,  Color.Green),
      TrafficLight(3,  Color.Red)
    )
  )
    val setupFuture = dbConfig.db.run(setup)

    /** Insert new lights*/
    def insert(light: TrafficLight): Future[Unit] =
      dbConfig.db.run(trafficLights += light).map(_=> ())
    /** Show all lights*/
    def all(): Future[Seq[TrafficLight]] = {
      dbConfig.db.run(trafficLights.result)
    }





}
