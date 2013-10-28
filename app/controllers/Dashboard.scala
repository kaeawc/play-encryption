package controllers

import play.api.mvc._

import scala.concurrent.{ExecutionContext,Future}
import ExecutionContext.Implicits.global

object Dashboard extends Controller with Private {

  def home = IfWithUser { user => Future { Ok(views.html.home(Some(user))) } }

}
