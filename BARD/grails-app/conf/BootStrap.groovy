import bard.db.dictionary.OntologyDataAccessService
import bard.validation.ext.ExternalOntologyFactory
import bard.validation.ext.ExternalOntologyPerson

class BootStrap {
    OntologyDataAccessService ontologyDataAccessService
    ExternalOntologyFactory externalOntologyFactory
    def init = { servletContext ->
        loadPersonOntology()
        computeTrees()
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
    def destroy = {
    }

}
