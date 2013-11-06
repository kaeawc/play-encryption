package controllers

import play.api.Logger
import play.api.mvc._

import scala.concurrent.{ExecutionContext,Future}
import ExecutionContext.Implicits.global

object Dashboard extends Controller with Private {

  def home = UserAction { user => Future { Ok(views.html.home(Some(user))) } }

}
