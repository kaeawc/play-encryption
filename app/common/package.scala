
import net.iharder.Base64

package object common {

  implicit def stringToBytes(str:String):Array[Byte] =
    str.toCharArray.map(_.toByte)

  def base64ToBytes(str:String):Array[Byte] =
    Base64.decode(str)

  def bytesToBase64(bytes:Array[Byte]):String =
    Base64.encodeObject(bytes)

  implicit def bytesToString(bytes:Array[Byte]):String =
    new String(bytes.map(_.toChar))

  def hex2bytes(hex: String): Array[Byte] = {
    hex.replaceAll("[^0-9A-Fa-f]", "").sliding(2, 2).toArray.map(Integer.parseInt(_, 16).toByte)
  }
 
  def bytes2hex(bytes: Array[Byte], sep: Option[String] = None): String = {
    sep match {
      case None => bytes.map("%02x".format(_)).mkString
      case _ => bytes.map("%02x".format(_)).mkString(sep.get)
    }
    // bytes.foreach(println)
  }
  
}