import bard.core.rest.spring.DataExportRestService

class BootStrap {
    DataExportRestService dataExportRestService
    def init = { servletContext ->
        //load dictionary if the data export api is available
        dataExportRestService.getDictionary()
    }
    def destroy = {
    }
}
