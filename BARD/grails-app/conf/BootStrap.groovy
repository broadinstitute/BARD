import bard.db.dictionary.OntologyDataAccessService
import bard.validation.extext.PersonCreator
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.springframework.context.ApplicationContext
import org.springframework.web.context.support.WebApplicationContextUtils
import bard.validation.extext.ExternalOntologyPerson
import bard.validation.ext.ExternalOntologyFactory
import bard.validation.extext.ExternalOntologyPerson

class BootStrap {
    OntologyDataAccessService ontologyDataAccessService
    ExternalOntologyFactory externalOntologyFactory
    GrailsApplication grailsApplication

	def init = { servletContext ->
        loadPersonOntology()
        computeTrees()
	}

	def destroy = {
	}

    void loadPersonOntology(){
        try {
            externalOntologyFactory.getCreators().add(new PersonCreator())
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
