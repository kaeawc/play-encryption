package crypto

import java.security._
import common._

trait Salt {
  
  val saltByteSize:Int = 16
  
  def createSalt(size:Int = saltByteSize):Array[Byte] = {
    val random = new SecureRandom()
    val salt   = new Array[Byte](size)
    random.nextBytes(salt)
    salt
  }
  
  def safeSalt(size:Int = saltByteSize):Array[Byte] = {
    val random = new SecureRandom()
    val salt   = new Array[Byte](size)
    random.nextBytes(salt)
    salt
  }
}
