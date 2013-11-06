import models._
import common._

import org.specs2.mutable._
import org.specs2.runner._
import org.junit.runner._

import play.api.libs.Crypto
import play.api.Logger
import play.api.test._
import play.api.mvc._
import play.api.test.Helpers._
import play.api.libs.json._
import scala.concurrent.{Future,ExecutionContext}
import ExecutionContext.Implicits.global

package object auth extends Specification {

  /**
   * The implicit result must be a redirect to the given URI
   */
  def mustRedirectTo(to:String)(implicit result:Future[SimpleResult]) = {

    status(result) must equalTo(303)

    redirectLocation(result).get must equalTo(to)

  }


  /**
   * Attempts to login with the given credentials
   */
  def attemptLogin(email:String,password:String) = {
      
    val header = FakeRequest(POST, "/login")
      .withHeaders(CONTENT_TYPE -> "application/x-www-form-urlencoded")

    val data = Json.obj("email" -> email, "password" -> password)

    val response = route(header,data).get

    response
  }

  def currentUser(implicit result:Future[SimpleResult]):Future[Option[User]] = {
    val cookie = getUserCookie
    cookie must beSome
    implicit val salt:Array[Byte] = Configuration.appSalt

    val session = Crypto.decryptAES(cookie.get.value)

    UserSession.fromJson(session) match {
      case Some(token:UserSession) => User.getById(token.user)
      case _ => Future { None }
    }
  }

  /**
   * Asserts that the current connection is authenticated as the given user
   */
  def mustBeAuthenticated(implicit result:Future[SimpleResult]) = currentUser map {
    case None => failure("Current session is not authenticated.")
    case _ => {}
  }

  def getUserCookie(implicit result:Future[SimpleResult]) = {
    cookies(result).get(Configuration.userCookieKey)
  }

  def authorizedCookie(email:String, password:String) = getUserCookie(validLogin(email, password)).get

  def createUser(email:String,password:String,retypedPassword:String) = {

    val header = FakeRequest(POST, "/register")

    val data = Json.obj("email" -> email, "password" -> password, "retypedPassword" -> retypedPassword)

    implicit val response = route(header,data).get

    mustRedirectTo("/home")

    User.getByEmail(email) map {
      case Some(user:User) => {
        user.email mustEqual email

        val salt = stringToBytes(user.salt)

        user.password mustEqual User.useSalt(password,salt)
      }
      case _ => failure("Could not parse the response as a User object")
    }

    response
  }

  def validLogin(email:String,password:String) = {
    
      implicit val response = createUser(email, password, password)

      mustBeAuthenticated

      response
  }
}