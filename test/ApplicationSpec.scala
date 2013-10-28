
import data._
import test._
import auth._

import org.specs2.mutable._
import org.specs2.runner._

import play.api.Logger
import play.api.test._
import play.api.test.Helpers._

class ApplicationSpec extends Specification {

  import models._

  "GET /" should {

    "render the landing page" in new WithApp {

      val request = FakeRequest(GET, "/")

      val response = route(request).get

      status(response) must equalTo(OK)
    }
  }

  "GET /about" should {

    "render the about page" in new WithApp {

      val request = FakeRequest(GET, "/about")

      val response = route(request).get

      status(response) must equalTo(OK)
    }
  }
}
