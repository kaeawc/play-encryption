package play.api.mvc

import play.api.mvc.Results._
import scala.concurrent.{Future,ExecutionContext}
import ExecutionContext.Implicits.global

trait Public extends Authentication {

  /**
   * Action checks for authenticated user state
   */
  def OnlyPublic(f: SimpleResult) =
    Action.async { implicit request => IfLoggedIn[AnyContent]({ Future { Redirect("/") } }, Future { f } ) }

  /**
   * Action checks for authenticated user state
   */
  def OnlyPublic[A](bp: BodyParser[A])(f: SimpleResult) =
    Action.async(bp) { implicit request => IfLoggedIn[A]({ Future { Redirect("/") } }, Future { f }) }

  /**
   * Action checks for authenticated user state
   */
  def OnlyPublic(f: Future[SimpleResult]) =
    Action.async { implicit request => IfLoggedIn[AnyContent]({ Future { Redirect("/") } }, f) }

  /**
   * Action checks for authenticated user state
   */
  def OnlyPublic[A](bp: BodyParser[A])(f: Future[SimpleResult]) =
    Action.async(bp) { implicit request => IfLoggedIn[A]({ Future { Redirect("/") } }, f) }
}
