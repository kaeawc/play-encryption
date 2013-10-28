package play.api.mvc

import play.api.data.Form
import play.api.data.Forms._

import scala.concurrent.{ExecutionContext,Future}
import ExecutionContext.Implicits.global

trait FormBinding extends Monitoring {

  def FormAsync[Tuple](
    form    : Form[Tuple]
  )(
    success : Tuple => Future[SimpleResult]
  ) =
  Action.async {
    implicit request => BindAsync(form)(success)
  }


  def BindAsync[Tuple](
    form    : Form[Tuple]
  )(
    success : Tuple => Future[SimpleResult]
  )(implicit request:Request[AnyContent]) = {
    form.bindFromRequest match {
      case form:Form[Tuple] if form.hasErrors => Future { bad(form.errors.map {
          error =>
          error.key -> error.message
        })
      }
      case form:Form[Tuple] =>
        success(form.get)
    }
  }

  def BindForm[Tuple](
    form    : Form[Tuple]
  )(
    success : Tuple => SimpleResult
  )(implicit request:Request[AnyContent]) = {
    form.bindFromRequest match {
      case form:Form[Tuple] if form.hasErrors => Future { bad(form.errors.map {
          error =>
          error.key -> error.message
        })
      }
      case form:Form[Tuple] =>
        Future { success(form.get) }
    }
  }

  def FormAction[Tuple]
    (form    : Form[Tuple])
    (success : Tuple => SimpleResult) =
  Action.async { implicit request => 
    BindForm(form)(success)
  }
}
