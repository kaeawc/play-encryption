package play.api.mvc

import models._
import play.api.Logger
import play.api.libs.json._
import play.api.mvc.Results._
import scala.concurrent.{Future,ExecutionContext}
import ExecutionContext.Implicits.global

trait Monitoring {

  /**
   * Return a HTTP 400 JSON response
   */
  def bad(reasons:JsValue):SimpleResult = BadRequest(reasons)

  /**
   * Return a HTTP 401 JSON response
   */
  def deny(reasons:JsValue):SimpleResult = Unauthorized(reasons)
  
  /**
   * Return a HTTP 400 JSON response of the form {'reason':reason}
   */
  def bad(reason:String = "Could not process your request."):SimpleResult = bad(seqToJson(Seq("reason" -> reason)))
  
  /**
   * Return a HTTP 401 JSON response of the form {'reason':reason}
   */
  def deny(reason:String = "You are not logged in."):SimpleResult = deny(seqToJson(Seq("reason" -> reason)))
  
  /**
   * Return a HTTP 400 JSON response composed from the sequence collection
   */
  def bad(reasons:Seq[(String,String)]):SimpleResult = bad(seqToJson(reasons))
  
  /**
   * Return a HTTP 401 JSON response composed from the sequence collection
   */
  def deny(reasons:Seq[(String,String)]):SimpleResult = deny(seqToJson(reasons))

  implicit def kvToSeq(kv:(String,String)):JsValue = seqToJson(Seq(kv))
  implicit def seqToJson(seq:Seq[(String,String)]):JsValue = JsObject(seq.map { case(k,v) => (k,JsString(v))})



  val noFuture = Future { None }

  /**
   * Attempts to get user metadata.
   */
  implicit def userVisit[A](implicit request: Request[A]):Future[Option[User]] = {
    visit[A] flatMap {
      case Some(session:UserSession) => User.getById(session.user)
      case _ => noFuture
    }
  }

  /**
   * Gets a user implicitly from request 
   */
  implicit def getUser(implicit request: Request[AnyContent]):Future[User] =
    userVisit[AnyContent] map {
      case Some(user:User) => user
      case _ => throw new Exception("Failed to get current user.")
    }

  /**
   * Gets a client visit visit
   */
  implicit def getSession(implicit request: Request[AnyContent]):Future[UserSession] =
    visit[AnyContent] map {
      case Some(session:UserSession) => session
      case _ => throw new Exception("Failed to get current user session.")
    }

  /**
   * Attempts to parse the request implicitly for a client UserView
   */
  implicit def visit[A](implicit request: Request[A]):Future[Option[UserSession]] =
    Visit.log(request)

  /**
   * Whether or not the request has an authenticated user, perform the action.
   */
  def Monitored[A](a: Future[SimpleResult]) =
    Action.async { implicit request =>
      userVisit flatMap {
        case Some(user:User) => a
        case _               => a
      }
    }

  def Page[A](a: Future[SimpleResult]) = Monitored[A](a)

  def Page[A](a: SimpleResult) = Monitored[A]( Future { a })
}
