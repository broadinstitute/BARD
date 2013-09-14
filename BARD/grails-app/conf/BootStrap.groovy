import bard.db.dictionary.OntologyDataAccessService
import bard.validation.ext.ExternalOntologyFactory
import bard.validation.extext.PersonCreator
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.codehaus.groovy.grails.plugins.springsecurity.SecurityFilterPosition
import org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils

class BootStrap {
    OntologyDataAccessService ontologyDataAccessService
    ExternalOntologyFactory externalOntologyFactory
    GrailsApplication grailsApplication

	def init = { servletContext ->
        SpringSecurityUtils.clientRegisterFilter('personaAuthenticationFilter', SecurityFilterPosition.SECURITY_CONTEXT_FILTER.order + 10)
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
