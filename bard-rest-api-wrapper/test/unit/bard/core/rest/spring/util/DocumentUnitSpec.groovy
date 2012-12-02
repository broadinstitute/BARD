package bard.core.rest.spring.util

import com.fasterxml.jackson.databind.ObjectMapper
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

@Unroll
class DocumentUnitSpec extends Specification {
    @Shared
    ObjectMapper objectMapper = new ObjectMapper()
    final String DOCUMENT_JSON = '''
    {
       "title": "Feasibility",
       "doi": "doi",
       "abs": "For the past 30 years",
       "pubmedId": 3335022,
       "resourcePath": "/documents/3335022"
    }
   '''
    void "test serialize json to document"() {
        when:
        Document document = objectMapper.readValue(DOCUMENT_JSON, Document.class)
        then:
        assert document
        assert document.title == "Feasibility"
        assert document.doi == "doi"
        assert document.abs == "For the past 30 years"
        assert document.pubmedId==3335022
        assert document.resourcePath=="/documents/3335022"
    }

}

