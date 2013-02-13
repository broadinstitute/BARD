import org.codehaus.groovy.grails.commons.ApplicationAttributes
import bard.core.rest.spring.DataExportRestService
import bard.core.rest.spring.ReloadCache

class BootStrap {
    DataExportRestService dataExportRestService
    def init = { servletContext ->

        dataExportRestService.getDictionary(ReloadCache.YES)
    }
    def destroy = {
    }
}
