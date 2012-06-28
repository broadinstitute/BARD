package dataexport.util

import common.tests.XmlTestAssertions
import common.tests.XmlTestSamples
import groovy.xml.MarkupBuilder
import org.codehaus.groovy.grails.web.mapping.LinkGenerator
import spock.lang.Specification
import spock.lang.Unroll
import dataexport.registration.MediaTypesDTO

/**
 *
 */
@Unroll
class RootServiceUnitSpec extends Specification {
    RootService rootService
    LinkGenerator grailsLinkGenerator
    Writer writer
    MarkupBuilder markupBuilder

    void setup() {
        grailsLinkGenerator = Mock()
        final MediaTypesDTO mediaTypesDTO = new MediaTypesDTO(dictionaryMediaType: "dictionaryMediaType", assaysMediaType: "assaysMediaType", projectsMediaType: "projectsMediaType")
        this.rootService =
            new RootService(mediaTypesDTO)
        this.rootService.grailsLinkGenerator = grailsLinkGenerator
        this.writer = new StringWriter()
        this.markupBuilder = new MarkupBuilder(writer)

    }

    void tearDown() {
        // Tear down logic here
    }

    /**
     * RootService#generateRootElement
     */
    void "test Generate Root links"() {
        when: "We generate the links for the root elements"
        this.rootService.generateRootElement(this.markupBuilder)
        then: "The expected results should match the obtained results"
        XmlTestAssertions.assertResults(results, this.writer.toString())
        where:
        label        | results
        "Root Links" | XmlTestSamples.BARD_DATA_EXPORT_UNIT
    }
}