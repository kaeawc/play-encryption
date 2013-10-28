package auth

import test._
import data._

import org.specs2.mutable._
import org.specs2.runner._
import org.junit.runner._

import play.api.Logger
import play.api.test._
import play.api.test.Helpers._
import play.api.libs.json._
import scala.concurrent.{ExecutionContext}
import ExecutionContext.Implicits.global

class LogoutSpec extends Specification {

  import models.User
  import models.User._

  "GET /logout" should {

    "return Unauthorize if not an authorized user." in new WithApp {

      val request = FakeRequest(GET, "/logout")

      val response = route(request).get

      status(response) must equalTo(401)
    }

    "wipe all cookies if an authorized user." in new WithApp {

      validLogin("someone@example.com", "password")

      val request = FakeRequest(GET, "/logout")

      val response = route(request).get

      status(response) must equalTo(401)
    }
  }
}
