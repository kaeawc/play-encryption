package controllers.auth

import play.api.Logger
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.libs.json._

import scala.concurrent.{ExecutionContext,Future}
import ExecutionContext.Implicits.global

import models._

object Logout
extends Controller
with Private
with Configuration {

  def submit = LoggedIn {
    Future { Ok(views.html.landing()).discardingCookies(DiscardingCookie(userCookieKey)) }
  }
}