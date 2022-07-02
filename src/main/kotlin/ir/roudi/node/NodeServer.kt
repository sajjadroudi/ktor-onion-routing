package ir.roudi.node

import io.ktor.utils.io.core.*
import ir.roudi.crypto.KeyGenerator

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
        val keyPair = KeyGenerator.generateKeyPair()
        publicKey = keyPair.publicKey
        privateKey = keyPair.privateKey
    }

    fun getOriginPortNumber(circuitId: Int) : Int? {
        return map[circuitId]
    }

    fun saveCircuitIdInfo(circuitId: Int, originPort: Int) {
        map[circuitId] = originPort
    }

}
