package dataexport.dictionary

import bard.db.dictionary.*
import bard.db.enums.ReadyForExtraction
import common.tests.XmlTestAssertions
import common.tests.XmlTestSamples
import dataexport.registration.BardHttpResponse
import dataexport.util.ResetSequenceUtil
import exceptions.NotFoundException
import grails.buildtestdata.TestDataConfigurationHolder
import grails.plugin.spock.IntegrationSpec
import groovy.xml.MarkupBuilder
import org.springframework.core.io.FileSystemResource
import org.springframework.core.io.Resource
import spock.lang.Unroll

import javax.sql.DataSource

import static javax.servlet.http.HttpServletResponse.*

@Unroll
class DictionaryExportServiceIntegrationSpec extends IntegrationSpec {
    DictionaryExportService dictionaryExportService

    Writer writer
    MarkupBuilder markupBuilder

    DataSource dataSource
    ResetSequenceUtil resetSequenceUtil
    def fixtureLoader
    Resource schemaResource = new FileSystemResource(new File("web-app/schemas/dictionarySchema.xsd"))

    void setup() {
        this.writer = new StringWriter()
        this.markupBuilder = new MarkupBuilder(this.writer)
        resetSequenceUtil = new ResetSequenceUtil(dataSource)

        TestDataConfigurationHolder.reset()
        this.resetSequenceUtil.resetSequence('ELEMENT_ID_SEQ')
    }

    void tearDown() {
        // Tear down logic here
    }

    void "test update Not Found Status"() {
        given: "Given a non-existing Element"
        when: "We call the dictionary service to update this element"
        this.dictionaryExportService.update(new Long(100000), 0, "Complete")

        then: "An exception is thrown, indicating that the element does not exist"
        thrown(NotFoundException)
    }

    void "test update #label"() {
        given: "Given an Element with id #id and version #version"
        Element e = Element.build(readyForExtraction: initialReadyForExtraction)

        when: "We call the dictionary service to update this project"
        final BardHttpResponse bardHttpResponse = this.dictionaryExportService.update(elementId, version, ReadyForExtraction.COMPLETE)

        then: "An ETag of #expectedETag is returned together with an HTTP Status of #expectedStatusCode"
        assert bardHttpResponse
        assert bardHttpResponse.ETag == expectedETag
        assert bardHttpResponse.httpResponseCode == expectedStatusCode
        assert Element.get(elementId).readyForExtraction == expectedReadyForExtractionVal

        where:
        label                                             | expectedStatusCode     | expectedETag | elementId   | version | initialReadyForExtraction   | expectedReadyForExtractionVal
        "Return OK and ETag 1 when status updated"        | SC_OK                  | 1            | 1           | 0       | ReadyForExtraction.READY    | ReadyForExtraction.COMPLETE
        "Return CONFLICT and ETag 0"                      | SC_CONFLICT            | new Long(0)  | new Long(1) | -1      | ReadyForExtraction.READY    | ReadyForExtraction.READY
        "Return PRECONDITION_FAILED and ETag 0"           | SC_PRECONDITION_FAILED | new Long(0)  | new Long(1) | 2       | ReadyForExtraction.READY    | ReadyForExtraction.READY
        "Return OK and ETag 0, Already completed Element" | SC_OK                  | new Long(0)  | new Long(1) | 0       | ReadyForExtraction.COMPLETE | ReadyForExtraction.COMPLETE
    }

    void "test generate Stage"() {
        given: "Given an Element with id #id and version #version"
        def fixture = fixtureLoader.build {
            stageElement(Element, label: 'IC50', description: 'Description')
            stageTree(StageTree, element: stageElement)
        }
        fixture.stageTree.save(flush: true)

        when:
        this.dictionaryExportService.generateStage(this.markupBuilder, elementId)

        then:
        XmlTestAssertions.assertResults(results, this.writer.toString())

        where:
        label   | elementId | results
        "Stage" | 1         | XmlTestSamples.STAGE
    }

    void "test generate Stage with non-existing id"() {
        when:
        this.dictionaryExportService.generateStage(this.markupBuilder, element)

        then:
        thrown(NotFoundException)

        where:
        label   | element
        "Stage" | new Long("300000000")
    }

    void "test generate ResultType"() {
        given:
        def fixture = fixtureLoader.build {
            element(Element, label: 'IC50', elementStatus: ElementStatus.Published)
            baseUnit(Element, label: 'uM')
            resultTypeTree(ResultTypeTree, element: element, baseUnit: baseUnit)
        }
        fixture.resultTypeTree.save(flush: true)

        when:
        this.dictionaryExportService.generateResultType(this.markupBuilder, elementId)

        then:
        XmlTestAssertions.assertResults(results, this.writer.toString())

        where:
        label         | elementId | results
        "Result Type" | 1         | XmlTestSamples.RESULT_TYPE
    }

    void "test generate ResultType with non existing id"() {
        when:
        this.dictionaryExportService.generateResultType(this.markupBuilder, element)

        then:
        thrown(NotFoundException)

        where:
        label         | element
        "Result Type" | new Long("3410000000")
    }

    void "test generate Element With non existing Element Id"() {
        when:
        this.dictionaryExportService.generateElement(this.markupBuilder, element)

        then:
        thrown(NotFoundException)

        where:
        label     | element
        "Element" | new Long("30000000")
    }

    void "test generate Element"() {
        given:
        Element.build(label: 'uM', elementStatus: ElementStatus.Published, readyForExtraction: ReadyForExtraction.READY)

        when:
        this.dictionaryExportService.generateElement(this.markupBuilder, elementId)

        then:
        XmlTestAssertions.assertResultsWithOverrideAttributes(results, this.writer.toString())

        where:
        label      | elementId | results
        "Elements" | 1         | XmlTestSamples.ELEMENT
    }

    void "test generate Dictionary"() {
        given:

        Element parentElement = Element.build(label: 'IC50')
        Element childElement = Element.build(label: 'log IC50')
        ElementHierarchy elementHierarchy = ElementHierarchy.build(relationshipType: 'subClassOf', parentElement: parentElement, childElement: childElement)

        ResultTypeTree.build(id: 1)
        StageTree.build(id: 1)
        AssayDescriptor.build(id: 1)
        BiologyDescriptor.build(id: 1)
        InstanceDescriptor.build(id: 1)
        LaboratoryTree.build(id: 1)
        UnitTree.build(id: 1)
        UnitConversion.build(fromUnit: Element.build(label: 'micromolar'),
                toUnit: Element.build(label: 'millimolar'),
                multiplier: 1000)

        when:
        this.dictionaryExportService.generateDictionary(this.markupBuilder)

        then:
        String actualXml = this.writer.toString()
        XmlTestAssertions.assertResultsWithOverrideAttributes(results, actualXml)
        XmlTestAssertions.validate(schemaResource, actualXml)

        where:
        label        | results
        "Dictionary" | XmlTestSamples.DICTIONARY
    }
}
