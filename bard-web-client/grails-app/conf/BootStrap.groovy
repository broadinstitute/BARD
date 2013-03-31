import bard.core.rest.spring.DataExportRestService
import bard.core.rest.spring.SunburstCacheService
import bard.core.rest.spring.SunburstRestService
import org.codehaus.groovy.grails.web.servlet.GrailsApplicationAttributes
import org.apache.catalina.core.ApplicationContext
import org.codehaus.groovy.grails.web.context.ServletContextHolder
import org.codehaus.groovy.grails.commons.ApplicationHolder
import org.springframework.core.io.Resource

class BootStrap {
    DataExportRestService dataExportRestService
    SunburstRestService sunburstRestService
    //return (ApplicationContext) ServletContextHolder.getServletContext().getAttribute(GrailsApplicationAttributes.APPLICATION_CONTEXT);

    def init = { servletContext ->
        String resourceName = "resources/target.txt"
        String rdmResourceName = "resources/PantherProteinClassTree.txt"


        def resource1 = ApplicationHolder.application.parentContext.getResource("classpath:$resourceName")
        def resource2 = ApplicationHolder.application.parentContext.getResource("classpath:$rdmResourceName")

        File locallyGeneratedResource =resource1.getFile()
        File  resourceFromRDM = resource2.getFile()

        //load dictionary if the data export api is available
        loadCapDictionary()
        loadTargets(locallyGeneratedResource,resourceFromRDM)
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
    void loadTargets( final File locallyGeneratedResource,final File  resourceFromRDM){
        try {
            sunburstRestService.loadTargetsFromFile(locallyGeneratedResource,resourceFromRDM)
        } catch (Exception ee) {
            log.error(ee)
        }
    }
}
