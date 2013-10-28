package crypto

import java.math.BigInteger
import java.security._
import java.security.spec._
import javax.crypto._
import javax.crypto.spec._

trait Hash extends Salt {

  val algorithm:String

  def hash(
    password   : String,
    salt       : Array[Byte] = createSalt()
  ):Array[Byte]

  def validate(
    plainText  : String,
    hashedText : Array[Byte],
    salt       : Array[Byte]
  ):Boolean

  /**
   * Compares two byte arrays in length-constant time. This comparison method
   * is used so that password hashes cannot be extracted from an on-line 
   * system using a timing attack and then attacked off-line.
   * 
   * @param   a       the first byte array
   * @param   b       the second byte array 
   * @return          true if both byte arrays are the same, false if not
   */
  protected def slowEquals(
    a : Array[Byte],
    b : Array[Byte]
  ):Boolean = {
    
    var diff = a.length ^ b.length

    for(i <- 0 until a.length if i < b.length)
      diff += a(i) ^ b(i)

    diff == 0
  }

}