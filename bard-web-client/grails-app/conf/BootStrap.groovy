import bard.core.rest.spring.DataExportRestService
import bard.core.rest.spring.SunburstCacheService
import bard.core.rest.spring.SunburstRestService
import org.codehaus.groovy.grails.web.servlet.GrailsApplicationAttributes
import org.apache.catalina.core.ApplicationContext
import org.codehaus.groovy.grails.web.context.ServletContextHolder
import org.codehaus.groovy.grails.commons.ApplicationHolder
import org.springframework.core.io.Resource

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
            final File targets = grailsApplication.parentContext.getResource("/WEB-INF/resources/PantherProteinClassTree.txt").file
              sunburstRestService.loadTargetsFromFile(targets)
        } catch (Exception ee) {
            log.error(ee)
        }
    }
}
