package ir.roudi.node

import io.ktor.utils.io.core.*
import ir.roudi.crypto.CryptoHandler

data class NodeServer(
    val name: String,
    val port: Int
) {

    // circuit id -> origin port number
    private val map = mutableMapOf<Int, Int>()

    var publicKey = ""
        private set

    var privateKey = ""
        private set

    init {
        val keyPair = CryptoHandler.generateKeyPair()
        publicKey = keyPair.publicKey
        privateKey = keyPair.privateKey
    }

    fun encrypt(input: String) : String {
        return CryptoHandler.encryptWithPublicKey(input.toByteArray(), publicKey)
    }

    fun decrypt(encryptedInput: String) : String {
        return CryptoHandler.decryptWithPrivateKey(encryptedInput.toByteArray(), privateKey)
    }

    fun getOriginPortNumber(circuitId: Int) : Int? {
        return map[circuitId]
    }

    fun saveCircuitIdInfo(circuitId: Int, originPort: Int) {
        map[circuitId] = originPort
    }

}
