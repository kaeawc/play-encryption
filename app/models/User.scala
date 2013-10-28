package models

import anorm._
import anorm.SqlParser._

import common._

import play.api.Logger
import play.api.db.DB
import play.api.Play.current
import play.api.libs.json._

import java.util.Date
import scala.concurrent.{Await, Future, ExecutionContext}
import scala.concurrent.duration._
import ExecutionContext.Implicits.global

case class User(
  id       : Long,
  email    : String,
  password : String,
  salt     : String,
  created  : Date = new Date()
)

object User
extends ((
  Long,
  String,
  String,
  String,
  Date
) => User)
with Authentication[User] {

  implicit val r = Json.reads[User]
  implicit val w = Json.writes[User]

  def parse(json:String) = 
    Json.fromJson(Json.parse(json)).get

  val table = "user"

  val users =
    long("id") ~
    str("email") ~
    str("password") ~
    str("salt") ~
    date("created") map {
      case   id~email~password~salt~created =>
        User(id,email,password,salt,created)
    }

  def getById(id:Long) = Future {
    DB.withConnection { implicit connection =>
      SQL(
        """
          SELECT
            a.id,
            a.email,
            a.password,
            a.salt,
            a.created
          FROM user a
          WHERE id = {id};
        """
      ).on(
        'id -> id
      ).as(users.singleOpt)
    }
  }

  def getByEmail(email:String) = Future {
    DB.withConnection { implicit connection =>
      SQL(
        """
          SELECT
            a.id,
            a.email,
            a.password,
            a.salt,
            a.created
          FROM user a
          WHERE email = {email};
        """
      ).on(
        'email -> email
      ).as(users.singleOpt)
    }
  }

  def countAll = Future {
    DB.withConnection { implicit connection =>
      val result = SQL(
        """
          SELECT COUNT(1) count
          FROM account a;
        """
      ).apply()

      try {
        Some(result.head[Long]("count"))
      } catch {
        case e:Exception => None
      }
    }
  }

  def create(registration:Registration) = {

    val email             = registration.email
    val password          = stringToBytes(registration.password)
    val salt              = createSalt()
    val created           = new Date()
    val hashedPassword    = useSalt(password,salt)
    val storedSalt:String = salt

    getByEmail(email) map {
      case Some(user:User) => {
        Logger.error("User already created")
        None
      }
      case _ => {
        DB.withConnection { implicit connection =>
          SQL(
            """
              INSERT INTO user (
                email,
                password,
                salt,
                created
              ) VALUES (
                {email},
                {password},
                {salt},
                {created}
              );
            """
          ).on(
            'email    -> email,
            'password -> hashedPassword,
            'salt     -> storedSalt,
            'created  -> created
          ).executeInsert()
        }
      }
    } map {
      case Some(id:Long) => {
        Some(User(
          id,
          email,
          hashedPassword,
          storedSalt,
          created
        ))
      }
      case _ => {
        Logger.error("User wasn't created")
        None
      }
    }
  }
  
  def authenticate(login:LoginCredentials) =
    getByEmail(login.email) map {
      case Some(user:User) => {

        val password       = stringToBytes(login.password)
        val salt           = hex2bytes(user.salt)
        val hashedPassword = bytes2hex(useSalt(password,salt))
        val correctHash    = bytes2hex(user.password)

        if (correctHash == hashedPassword)
          Some(user)
        else
          None
      } 
      case _ => None
    }
}
