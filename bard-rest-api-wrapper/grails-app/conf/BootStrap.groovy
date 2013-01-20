import org.codehaus.groovy.grails.commons.ApplicationAttributes
import bard.core.rest.spring.DataExportRestService

class BootStrap {
    DataExportRestService dataExportRestService
    def init = { servletContext ->
        dataExportRestService.loadDictionary()
    }
    def destroy = {
    }
}
