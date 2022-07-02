package ir.roudi.crypto

import io.ktor.utils.io.core.*
import java.security.Key
import java.security.PublicKey
import java.util.*
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec

object NewCryptoHandler {

    fun encrypt(data: String, key: Key): EncryptedContent {
        val symmetricKey = KeyGenerator.generateSymmetricKey()
        val encryptedData = encryptWithSymmetricKey(data, symmetricKey)
        val encryptedKey = encryptWithRsaKey(symmetricKey.encodeToString(), key)
        return EncryptedContent(encryptedKey, encryptedData)
    }

    fun decrypt(content: EncryptedContent, key: Key) : String {
        val symmetricKey = decryptWithRsaKey(content.key, key)
        return decryptWithSymmetricKey(content.data, symmetricKey)
    }

    private fun encryptWithRsaKey(data: String, key: Key) : String {
        val cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding")
        cipher.init(Cipher.ENCRYPT_MODE, key)
        return Base64.getEncoder().encodeToString(cipher.doFinal(data.toByteArray()))
    }

    private fun decryptWithRsaKey(data: String, key: Key) : String {
        val cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding")
        cipher.init(Cipher.DECRYPT_MODE, key)
        return String(cipher.doFinal(Base64.getDecoder().decode(data)))
    }

    private fun encryptWithSymmetricKey(data: String, key: SecretKey): String {
        val aesCipher = Cipher.getInstance("AES")
        aesCipher.init(Cipher.ENCRYPT_MODE, key)
        return Base64.getEncoder().encodeToString(aesCipher.doFinal(data.toByteArray()))
    }

    private fun decryptWithSymmetricKey(data: String, key: String) : String {
        val keyByteArray = key.toBytes() // TODO: This conversion may cause error
        val symmetricKey = SecretKeySpec(keyByteArray, 0, keyByteArray.size, "AES")
        val aesCipher = Cipher.getInstance("AES")
        aesCipher.init(Cipher.DECRYPT_MODE, symmetricKey)
        return String(aesCipher.doFinal(Base64.getDecoder().decode(data)))
    }

    private fun ByteArray.encodeToString(): String {
        return Base64.getEncoder().encodeToString(this)
    }

    private fun String.toBytes() : ByteArray {
        return Base64.getDecoder().decode(this)
    }

    private fun Key.encodeToString(): String {
        return encoded.encodeToString()
    }

}