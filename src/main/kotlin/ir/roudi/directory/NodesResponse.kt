package ir.roudi.directory

import kotlinx.serialization.Serializable

@Serializable
data class NodesResponse(
    val nodes: List<Node>,
    val circuitId: Int
)
