package dataexport.experiment

import bard.db.dictionary.Element
import common.tests.XmlTestAssertions
import common.tests.XmlTestSamples
import dataexport.registration.MediaTypesDTO
import exceptions.NotFoundException
import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import groovy.sql.Sql
import groovy.xml.MarkupBuilder
import groovy.xml.StaxBuilder
import org.codehaus.groovy.grails.web.mapping.LinkGenerator
import org.custommonkey.xmlunit.XMLAssert
import spock.lang.Specification
import spock.lang.Unroll

import javax.sql.DataSource
import javax.xml.stream.XMLOutputFactory

import bard.db.experiment.*

/**
 * Created with IntelliJ IDEA.
 * User: jasiedu
 * Date: 6/19/12
 * Time: 12:52 PM
 * To change this template use File | Settings | File Templates.
 */
@TestFor(ResultExportService)
@Mock([Experiment, Result])
@Unroll
class ResultExportServiceUnitSpec extends Specification {
    Writer writer
    StaxBuilder staxBuilder

    ResultExportService resultExportService
    DataSource dataSource = Mock(DataSource.class)
    LinkGenerator grailsLinkGenerator = Mock(LinkGenerator.class)

    def cleanup() {
        Sql.metaClass = null


    }

    void setup() {
        final MediaTypesDTO mediaTypesDTO =
            new MediaTypesDTO(
                    experimentsMediaType: "experimentsMediaType",
                    experimentMediaType: "experimentMediaType",
                    resultsMediaType: "resultsMediaType",
                    resultMediaType: "resultMediaType",
                    resultTypeMediaType: "resultTypeMediaType",
                    elementMediaType: "elementMediaType",
                    projectMediaType: "projectMediaType",
                    stageMediaType: "stageMediaType",
                    assayMediaType: "assayMediaType"
            )
        final XMLOutputFactory factory = XMLOutputFactory.newInstance()
        this.writer = new StringWriter()
        this.staxBuilder = new StaxBuilder(factory.createXMLStreamWriter(writer))
        this.resultExportService = this.service
        this.resultExportService.mediaTypes = mediaTypesDTO
        this.resultExportService.grailsLinkGenerator = grailsLinkGenerator
        this.resultExportService.dataSource = dataSource
    }


    void "test Generate result"() {
        given: "A Result"
        Writer writer1 = new StringWriter()
        MarkupBuilder markupBuilder = new MarkupBuilder(writer1)
        Set<ResultContextItem> resultContextItems = [new ResultContextItem(
                qualifier: "%", valueDisplay: "20 %",
                valueNum: 2.0, valueMin: 1.0,
                valueMax: 3.0)] as Set<ResultContextItem>
        final Result result = new Result(valueDisplay: "20 um",
                valueNum: 20,
                valueMin: 10,
                valueMax: 30,
                substance: new Substance(id: 2),
                experiment: new Experiment(id: 20),
                resultStatus: "Approved",
                resultType: new Element(id: 2, label: "Stuff"),
                qualifier: "=",
                readyForExtraction: 'Ready', resultContextItems: resultContextItems)

        when: "We attempt to serialize the Result to XML"
        Sql.metaClass.eachRow = { String query, Closure c ->
            [200]

        }
        this.resultExportService.generateResult(markupBuilder, result)

        then: "We obtain the expected XML object"
        XMLAssert.assertXpathEvaluatesTo("3", "count(//link)", writer1.toString());
        XMLAssert.assertXpathEvaluatesTo("1", "count(//link[@type='experimentMediaType'])", writer1.toString())
        XMLAssert.assertXpathEvaluatesTo("1", "count(//link[@type='resultMediaType'])", writer1.toString())
        XMLAssert.assertXpathEvaluatesTo("1", "count(//resultType)", writer1.toString())
        XMLAssert.assertXpathEvaluatesTo("1", "count(//resultContextItems)", writer1.toString())
        XMLAssert.assertXpathEvaluatesTo("1", "count(//resultContextItem)", writer1.toString())
        XMLAssert.assertXpathEvaluatesTo("1", "count(//link[@type='elementMediaType'])", writer1.toString())
        XMLAssert.assertXpathEvaluatesTo("1", "count(//result)", writer1.toString())
    }

    void "test Generate result links"() {
        given: "A Result with an experiment"
        final Result result = new Result(experiment: new Experiment())

        when: "We attempt to generate external links for the results"
        this.staxBuilder.links() {
            this.resultExportService.generateResultLinks(this.staxBuilder, result)
        }
        then: "We obtain the expected XML object"
        XmlTestAssertions.assertResults("<links><link rel=\"up\" title=\"Experiment\" type=\"experimentMediaType\" href=\"null\"></link><link rel=\"edit\" type=\"resultMediaType\" href=\"null\"></link></links>", this.writer.toString())
    }

    void "test Generate Results Not Found Exception"() {
        given:
        Result.metaClass.static.Result.findAllByExperimentAndReadyForExtraction = {exp, ready -> [] }
        when: "We attempt to generate results"
        this.resultExportService.generateResults(this.staxBuilder, new Long("2"), 0)
        then: "An exception should be thrown"
        thrown(NotFoundException)
    }

    void "test Generate Result Not Found Exception"() {
        given:
        Writer writer1 = new StringWriter()
        MarkupBuilder markupBuilder = new MarkupBuilder(writer1)
        Result.metaClass.static.get = {id -> null }
        when: "We attempt to generate results"
        this.resultExportService.generateResult(markupBuilder, new Long("2"))
        then: "An exception should be thrown"
        thrown(NotFoundException)
    }

    void "test generate Result Context Items #label"() {
        given: "A Set of Result Context Items"
        when: "We call the service method to generate the XML representation of the Items"
        this.resultExportService.generateResultContextItems(this.staxBuilder, resultContextItems)
        then: "We get back a valid ResultContextItems"
        XmlTestAssertions.assertResults(results, this.writer.toString())
        where:
        label                                      | resultContextItems                         | results
        "Full Document"                            | [new ResultContextItem(
                attributeElement: new Element(label: "attrribute"),
                qualifier: "%", valueDisplay: "20 %",
                valueNum: 2.0, valueMin: 1.0,
                valueMax: 3.0,
                valueElement: new Element(label: "valueControlled"))] as Set<ResultContextItem> | XmlTestSamples.RESULT_CONTEXT_ITEMS_UNIT
        "No Experiment/attribute/valueControlled " | [new ResultContextItem(
                qualifier: "%", valueDisplay: "20 %",
                valueNum: 2.0, valueMin: 1.0,
                valueMax: 3.0)] as Set<ResultContextItem>                                       | XmlTestSamples.RESULT_CONTEXT_ITEMS_UNIT_NO_CHILD_ELEMENTS

    }

    void "test generate Attributes For Result Context Item #label"() {
        given: "A Result Context Item"
        when: "We call the service method to generate Attributes"
        final Map<String, String> resultContextItemAttributes =
            this.resultExportService.generateAttributesForRunContextItem(resultContextItem,"resultContextItemId")
        then: "The generated map is equal to the expected map"
        resultContextItemAttributes == results
        where:
        label                                      | resultContextItem  | results
        "Full Document"                            | new ResultContextItem(
                attributeElement: new Element(label: "attrribute"),
                qualifier: "%", valueDisplay: "20 %",
                valueNum: 2.0, valueMin: 1.0,
                valueMax: 3.0,
                valueElement: new Element(label: "valueControlled")) | [resultContextItemId: null, qualifier: '%', valueDisplay: '20 %', valueNum: '2.0', valueMin: '1.0', valueMax: '3.0']
        "No Experiment/attribute/valueControlled " | new ResultContextItem(
                qualifier: "%", valueDisplay: "20 %",
                valueNum: 2.0, valueMin: 1.0,
                valueMax: 3.0)                                          | [resultContextItemId: null, qualifier: '%', valueDisplay: '20 %', valueNum: '2.0', valueMin: '1.0', valueMax: '3.0']

    }

    void "test generate Result Context Item #label"() {
        given: "A Result Context Item"
        when: "We call the service method to generate the XML representation"
        this.resultExportService.generateRunContextItem(this.staxBuilder, resultContextItem)
        then: "The generated XML is the similar to the expected XML"
        XmlTestAssertions.assertResults(results, this.writer.toString())
        where:
        label                                      | resultContextItem  | results
        "Full Document"                            | new ResultContextItem(
                attributeElement: new Element(label: "attrribute"),
                qualifier: "%", valueDisplay: "20 %",
                valueNum: 2.0, valueMin: 1.0,
                valueMax: 3.0,
                valueElement: new Element(label: "valueControlled")) | XmlTestSamples.RESULT_CONTEXT_ITEM_UNIT
        "No Experiment/attribute/valueControlled " | new ResultContextItem(
                qualifier: "%", valueDisplay: "20 %",
                valueNum: 2.0, valueMin: 1.0,
                valueMax: 3.0)                                          | XmlTestSamples.RESULT_CONTEXT_ITEM_UNIT_NO_CHILD_ELEMENTS

    }

    void "test generate hierarchy"() {
        given: "A ResultHierarchy object, Serialize it to XML"
        ResultHierarchy resultHierarchy = new ResultHierarchy(hierarchyType: HierarchyType.Derives, parentResult: new Result(id: 122))

        when:
        resultExportService.generateResultHierarchy(this.staxBuilder, resultHierarchy)
        then:
        XmlTestAssertions.assertResults("<resultHierarchy parentResultId=\"null\" hierarchyType=\"Derives\"></resultHierarchy>", this.writer.toString())
    }

    void "test generate hierarchies"() {
        given: "A ResultHierarchy object, Serialize it to XML"
        final Set<ResultHierarchy> resultHierarchies =
            [new ResultHierarchy(hierarchyType: HierarchyType.Derives, parentResult: new Result(id: 122)),
                    new ResultHierarchy(hierarchyType: HierarchyType.Child, parentResult: new Result(id: 1228))] as Set<ResultHierarchy>
        when:
        resultExportService.generateResultHierarchies(this.staxBuilder, resultHierarchies)
        then:
        XmlTestAssertions.assertResults("<resultHierarchies><resultHierarchy parentResultId=\"null\" hierarchyType=\"Derives\"></resultHierarchy><resultHierarchy parentResultId=\"null\" hierarchyType=\"Child\"></resultHierarchy></resultHierarchies>", this.writer.toString())
    }

}
