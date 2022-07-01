package ir.roudi.directory

import java.util.concurrent.atomic.AtomicInteger

object DirectoryDataSource {

    private val nodes = mutableListOf<Node>()
    private val circuitId = AtomicInteger(1)

    fun saveNode(node: Node) {
        nodes += node
    }

    fun getAllNodes() : List<Node> {
        return nodes.toList()
    }

    fun getLastCircuitId(): Int {
        return circuitId.getAndIncrement()
    }

}