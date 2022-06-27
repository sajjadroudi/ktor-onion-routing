package ir.roudi.directory

object DirectoryDataSource {

    private val nodes = mutableListOf<Node>()

    fun saveNode(node: Node) {
        nodes += node
    }

    fun getAllNodes() : List<Node> {
        return nodes.toList()
    }

}