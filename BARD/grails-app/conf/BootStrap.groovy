import bard.db.ReadyForExtractListener
import bard.db.dictionary.OntologyDataAccessService
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.springframework.context.ApplicationContext
import org.springframework.web.context.support.WebApplicationContextUtils
import bard.validation.ext.ExternalOntologyPerson
import bard.validation.ext.ExternalOntologyFactory

class BootStrap {
    OntologyDataAccessService ontologyDataAccessService
    ExternalOntologyFactory externalOntologyFactory
    GrailsApplication grailsApplication

	def init = { servletContext ->
		externalOntologyFactory.getCreators().add(new ExternalOntologyPerson.PersonCreator())
		ontologyDataAccessService.computeTrees(false)

        def applicationContext = grailsApplication.mainContext
        applicationContext.eventTriggeringInterceptor.datastores.each { k, datastore ->
            println("datastore=${datastore} isinstance ${datastore instanceof org.grails.datastore.mapping.core.Datastore}")
            applicationContext.addApplicationListener(new ReadyForExtractListener(datastore))
        }
	}
	def destroy = {
	}

}
