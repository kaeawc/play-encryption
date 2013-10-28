package play.api.mvc

import play.api.data.Form
import play.api.data.Forms._

import scala.concurrent.{ExecutionContext,Future}
import ExecutionContext.Implicits.global

trait PrivateForm extends FormBinding with Private {

  override def FormAsync[Tuple]
    (form    : Form[Tuple])
    (success : Tuple => Future[SimpleResult]) =
  Action.async { implicit request =>
    IfLoggedIn[AnyContent](BindAsync(form)(success))
  }

  override def FormAction[Tuple]
    (form    : Form[Tuple])
    (success : Tuple => SimpleResult) =
  Action.async { implicit request => 
    IfLoggedIn[AnyContent](BindForm(form)(success))
  }
}
