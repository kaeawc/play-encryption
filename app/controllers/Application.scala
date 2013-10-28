package controllers

import play.api.mvc._

import scala.concurrent.{ExecutionContext,Future}
import ExecutionContext.Implicits.global

object Application
extends Controller
with Private {

  def landing = WithUser { user => Future { Ok(views.html.landing(user)) } }

  def about   = WithUser { user => Future { Ok(views.html.about(user)) } }
}
