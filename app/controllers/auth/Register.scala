package controllers.auth

import play.api.Logger
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.libs.json._

import scala.concurrent.{ExecutionContext,Future}
import ExecutionContext.Implicits.global

import models._

object Register
extends Controller
with Private
with FormBinding
with CookieManagement {

  val registerForm = Form[Registration](
    mapping(
      "email"           -> email,
      "password"        -> text(minLength = 6),
      "retypedPassword" -> text(minLength = 6)
    )(Registration.apply)(Registration.unapply) verifying("Passwords must match",
      fields => fields match {
        case data:Registration => {
          data.password == data.retypedPassword
        }
      }
    )
  )

  def getForm = WithUser { user => Future { Ok(views.html.auth.register(registerForm,user)) } }

  def submit = FormAsync(registerForm) {
    registration:Registration =>

    User.create(registration) flatMap {
      case Some(user:User) =>
        createUserCookie(user) map {
          case Some(cookie:Cookie) =>
            Redirect(controllers.routes.Dashboard.home).withCookies(cookie)
          case _ =>
            InternalServerError(Json.obj("reason" -> "We could not create a secure persistant cookie for you."))
        }
      case _ =>
        Future { NotFound }
    }
  }

}