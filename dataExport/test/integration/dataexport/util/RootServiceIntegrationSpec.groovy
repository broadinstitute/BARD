package dataexport.util

import common.tests.XmlTestAssertions
import common.tests.XmlTestSamples
import grails.plugin.spock.IntegrationSpec
import groovy.xml.MarkupBuilder
import org.custommonkey.xmlunit.XMLAssert
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

        println(this.writer.toString())
        XmlTestAssertions.assertResults(results, writer.toString())

        XMLAssert.assertXpathEvaluatesTo("6", "count(//link)", writer.toString());
        XMLAssert.assertXpathEvaluatesTo("1", "count(//link[@type='application/vnd.bard.cap+xml;type=assays'])", writer.toString())
        XMLAssert.assertXpathEvaluatesTo("1", "count(//link[@type='application/vnd.bard.cap+xml;type=dictionary'])", writer.toString())
        XMLAssert.assertXpathEvaluatesTo("1", "count(//link[@type='application/vnd.bard.cap+xml;type=projects'])", writer.toString())
        XMLAssert.assertXpathEvaluatesTo("1", "count(//link[@type='application/vnd.bard.cap+xml;type=experiments'])", writer.toString())
        XMLAssert.assertXpathEvaluatesTo("1", "count(//link[@type='application/vnd.bard.cap+xml;type=externalReferences'])", writer.toString())
        XMLAssert.assertXpathEvaluatesTo("1", "count(//link[@type='application/vnd.bard.cap+xml;type=externalSystems'])", writer.toString())

        where:
        label                 | results
        "Dictionary Root Url" | XmlTestSamples.BARD_DATA_EXPORT
    }
}
