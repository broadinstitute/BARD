package dataexport.registration

import bard.db.registration.Assay
import bard.db.registration.AssayDocument
import common.tests.XmlTestAssertions
import common.tests.XmlTestSamples
import exceptions.NotFoundException
import grails.buildtestdata.mixin.Build
import grails.test.mixin.TestFor
import groovy.xml.MarkupBuilder
import org.codehaus.groovy.grails.web.mapping.LinkGenerator
import org.springframework.core.io.FileSystemResource
import org.springframework.core.io.Resource
import spock.lang.Specification
import spock.lang.Unroll

/**
 * Created with IntelliJ IDEA.
 * User: jasiedu
 * Date: 6/19/12
 * Time: 12:52 PM
 * To change this template use File | Settings | File Templates.
 */
@TestFor(AssayExportService)
@Build([Assay, AssayDocument])
@Unroll
class AssayExportServiceUnitSpec extends Specification {
    Writer writer
    MarkupBuilder markupBuilder

    AssayExportService assayExportService = new AssayExportService()
    AssayExportHelperService assayExportHelperService = new AssayExportHelperService()
    Resource schemaResource = new FileSystemResource(new File("web-app/schemas/assaySchema.xsd"))

    void setup() {
        LinkGenerator grailsLinkGenerator = Mock(LinkGenerator.class)
        final MediaTypesDTO mediaTypesDTO =
            new MediaTypesDTO(resultTypeMediaType: "xml", elementMediaType: "xml", assaysMediaType: "xml", assayMediaType: "xml", assayDocMediaType: "xml")

        this.assayExportService = this.service
        // TODO seems odd to be wiring in this collaborator, seems like it should be mocked instead
        this.assayExportHelperService.mediaTypesDTO = mediaTypesDTO
        this.assayExportHelperService.grailsLinkGenerator = grailsLinkGenerator
        this.assayExportService.assayExportHelperService = assayExportHelperService
        this.writer = new StringWriter()
        this.markupBuilder = new MarkupBuilder(writer)
    }

    void "test generate Assay #label"() {
        given: "An Assay"
        Assay assay = valueUnderTest.call()

        when: "We attempt to generate an Assay XML document"
        this.assayExportService.generateAssay(this.markupBuilder, assay.id)

        then: "A valid xml document is generated and is similar to the expected document"
        String actualXml = this.writer.toString()
        println("expected: $results")
        println("actualXml: $actualXml")
        XmlTestAssertions.assertResults(results, actualXml)
//        XmlTestAssertions.validate(schemaResource, actualXml)

        where:
        label                         | valueUnderTest                               | results
        "Assay with no designer name" | { Assay.build() }                            | XmlTestSamples.ASSAY_NO_DESIGNER_UNIT
        "Assay with designer name"    | { Assay.build(designedBy: 'Broad') }         | XmlTestSamples.ASSAY_WITH_DESIGNER_NAME
        "Assay with designer name"    | { def ad = AssayDocument.build(); ad.assay } | XmlTestSamples.ASSAY_WITH_DOCUMENT

    }

    void "test Generate Assay Document Not Found Exception"() {
        given:
        AssayDocument.metaClass.static.get = { id -> null }
        when: "We attempt to generate an Assay document"
        this.assayExportService.generateAssayDocument(this.markupBuilder, new Long("2"))
        then: "An exception should be thrown"
        thrown(NotFoundException)
    }

    void "test Generate Assay Not Found Exception"() {
        given:
        Assay.metaClass.static.get = { id -> null }
        when: "We attempt to generate an Assay"
        this.assayExportService.generateAssay(this.markupBuilder, new Long("2"))
        then: "An exception should be thrown"
        thrown(NotFoundException)
    }
}