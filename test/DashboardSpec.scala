
import data._
import test._
import auth._

import org.specs2.mutable._
import org.specs2.runner._

import play.api.Logger
import play.api.test._
import play.api.test.Helpers._

class DashboardSpec extends Specification {

  import models._

  "GET /home" should {

    "return Unauthorized without a valid user cookie" in new WithApp {

      val request = FakeRequest(GET, "/home")

      val response = route(request).get

      status(response) must equalTo(401)
    }

    "render the user home page with a valid user cookie" in new WithApp {

      val request = FakeRequest(GET, "/home")
        .withCookies(authorizedCookie(Email.random(),Random.string(10)))

      val response = route(request).get

      status(response) must equalTo(OK)
    }
  }
}
