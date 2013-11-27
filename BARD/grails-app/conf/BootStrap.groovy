import bard.db.dictionary.OntologyDataAccessService
import bard.util.BardCacheUtilsService
import bard.validation.ext.ExternalOntologyFactory
import bard.validation.extext.PersonCreator
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.codehaus.groovy.grails.plugins.springsecurity.SecurityFilterPosition
import org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils

class BootStrap {
    BardCacheUtilsService bardCacheUtilsService
    ExternalOntologyFactory externalOntologyFactory
    GrailsApplication grailsApplication

	def init = { servletContext ->
        if(grailsApplication.config.grails.plugin.databasemigration.updateOnStart) {
            // if we are starting solely for the purpose of updating the schema, don't bother
            // populating caches
            return
        }

        SpringSecurityUtils.clientRegisterFilter('personaAuthenticationFilter', SecurityFilterPosition.SECURITY_CONTEXT_FILTER.order + 10)
        loadPersonOntology()
        bardCacheUtilsService.refreshDueToNonDictionaryEntry()
    }

	def destroy = {
	}

    void loadPersonOntology(){
        try {
            externalOntologyFactory.getCreators().add(new PersonCreator())
        } catch (Exception ee) {
            log.error(ee,ee)
        }
    }

}
