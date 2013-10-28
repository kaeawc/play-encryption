import play.api._

import java.security._

import scala.concurrent.duration._
import scala.concurrent.{Future,Await,ExecutionContext}
import ExecutionContext.Implicits.global
import org.bouncycastle.jce.provider._

object Global extends GlobalSettings {

  override def onStart(app: Application) {
    Security.addProvider(new BouncyCastleProvider())
  }

}