package bard.db.registration

import bard.db.experiment.GroupType
import grails.converters.JSON

class GroupTypeController {

    def list() {

        def types = []
        GroupType.list().each { GroupType it ->
            def attributeMap = [:]
            attributeMap.id = it.name()
            attributeMap.label = it.value
            types.add(attributeMap)
        }
        render types as JSON
    }
}
