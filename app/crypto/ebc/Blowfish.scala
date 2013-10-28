package crypto.ebc

import java.security.SecureRandom

import javax.crypto._
import javax.crypto.spec._

trait Blowfish extends crypto.EncryptionScheme {

  protected val keyGenerator = KeyGenerator.getInstance("Blowfish")
  protected val secretKey = keyGenerator.generateKey()
  protected val secretKeySpec = new SecretKeySpec(secretKey.getEncoded(), "Blowfish")
  protected val initializationVector = new IvParameterSpec(randomBytes(8))

  protected implicit def cipher:Cipher = Cipher.getInstance("Blowfish/EBC/PKCS5Padding")

  protected def encrypt(input:Array[Byte])(implicit cipher:Cipher) = {
    cipher.init(Cipher.ENCRYPT_MODE, secretKey, initializationVector)

    val encrypted = new Array[Byte](cipher.getOutputSize(input.length))
    var enc_len = cipher.update(input, 0, input.length, encrypted, 0)
    cipher.doFinal(encrypted, enc_len)
    encrypted
  }

  protected def decrypt(input:Array[Byte])(implicit cipher:Cipher,iv:Array[Byte]) = {
    cipher.init(Cipher.ENCRYPT_MODE, secretKey, initializationVector)

    val encrypted = new Array[Byte](cipher.getOutputSize(input.length))
    var enc_len = cipher.update(input, 0, input.length, encrypted, 0)
    cipher.doFinal(encrypted, enc_len)
    encrypted
  }
}
