package dao
import com.typesafe.config.Config
import javax.inject.{Inject, Singleton}
import model.{Color, TrafficLight}
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

@Singleton()
class TrafficLightDAO @Inject() (config: Config,
                                 protected val dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext)
  extends HasDatabaseConfigProvider[JdbcProfile] with TrafficLightDAOComponent {
//  val dbConfig = dbConfigProvider.get[JdbcProfile]

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
      TrafficLight(1, Color.Green),
      TrafficLight(2, Color.Green),
      TrafficLight(3, Color.Red)
    )
  )
  val setupFuture = dbConfig.db.run(setup)

  /** Insert new lights */
  def save(light: TrafficLight): Future[Unit] = { //update

    dbConfig.db.run(trafficLights += light).map(_ => ())
  }
//  def changeToGreenFromRed(id: Int): Future[TrafficLight] = Future {
//    // Step 1. Make the light Green
//    val greenTrafficLight = TrafficLight(id, Color.Green)
//
//    val q = for { c <- trafficLights if c.id === id } yield c.lights
//    val updateAction = q.update(Color.Red)
//    // Get the statement without having to specify an updated value:
//    //    val sql = q.updateStatement
//    dbConfig.db.run(updateAction) // this is a side-effect
//  // use that returning value to return instead of greenTL
//    greenTrafficLight
//  }
  /** Show all lights*/
  def all(): Future[Seq[TrafficLight]] = {
    dbConfig.db.run(trafficLights.result)
  }
  def get(id: Int): Future[Option[TrafficLight]] = {
    dbConfig.db.run(trafficLights.filter(tl => tl.id === id).result.headOption)
  }
  def delete(id: Int): Future[Int] = {
    dbConfig.db.run(trafficLights.filter(tl => tl.id === id).delete)
  }
  private var ongoingRequests: Map[Int, Future[TrafficLight]] = Map.empty

  def changeToGreenFromRed(id: Int): Future[TrafficLight] = Future {
    // Step 1. Make the light Green
    val greenTrafficLight = TrafficLight(id, Color.Green)
    dbConfig.db.run(trafficLights +=  greenTrafficLight)

    greenTrafficLight
  }

  System.setProperty("traffic-light.duration", "This value comes from a system property")

  def changeToRedFromGreen(id: Int): Future[TrafficLight] = {
    val request = Future {
      // Step 1. Make the light Orange
      val orangeTrafficLight = TrafficLight(id, Color.Orange)
      dbConfig.db.run(trafficLights +=  orangeTrafficLight)

      // Step 2. Wait 15 seconds
      Thread.sleep(config.getInt("traffic-light.duration"))

      // Step 3. Make the light Red
      val redTrafficLight = TrafficLight(id, Color.Red)
      dbConfig.db.run(trafficLights +=  redTrafficLight)

      // Step 4. Finish the request.
      ongoingRequests -= id

      redTrafficLight
    }

    ongoingRequests += id -> request

    request
  }

  def changeToRedFromOrange(id: Int): Future[TrafficLight] = {
    val ongoingRequestOpt = ongoingRequests.get(id)
    ongoingRequestOpt match {
      case Some(ongoingRequest) =>
        ongoingRequest
      case None =>
        Future.successful(TrafficLight(id, Color.Red))
    }
  }



}
