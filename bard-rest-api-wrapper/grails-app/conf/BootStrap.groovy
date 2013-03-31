import bard.core.rest.spring.DataExportRestService
import bard.core.rest.spring.SunburstCacheService
import bard.core.rest.spring.SunburstRestService

class BootStrap {
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
            File locallyGeneratedResource = new File("../bard-web-client/grails-app/conf/resources/target.txt")
            File  resourceFromRDM = new File("../bard-web-client/grails-app/conf/resources/PantherProteinClassTree.txt")
            sunburstRestService.loadTargetsFromFile(locallyGeneratedResource,resourceFromRDM)
        } catch (Exception ee) {
            log.error(ee)
        }
    }
}
