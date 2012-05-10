package bard.db.registration

import grails.converters.JSON
import org.springframework.dao.DataIntegrityViolationException

class AssayController {

    AssayService assayService

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index() {
        redirect(action: "list", params: params)
    }

    def list() {
        params.max = Math.min(params.max ? params.int('max') : 10, 100)
        [assayInstanceList: Assay.list(params), assayInstanceTotal: Assay.count()]
    }

    def create() {
        [assayInstance: new Assay(params)]
    }

    def save() {
        def assayInstance = new Assay(params)
        if (!assayInstance.save(flush: true)) {
            render(view: "create", model: [assayInstance: assayInstance])
            return
        }

		flash.message = message(code: 'default.created.message', args: [message(code: 'assay.label', default: 'Assay'), assayInstance.id])
        redirect(action: "show", id: assayInstance.id)
    }

    def show() {
        def assayInstance = Assay.get(params.id)
        if (!assayInstance) {
			flash.message = message(code: 'default.not.found.message', args: [message(code: 'assay.label', default: 'Assay'), params.id])
            redirect(action: "list")
            return
        }

        [assayInstance: assayInstance]
    }

    def edit() {
        def assayInstance = Assay.get(params.id)
        if (!assayInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'assay.label', default: 'Assay'), params.id])
            redirect(action: "list")
            return
        }

        [assayInstance: assayInstance]
    }

    def update() {
        def assayInstance = Assay.get(params.id)
        if (!assayInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'assay.label', default: 'Assay'), params.id])
            redirect(action: "list")
            return
        }

        if (params.version) {
            def version = params.version.toLong()
            if (assayInstance.version > version) {
                assayInstance.errors.rejectValue("version", "default.optimistic.locking.failure",
                          [message(code: 'assay.label', default: 'Assay')] as Object[],
                          "Another user has updated this Assay while you were editing")
                render(view: "edit", model: [assayInstance: assayInstance])
                return
            }
        }

        assayInstance.properties = params

        if (!assayInstance.save(flush: true)) {
            render(view: "edit", model: [assayInstance: assayInstance])
            return
        }

		flash.message = message(code: 'default.updated.message', args: [message(code: 'assay.label', default: 'Assay'), assayInstance.id])
        redirect(action: "show", id: assayInstance.id)
    }

    def delete() {
        def assayInstance = Assay.get(params.id)
        if (!assayInstance) {
			flash.message = message(code: 'default.not.found.message', args: [message(code: 'assay.label', default: 'Assay'), params.id])
            redirect(action: "list")
            return
        }

        try {
            assayInstance.delete(flush: true)
			flash.message = message(code: 'default.deleted.message', args: [message(code: 'assay.label', default: 'Assay'), params.id])
            redirect(action: "list")
        }
        catch (DataIntegrityViolationException e) {
			flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'assay.label', default: 'Assay'), params.id])
            redirect(action: "show", id: params.id)
        }
    }

    def details() {
        Assay assayInstance = Assay.get(params.id)
        if (!assayInstance) {
            response.status = 404
            return
        }
        Map mcmap = assayService.getMeasureContextItemsForAssay(assayInstance)

        /*
        // Define a recursive closure to assemble the JSON
        def buildForNode
        buildForNode = { def node ->
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
        */
        render buildForNode(mcmap) as JSON
    }

    private Map handleMeasureContextItem(MeasureContextItem item) {
        Map map = [:]
        map.id = item.id
        map.text = item.attributeElement.label
        map.value = item.valueDisplay
        if (item.children) {
            map.expanded = 'true'
            map.children = item.children.collect {
                Map childMap = [:]
                childMap.identity = item.id
                childMap.text = it.attributeElement.label
                childMap.value = it.valueDisplay
                childMap.leaf = true
                return childMap
            }
        } else {
            map.leaf = true
        }
        return map
    }

    private List handleList(List list) {
        list.collect {
            if (it instanceof Map) {
                return buildForNode(it)
            }
            else if (it instanceof MeasureContextItem) {
                return handleMeasureContextItem(it)
            }
            else if (it instanceof List) {
                return handleList(it)
            }
            else if (it instanceof String) {
                Map map = [:]
                map.text = it
                map.leaf = true
                return map
            }
            else {
                log.warn("Can't handle type: " + it)
                return null
            }
        }
    }

    private List buildForNode(Map inmap) {
        List list = []
        inmap.keySet().each {
            Map map = [:]
            map.text = it
            map.expanded = 'true'
            def value = inmap.get(it)
            if (value instanceof Map) {
                map.children = buildForNode(value)
            }
            else if (value instanceof List) {
                map.children = handleList(value)
            }
            else if (value instanceof MeasureContextItem) {
                map.children = handleMeasureContextItem(value)
            }
            else {
                log.debug("**************** " + it)
                map.leaf = true
            }
            list.push(map)
        }
        return list
    }
}
