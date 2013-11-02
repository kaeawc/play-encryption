package play.api.mvc

import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc.Results._
import scala.concurrent.{Future,ExecutionContext}
import ExecutionContext.Implicits.global

trait LoggedIn extends Authentication {

  /**
   * Action checks for authenticated user state
   */
  def LoggedIn(f: SimpleResult) =
    Action.async { implicit request => IfLoggedIn[AnyContent](Future { f }) }

  /**
   * Action checks for authenticated user state
   */
  def LoggedIn(f: Future[SimpleResult]) =
    Action.async { implicit request => IfLoggedIn[AnyContent](f) }

  /**
   * Action checks for authenticated user state
   */
  def LoggedIn[A](bp: BodyParser[A])(f: SimpleResult) =
    Action.async(bp) { implicit request => IfLoggedIn[A](Future { f }) }

  /**
   * Action checks for authenticated user state
   */
  def LoggedIn[A](bp: BodyParser[A])(f: Future[SimpleResult]) =
    Action.async(bp) { implicit request => IfLoggedIn[A](f) }

  /**
   * Action checks for authenticated user state
   */
  def LoggedInPage(f: SimpleResult) =
    Action.async { implicit request => IfLoggedIn[AnyContent](Future { f },{ Future { Redirect("/") } }) }

  /**
   * Action checks for authenticated user state
   */
  def LoggedInPage[A](bp: BodyParser[A])(f: SimpleResult) =
    Action.async(bp) { implicit request => IfLoggedInPage[A](Future { f },{ Future { Redirect("/") } }) }

  /**
   * Action checks for authenticated user state
   */
  def LoggedInPage(f: Future[SimpleResult]) =
    Action.async { implicit request => IfLoggedIn[AnyContent](f,{ Future { Redirect("/") } }) }

  /**
   * Action checks for authenticated user state
   */
  def LoggedInPage[A](bp: BodyParser[A])(f: Future[SimpleResult]) =
    Action.async(bp) { implicit request => IfLoggedInPage[A](f,{ Future { Redirect("/") } }) }
}
