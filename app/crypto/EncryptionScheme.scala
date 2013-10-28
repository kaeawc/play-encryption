package crypto

import java.security._

import javax.crypto._
import javax.crypto.spec._

trait EncryptionScheme extends Salt {

  protected def randomBytes(length:Int):Array[Byte] = {
    val random = new SecureRandom()
    val bytes = new Array[Byte](length)
    random.nextBytes(bytes)
    bytes
  }
}