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
with Public
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

  def getForm = VisitAction { implicit user => Future { Ok(template(registerForm)) } }

  def template(form:Form[Registration]) = views.html.auth.register(form,None)

  def submit = OnlyPublicAction {

    implicit request =>

    BindAsync(registerForm,template) {
      registration:Registration =>

      User.create(registration) flatMap {
        case Some(user:User) =>
          createUserCookie(user.id) map {
            case Some(cookie:Cookie) =>
              Redirect(controllers.routes.Dashboard.home).discardingCookies(DiscardingCookie(userCookieKey)).withCookies(cookie)
            case _ =>
              InternalServerError(Json.obj("reason" -> "We could not create a secure persistant cookie for you."))
          }
        case _ =>
          Future { NotFound }
      }
    }
  }

}