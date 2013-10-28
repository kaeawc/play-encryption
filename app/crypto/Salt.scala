package crypto

import java.security._
import common._

trait Salt {
  
  val saltByteSize:Int
  
  def createSalt(size:Int = saltByteSize):Array[Byte] = {
    val random = new SecureRandom()
    val salt   = new Array[Byte](size)
    random.nextBytes(salt)
    salt
  }
}
