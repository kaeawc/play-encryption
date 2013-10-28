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

class LoginSpec extends Specification {

  import models.User
  import models.User._

  "POST /login" should {

    "return Unauthorized if user doesn't exist" in new WithApp {

      val header = FakeRequest(POST, "/login")

      val data = Json.obj("email" -> Email.random(), "password" -> Random.string(10))

      val response = route(header,data).get

      status(response) must equalTo(401)
    }

    "return Unauthorized if user exists but doesn't match credentials" in new WithApp {

      val password = Random.string(10)

      createUser(Email.random(), password, password)
      
      val header = FakeRequest(POST, "/login")

      val data = Json.obj("email" -> "someone@example.com", "password" -> Random.string(10))

      val response = route(header,data).get

      status(response) must equalTo(401)
    }

    "Redirect to /home when credentials match up" in new WithApp {

      validLogin(Email.random(), Random.string(10))
    }

    "return BadRequest if not all information was sent." in new WithApp {

      val request = FakeRequest(POST, "/login")

      val response = route(request).get

      status(response) must equalTo(400)
      contentType(response) must beSome("application/json")
      
      val expectedError = Json.obj(
        "email" -> "error.required",
        "password" -> "error.required"
      )
      
      contentAsString(response) mustEqual(expectedError.toString)
    }
  }
}
