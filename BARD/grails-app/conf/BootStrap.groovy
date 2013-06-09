import bard.db.ReadyForExtractListener
import bard.db.dictionary.OntologyDataAccessService
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.springframework.context.ApplicationContext
import org.springframework.web.context.support.WebApplicationContextUtils
import bard.validation.ext.ExternalOntologyPerson
import bard.validation.ext.ExternalOntologyFactory
import bard.validation.ext.ExternalOntologyPerson

class BootStrap {
    OntologyDataAccessService ontologyDataAccessService
    ExternalOntologyFactory externalOntologyFactory
    GrailsApplication grailsApplication

	def init = { servletContext ->
        loadPersonOntology()
        computeTrees()

        def applicationContext = grailsApplication.mainContext
        applicationContext.eventTriggeringInterceptor.datastores.each { k, datastore ->
            println("datastore=${datastore} isinstance ${datastore instanceof org.grails.datastore.mapping.core.Datastore}")
            applicationContext.addApplicationListener(new ReadyForExtractListener(datastore))
        }
	}

	def destroy = {
	}

    void loadPersonOntology(){
        try {
            externalOntologyFactory.getCreators().add(new ExternalOntologyPerson.PersonCreator())
        } catch (Exception ee) {
            log.error(ee)
        }
    }

    void computeTrees(){
        try {
            ontologyDataAccessService.computeTrees(false)
        } catch (Exception ee) {
            log.error(ee)
        }
    }

}
