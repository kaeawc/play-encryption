package controllers.auth

import play.api.Logger
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.libs.json._

import scala.concurrent.{ExecutionContext,Future}
import ExecutionContext.Implicits.global

import models._

object Login
extends Controller
with FormBinding
with Public
with Private
with CookieManagement {

  val loginForm = Form[LoginCredentials](
    mapping(
      "email"    -> email,
      "password" -> text(minLength = 6)
    )(LoginCredentials.apply)(LoginCredentials.unapply)
  )

  def getForm = OnlyPublic { Ok(views.html.auth.login(loginForm,None)) }

  def template(form:Form[LoginCredentials]) = views.html.auth.login(form)

  def submit = OnlyPublicAction {

    implicit request =>

    BindAsync(loginForm,template) {
      login:LoginCredentials =>

      User.authenticate(login) flatMap {
        case Some(user:User) =>
          createUserCookie(user.id) map {
            case Some(cookie:Cookie) =>
              Redirect(controllers.routes.Dashboard.home).discardingCookies(DiscardingCookie(userCookieKey)).withCookies(cookie)
            case _ =>
              InternalServerError(Json.obj("reason" -> "We could not create a secure persistant cookie for you."))
          }
        case _ =>
          Future { Unauthorized(Json.obj("reason" -> "Your credentials are invalid.")) }
      }
    }
  }
}