package controllers

import dao.{TrafficLightDAO, TrafficLightDAOComponent}
import org.mockito.MockitoSugar
import org.scalatestplus.play.guice._
import play.api.mvc.ControllerComponents
import play.api.test.Helpers._
import play.api.test._
import play.api.libs.ws.WSClient
import services.TrafficLightService
//import org.scalatest.mockito.MockitoSugar
import org.scalatestplus.play._
/**
 * Add your spec here.
 * You can mock out a whole application including requests, plugins etc.
 *
 * For more information, see https://www.playframework.com/documentation/latest/ScalaTestingWithScalaTest
 */
class HomeControllerSpec extends PlaySpec with GuiceOneAppPerTest with Injecting with MockitoSugar{

  "HomeController GET" should {

    "render the index page from a new instance of controller" in {
      val mockTLService = mock[TrafficLightService]
      val mockTLDAO = mock[TrafficLightDAOComponent]
      val mockWS = mock[WSClient]
      val mockCC = mock[ControllerComponents]
      val trafficLightDAO = inject[TrafficLightDAO]
      // i try to use mock or inject here, but no success
      val controller = new HomeController(mockTLService, mockTLDAO, mockWS, stubControllerComponents())
      val home = controller.index().apply(FakeRequest(GET, "/"))

      status(home) mustBe OK
      contentType(home) mustBe Some("text/html")
      contentAsString(home) must include ("Welcome to Play")
    }

    "render the index page from the application" in {
      val controller = inject[HomeController]
      val home = controller.index().apply(FakeRequest(GET, "/"))

      status(home) mustBe OK
      contentType(home) mustBe Some("text/html")
      contentAsString(home) must include ("Welcome to Play")
    }

    "render the index page from the router" in {
      val request = FakeRequest(GET, "/")
      val home = route(app, request).get

      status(home) mustBe OK
      contentType(home) mustBe Some("text/html")
      contentAsString(home) must include ("Welcome to Play")
    }
  }

//  "HomeController PUT" should {
//
//  }
}
