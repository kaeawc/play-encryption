package play.api.mvc

import models.User

import play.api.mvc.Results._
import play.api.data.Form
import play.api.data.Forms._
import play.api.templates.HtmlFormat

import scala.concurrent.{ExecutionContext,Future}
import ExecutionContext.Implicits.global

trait FormBinding extends Monitoring {

  def FormAsync[Tuple](
    form     : Form[Tuple],
    template : Form[Tuple] => HtmlFormat.Appendable
  )(
    success  : Tuple => Future[SimpleResult]
  ) = Action.async {
    implicit request => BindAsync(form,template)(success)
  }

  def FormAsync[Tuple](
    form    : Form[Tuple]
  )(
    success : Tuple => Future[SimpleResult]
  ) = Action.async {
    implicit request => BindAsync(form)(success)
  }

  def badFormAsJson[Tuple](form:Form[Tuple]) = Future {
    bad(form.errors.map {
      error =>
      error.key -> error.message
    })
  }

  def badForm[Tuple](
    form     : Form[Tuple],
    template : Form[Tuple] => HtmlFormat.Appendable
  ):Future[SimpleResult] = Future { BadRequest(template(form)) }

  def BindAsync[Tuple](
    form    : Form[Tuple]
  )(
    success : Tuple => Future[SimpleResult]
  )(implicit request:Request[AnyContent]) = {
    form.bindFromRequest match {
      case form:Form[Tuple] if form.hasErrors => badFormAsJson(form)
      case form:Form[Tuple] =>
        success(form.get)
    }
  }

  def BindAsync[Tuple](
    form     : Form[Tuple],
    template : Form[Tuple] => HtmlFormat.Appendable
  )(
    success  : Tuple => Future[SimpleResult]
  )(implicit
    request  : Request[AnyContent]
  ) = form.bindFromRequest match {
      case form:Form[Tuple] if form.hasErrors => badForm(form, template)
      case form:Form[Tuple] => success(form.get)
    }

  def BindUserAsync[Tuple](
    form     : Form[Tuple],
    template : Form[Tuple] => HtmlFormat.Appendable
  )(
    success  : Tuple => Future[SimpleResult]
  )(implicit 
    request  : Request[AnyContent],
    user     : Option[User]
  ) = form.bindFromRequest match {
      case form:Form[Tuple] if form.hasErrors => badForm(form, template)
      case form:Form[Tuple] => success(form.get)
    }

  def BindForm[Tuple](
    form    : Form[Tuple]
  )(
    success : Tuple => SimpleResult
  )(implicit
    request  : Request[AnyContent]
  ) = form.bindFromRequest match {
      case form:Form[Tuple] if form.hasErrors => Future { bad(form.errors.map {
          error =>
          error.key -> error.message
        })
      }
      case form:Form[Tuple] => Future { success(form.get) }
    }

  def FormAction[Tuple]
    (form    : Form[Tuple])
    (success : Tuple => SimpleResult) =
  Action.async { implicit request => 
    BindForm(form)(success)
  }
}
