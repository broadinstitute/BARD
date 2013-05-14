import bard.db.dictionary.OntologyDataAccessService
import org.springframework.web.context.support.WebApplicationContextUtils
import bard.validation.ext.ExternalOntologyPerson
import bard.validation.ext.ExternalOntologyFactory

class BootStrap {
     OntologyDataAccessService ontologyDataAccessService
	 ExternalOntologyFactory externalOntologyFactory
	def init = { servletContext ->
		externalOntologyFactory.getCreators().add(new ExternalOntologyPerson.PersonCreator())
		ontologyDataAccessService.computeTrees(false)
	}
	def destroy = {
	}

}
