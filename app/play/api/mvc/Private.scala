package play.api.mvc


import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc.Results._
import scala.concurrent.{Future,ExecutionContext}
import ExecutionContext.Implicits.global

trait Private extends Authentication with LoggedIn {

  override def Monitored[A](f: Future[SimpleResult]) =
    Action.async { implicit request => IfLoggedIn[AnyContent](f) }

}
