package bard.db.dictionary

import groovy.json.JsonBuilder

class ElementController {

    BuildElementPathsService buildElementPathsService

    def list() {

        // Define a recursive closure to assemble the JSON
        def buildForNode
        buildForNode = { Element node ->
            def map=[:]
            map.id = node.id
            map.text = node.label
            if (node.elements) {
                map.children = node.elements.collect {
                    buildForNode(it)
                }
            }
            else {
                map.leaf = true
            }
            return map
        }

        Element root = Element.findByParentElementIsNull()

        def builder = new JsonBuilder()
        builder(buildForNode(root))
        render builder.toString()
    }


    def edit() {
        List<ElementAndFullPath> list = buildElementPathsService.createListSortedByString(buildElementPathsService.buildAll())

        [list: list, maxPathLength: buildElementPathsService.maxPathLength]
    }

    def update() {
        render("hello world")
    }
}
