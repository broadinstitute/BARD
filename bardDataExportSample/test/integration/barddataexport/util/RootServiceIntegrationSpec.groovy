package barddataexport.util

import common.tests.XmlTestAssertions
import common.tests.XmlTestSamples
import grails.plugin.spock.IntegrationSpec
import groovy.xml.MarkupBuilder
import spock.lang.Unroll

@Unroll
class RootServiceIntegrationSpec extends IntegrationSpec {
    RootService rootService
    Writer writer
    MarkupBuilder markupBuilder


    void setup() {
        this.writer = new StringWriter()
        this.markupBuilder = new MarkupBuilder(this.writer)
    }


    void "test generate root links"() {
        when:
        rootService.generateRootElement(this.markupBuilder)
        then:
        XmlTestAssertions.assertResults(results, writer.toString())
        where:
        label                 | results
        "Dictionary Root Url" | XmlTestSamples.BARD_DATA_EXPORT
    }
}
