package crypto

import java.math.BigInteger
import java.security._
import java.security.spec._
import javax.crypto._
import javax.crypto.spec._

trait PBKDF2 extends Hash {

  val algorithm        = "PBKDF2WithHmacSHA1"

  override val saltByteSize     = 255
  val hashByteSize     = 255
  val iterations       = 1000

  val iterationIndex   = 0
  val saltIndex        = 1
  val index            = 2

  /**
   * Validates a password using a hash.
   *
   * @param   password        the password to check
   * @param   correctHash     the hash of the valid password
   * @return                  true if the password is correct, false if not
   */
  def validate(
    password    : String,
    correctHash : Array[Byte],
    salt        : Array[Byte]
  ):Boolean = {

    val hashedPassword = hash(password,salt)

    slowEquals(hashedPassword,correctHash)
  }

  /**
   *  Computes the hash of a password.
   *
   * @param   password    the password to hash.
   * @param   salt        the salt
   * @param   iterations  the iteration count (slowness factor)
   * @param   bytes       the length of the hash to compute in bytes
   * @return              the PBDKF2 hash of the password
   */
  def hash(
    password   : String,
    salt       : Array[Byte]
  ):Array[Byte] = {

    val spec = new PBEKeySpec(password.toCharArray(), salt, iterations, hashByteSize)
    val skf = SecretKeyFactory.getInstance(algorithm)
    skf.generateSecret(spec).getEncoded()
  }
}