import bard.db.dictionary.OntologyDataAccessService

class BootStrap {
     OntologyDataAccessService ontologyDataAccessService
	def init = { servletContext ->
       ontologyDataAccessService.computeTrees(false)
	}
	def destroy = {
	}

}
