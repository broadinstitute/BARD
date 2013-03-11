import org.codehaus.groovy.grails.commons.ApplicationAttributes
import bard.core.rest.spring.DataExportRestService
import bard.core.rest.spring.ReloadCache

class BootStrap {
  //  DataExportRestService dataExportRestService
    def init = { servletContext ->
        //load dictionary if the data export api is available
  //      dataExportRestService.getDictionary()
    }
    def destroy = {
    }
}
