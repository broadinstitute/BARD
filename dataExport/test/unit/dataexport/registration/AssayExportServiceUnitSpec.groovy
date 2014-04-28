/* Copyright (c) 2014, The Broad Institute
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
    * Redistributions of source code must retain the above copyright
      notice, this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright
      notice, this list of conditions and the following disclaimer in the
      documentation and/or other materials provided with the distribution.
    * Neither the name of The Broad Institute nor the
      names of its contributors may be used to endorse or promote products
      derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL The Broad Institute BE LIABLE FOR ANY
DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package dataexport.registration

import bard.db.registration.Assay
import bard.db.registration.AssayDocument
import common.tests.XmlTestAssertions
import common.tests.XmlTestSamples
import exceptions.NotFoundException
import grails.buildtestdata.mixin.Build
import grails.test.mixin.Mock
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
@Mock([Assay, AssayDocument])
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
        XmlTestAssertions.assertResultsWithOverrideAttributes(results, actualXml)

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

    void "test generated Assay modifiedBy #label"() {
        given: "An Experiment"
        Assay assay = Assay.build()
        assay.modifiedBy = modifiedBy

        when: "We attempt to generate an experiment XML document"
        this.assayExportService.generateAssay(this.markupBuilder, assay.id)

        then:
        String actualXml = this.writer.toString()
        def resultXml = new XmlSlurper().parseText(actualXml)
        resultXml.@modifiedBy == expectedModifiedBy

        where:
        label           | modifiedBy    | expectedModifiedBy
        "without email" | 'foo'         | 'foo'
        "with email"    | 'foo@foo.com' | 'foo'

    }
}
