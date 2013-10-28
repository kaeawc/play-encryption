package crypto.gcm

import common._

import java.security._

import javax.crypto._
import javax.crypto.spec._

import org.bouncycastle.jce.provider._

trait AES extends crypto.EncryptionScheme with play.api.mvc.Configuration {

  override val saltByteSize = 16

  val secretKey = new SecretKeySpec(appSalt, "AES")

  // val secretKey = new SecretKeySpec(appSecret)

  protected def initializationVector(bytes:Array[Byte]) = new IvParameterSpec(bytes)

  implicit val cipher:Cipher =
    Cipher.getInstance("AES/GCM/NoPadding", new BouncyCastleProvider())

  def encrypt(input:Array[Byte],salt:Array[Byte])(implicit cipher:Cipher) = {
    cipher.init(Cipher.ENCRYPT_MODE, secretKey, initializationVector(salt))
    cipher.doFinal(input)
  }

  def decrypt(input:Array[Byte])(implicit cipher:Cipher,iv:Array[Byte]) = {
    cipher.init(Cipher.DECRYPT_MODE, secretKey, initializationVector(iv))
    cipher.doFinal(input)
  }
}

object AES extends AES {

}