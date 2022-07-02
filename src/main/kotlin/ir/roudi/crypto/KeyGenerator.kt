package ir.roudi.crypto

import ir.roudi.crypto.KeyGenerator.encodeToString
import java.security.Key
import java.security.KeyPairGenerator
import java.util.*
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey

object KeyGenerator {

    fun generateKeyPair(): KeyPair {
        val keyGen = KeyPairGenerator.getInstance("RSA")
        keyGen.initialize(1024)
        val keyPair = keyGen.generateKeyPair()
        return KeyPair(
            keyPair.public.encodeToString(),
            keyPair.private.encodeToString()
        )
    }

    fun generateSymmetricKey(): SecretKey {
        val generator = KeyGenerator.getInstance("AES")
        generator.init(128)
        return generator.generateKey()
    }

    private fun Key.encodeToString(): String {
        return encoded.encodeToString()
    }

    private fun ByteArray.encodeToString(): String {
        return Base64.getEncoder().encodeToString(this)
    }


}