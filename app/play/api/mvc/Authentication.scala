package play.api.mvc

import models.{User,UserSession}

import play.api.Logger

import play.api.mvc.Results._
import scala.concurrent.{Future,ExecutionContext}
import ExecutionContext.Implicits.global
import play.api.templates.Html

trait Authentication
extends Monitoring
with CookieManagement {

  /**
   * If request has authenticated user state do action A, otherwise do action B
   */
  def IfLoggedIn[A](
    a: Future[SimpleResult],
    b: Future[SimpleResult] = { Future { deny() } }
  )(implicit request:Request[A]):Future[SimpleResult] =
    userVisit[A] flatMap {
      case Some(user:User) => a
      case _               => b
    }

  def SessionAction(
    a       : Option[User] => Future[SimpleResult],
    session : UserSession
  )(implicit
    request:Request[AnyContent]
  ) = {
    User.getById(session.user) flatMap {
      case Some(user:User) => {

        createCookieFromSession(session) match {
          case Some(cookie:Cookie) =>
            replaceCookie(a(Some(user)),cookie)
          case _ =>
            removeCookie(a(None))
        }
      }
      case _ => removeCookie(a(None))
    }
  }

  /**
   * If request has authenticated user state do action A, otherwise do action B
   */
  def VisitAction(
    a: Option[User] => Future[SimpleResult]
  ) = Action.async {
    implicit request:Request[AnyContent] =>

    visit[AnyContent] flatMap {
      case Some(session:UserSession) => SessionAction(a,session)
      case _ => removeCookie(a(None))
    }
  }

  /**
   * If request has authenticated user state do action A, otherwise do action B
   */
  def PublicUserAction(
    a: Request[AnyContent] => Option[User] => Future[SimpleResult]
  ) = Action.async {
    implicit request:Request[AnyContent] =>
    userVisit[AnyContent] flatMap {
      case Some(user:User) => a(request)(Some(user))
      case _               => a(request)(None)
    }
  }

  /**
   * If request has authenticated user state do action A, otherwise do action B
   */
  def OnlyPublicAction(
    a: Request[AnyContent] => Future[SimpleResult],
    b: Future[SimpleResult] = { Future { Unauthorized(views.html.error.denied()) } }
  ) = Action.async {
    implicit request:Request[AnyContent] =>
    userVisit[AnyContent] flatMap {
      case None => a(request)
      case _    => b
    }
  }

  def UserAction(
    a       : User => Future[SimpleResult],
    b       : Future[SimpleResult],
    session : UserSession
  )(implicit
    request : Request[AnyContent]
  ):Future[SimpleResult] =
    User.getById(session.user) flatMap {
      case Some(user:User) =>
        createCookieFromSession(session) match {
          case Some(cookie:Cookie) =>
            replaceCookie(a(user),cookie)
          case _ =>
            removeCookie(b)
      }
      case _ =>
        removeCookie(b)
    }

  /**
   * If request has authenticated user state do action A, otherwise do action B
   */
  def OnlyUserAction(
    a: User => Future[SimpleResult],
    b: Future[SimpleResult] = { Future { Unauthorized(views.html.error.denied()) } }
  ) = Action.async {
    implicit request:Request[AnyContent] =>
    visit[AnyContent] flatMap {
      case Some(session:UserSession) =>
        UserAction(a,b,session)
      case _ =>
        removeCookie(b)
    }
  }

  /**
   * If request has authenticated user state do action A, otherwise do action B
   */
  def IfLoggedInPage[A](
    a: Future[SimpleResult],
    b: Future[SimpleResult] = { Future { Unauthorized(views.html.error.denied()) } }
  )(implicit request:Request[A]):Future[SimpleResult] =
    userVisit[A] flatMap {
      case Some(user:User) => a
      case _               => b
    }
}
