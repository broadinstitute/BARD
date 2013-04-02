import bard.core.rest.spring.DataExportRestService
import bard.core.rest.spring.SunburstRestService

class BootStrap {
    def grailsApplication
    DataExportRestService dataExportRestService
    SunburstRestService sunburstRestService

    def init = { servletContext ->

        //load dictionary if the data export api is available
        loadCapDictionary()
        loadTargets()
    }
    def destroy = {
    }

    void loadCapDictionary() {
        try {
            dataExportRestService.getDictionary()
        } catch (Exception ee) {
            log.error(ee)
        }
    }
    void loadTargets(){
        try {
            final File targets = new File("../bard-web-client/web-app/WEB-INF/resources/PantherProteinClassTree.txt")
            assert targets.exists()
            sunburstRestService.loadTargetsFromFile(targets)
        } catch (Exception ee) {
            log.error(ee)
        }
    }
}
