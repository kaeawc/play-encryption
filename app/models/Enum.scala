package models

import play.api.libs.json._
import play.api.libs.json.JsString
import scala.Some

trait Enum[A] {
  trait Value { self : A => }
  val values: List[A]
  def parse(v:String) : Option[A] = values.find(_.toString() == v)
}

trait EnumJson[A] {
  self : Enum[A] =>

  implicit def reads : Reads[A] = new Reads[A] {
    def reads(json: JsValue) : JsResult[A] = json match {
      case JsString(v) => parse(v) match {
        case Some(a) => JsSuccess(a)
        case _ => JsError(s"String value $v is not a valid enum item")
      }
      case _ => JsError("String value expected")
    }
  }

  implicit def writes : Writes[A] = new Writes[A] {
    def writes(v: A) : JsValue = JsString(v.toString)
  }
}
