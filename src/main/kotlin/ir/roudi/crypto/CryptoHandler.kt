package ir.roudi.crypto

import io.ktor.utils.io.core.*
import java.security.*
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import java.util.*
import javax.crypto.Cipher
import kotlin.String

object CryptoHandler {

    fun generateKeyPair(): KeyPair {
        val keyGen = KeyPairGenerator.getInstance("RSA")
        keyGen.initialize(1024)
        val keyPair = keyGen.generateKeyPair()
        return KeyPair(keyPair.public.encodeToString(), keyPair.private.encodeToString())
    }

    fun encryptWithPublicKey(data: String, publicKey: String) : String {
        return encryptWithPublicKey(data.toByteArray(), publicKey)
    }

    fun encryptWithPublicKey(data: ByteArray, publicKey: String): String {
        val cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding")
        cipher.init(Cipher.ENCRYPT_MODE, loadPublicKey(publicKey))
        return cipher.doFinal(data).encodeToString()
    }

    fun decryptWithPublicKey(encryptedData: ByteArray, publicKey: String): String {
        val cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding")
        cipher.init(Cipher.DECRYPT_MODE, loadPublicKey(publicKey))
        return String(cipher.doFinal(Base64.getDecoder().decode(encryptedData)))
    }

    private fun loadPublicKey(stored: String): PublicKey {
        val data: ByteArray = Base64.getDecoder().
        decode(stored.toByteArray())
        val spec = X509EncodedKeySpec(data)
        val fact = KeyFactory.getInstance("RSA")
        return fact.generatePublic(spec)
    }

    fun encryptWithPrivateKey(data: ByteArray, privateKey: String): String {
        val cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding")
        cipher.init(Cipher.ENCRYPT_MODE, loadPrivateKey(privateKey))
        return cipher.doFinal(data).encodeToString()
    }

    fun decryptWithPrivateKey(encryptedData: ByteArray, privateKey: String): String {
        val cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding")
        cipher.init(Cipher.DECRYPT_MODE, loadPrivateKey(privateKey))
        return String(cipher.doFinal(Base64.getDecoder().decode(encryptedData)))
    }

    private fun loadPrivateKey(key64: String): PrivateKey {
        val clear: ByteArray = Base64.getDecoder().
        decode(key64.toByteArray())
        val keySpec = PKCS8EncodedKeySpec(clear)
        val fact = KeyFactory.getInstance("RSA")
        val priv = fact.generatePrivate(keySpec)
        Arrays.fill(clear, 0.toByte())
        return priv
    }

    private fun Key.encodeToString(): String {
        return encoded.encodeToString()
    }

    private fun ByteArray.encodeToString(): String {
        return Base64.getEncoder().encodeToString(this)
    }
}