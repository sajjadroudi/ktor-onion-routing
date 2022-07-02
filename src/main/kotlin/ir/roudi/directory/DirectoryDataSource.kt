package ir.roudi.directory

import ir.roudi.Logger
import java.util.concurrent.atomic.AtomicInteger

object DirectoryDataSource {

    private val nodes = mutableListOf<Node>()
    private val circuitId = AtomicInteger(1)

    fun saveNode(node: Node) {
        nodes += node
        Logger.log("DirectoryDataSource", "Save a new node ${node.name}")
    }

    fun getAllNodes() : List<Node> {
        Logger.log("DirectoryDataSource", "All nodes are gotten.")
        return nodes.toList()
    }

    fun getLastCircuitId(): Int {
        Logger.log("DirectoryDataSource", "Last circuit id incremented by one")
        return circuitId.getAndIncrement()
    }

}