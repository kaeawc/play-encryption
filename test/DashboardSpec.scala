
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

      status(response) mustEqual OK
    }

    "authorized cookie should only be valid once" in new WithApp {

      val cookie = authorizedCookie(Email.random(),Random.string(10))

      val request = FakeRequest(GET, "/home")
        .withCookies(cookie)

      val response = route(request).get

      status(response) mustEqual OK

      val badRequest = FakeRequest(GET, "/home")
        .withCookies(cookie)

      val badResponse = route(badRequest).get

      status(badResponse) mustEqual 401
    }

    "returned cookie should allow session to persist" in new WithApp {

      val request = FakeRequest(GET, "/home")
        .withCookies(authorizedCookie(Email.random(),Random.string(10)))

      val response = route(request).get

      status(response) mustEqual OK

      val cookie = getUserCookie(response)

      val nextRequest = FakeRequest(GET, "/home")
        .withCookies(cookie.get)

      val nextResponse = route(nextRequest).get

      status(nextResponse) mustEqual OK

      val cookieAgain = getUserCookie(nextResponse)

      val finalRequest = FakeRequest(GET, "/home")
        .withCookies(cookieAgain.get)

      val finalResponse = route(finalRequest).get

      status(finalResponse) mustEqual OK
    }
  }
}
