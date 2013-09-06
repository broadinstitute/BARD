import bard.core.rest.spring.DictionaryRestService
import bard.core.rest.spring.SunburstRestService

class BootStrap {
    def grailsApplication
    DictionaryRestService dictionaryRestService
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
            dictionaryRestService.getDictionary()
        } catch (Exception ee) {
            log.error(ee)
        }
    }
    void loadTargets(){
        try {
            final File targets = new File("../BARD/web-app/WEB-INF/resources/PantherProteinClassTree.txt")
            assert targets.exists()
            sunburstRestService.loadTargetsFromFile(targets)
        } catch (Exception ee) {
            log.error(ee)
        }
    }
}
