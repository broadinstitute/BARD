package bard.validation.extext

import bard.validation.ext.ExternalOntologyAPI
import bard.validation.ext.ExternalOntologyCreator
import bard.validation.ext.ExternalOntologyException

// to see why the package name is odd, see
// http://jira.grails.org/browse/GRAILS-9016

class PersonCreator implements ExternalOntologyCreator {
    static URI PERSON_URI = new URI("http://www.bard.nih.gov/person#")

    @Override
    public ExternalOntologyAPI create(URI uri, Properties props) throws ExternalOntologyException {
        if(PERSON_URI.equals(uri))
            try {
            return new ExternalOntologyPerson();
            } catch(Exception ex) {
                log.error("failed to create ExternalOntologyPerson", ex)
                throw new RuntimeException(ex)
            }
        else
            return null
    }
}
