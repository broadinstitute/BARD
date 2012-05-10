package bard.db.dictionary

import groovy.json.JsonBuilder

class ResultTypeController {

    def list() {

        // Define a recursive closure to assemble the JSON
        def buildForNode
        buildForNode = { ResultType node ->
            def map=[:]
            map.id = node.id
            map.text = node.resultTypeName
            if (node.children) {
                map.children = node.children.collect {
                    buildForNode(it)
                }
            }
            else {
                map.leaf = true
            }
            return map
        }

        ResultType root = ResultType.findByParentIsNull()

        def builder = new JsonBuilder()
        builder(buildForNode(root))
        render builder.toString()
    }
}
