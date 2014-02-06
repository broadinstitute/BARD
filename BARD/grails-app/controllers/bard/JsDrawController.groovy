package bard

import org.codehaus.groovy.grails.commons.GrailsApplication

class JsDrawController {

    GrailsApplication grailsApplication

    def license() {
        def code = grailsApplication.config.jsDraw.license
        if (!code) {
            response.sendError(404)
        } else {
            render(text:"JSDraw2.licensecode=\"${code}\";",contentType:"application/javascript")
        }
    }
}
