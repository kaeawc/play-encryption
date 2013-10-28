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

class RegisterSpec extends Specification {

  import models.User
  import models.User._

  "POST /register" should {

    "return Accepted if user was created" in new WithApp {

      val password = Random.string(10)

      createUser(Email.random(), password, password)
    }

    "return BadRequest if passwords don't match" in new WithApp {

      val request = FakeRequest(POST, "/register")

      val response = route(request).get

      status(response) must equalTo(400)
      contentType(response) must beSome("application/json")
      
      val expectedError = Json.obj(
        "email" -> "error.required",
        "password" -> "error.required",
        "retypedPassword" -> "error.required"
      )
      
      contentAsString(response) mustEqual(expectedError.toString)
    }

    "return BadRequest if not all information was sent." in new WithApp {

      val request = FakeRequest(POST, "/register")

      val data = Json.obj(
        "email" -> Email.random(),
        "password" -> Random.string(10),
        "retypedPassword" -> Random.string(10)
      )

      val response = route(request,data).get

      status(response) must equalTo(400)
      contentType(response) must beSome("application/json")
      
      val expectedError = Json.obj("" -> "Passwords must match")
      
      contentAsString(response) mustEqual(expectedError.toString)
    }
  }
}
