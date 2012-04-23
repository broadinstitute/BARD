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
            if (node.childResultTypes) {
                map.children = node.childResultTypes.collect {
                    buildForNode(it)
                }
            }
            else {
                map.leaf = true
            }
            return map
        }

        ResultType root = ResultType.findByParentResultTypeIsNull()

        def builder = new JsonBuilder()
        builder(buildForNode(root))
        render builder.toString()
    }
}
