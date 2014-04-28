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

import bard.db.dictionary.Element
import bard.db.enums.*
import bard.db.registration.*
import common.tests.XmlTestAssertions
import common.tests.XmlTestSamples
import dataexport.util.ResetSequenceUtil
import grails.buildtestdata.TestDataConfigurationHolder
import grails.plugin.spock.IntegrationSpec
import groovy.xml.MarkupBuilder
import org.springframework.core.io.FileSystemResource
import org.springframework.core.io.Resource
import spock.lang.Unroll

import javax.sql.DataSource

@Unroll
class AssayExportHelperServiceIntegrationSpec extends IntegrationSpec {
    AssayExportHelperService assayExportHelperService
    Writer writer
    MarkupBuilder markupBuilder
    DataSource dataSource
    ResetSequenceUtil resetSequenceUtil
    Resource schemaResource = new FileSystemResource(new File("web-app/schemas/assaySchema.xsd"))

    void setup() {
        this.writer = new StringWriter()
        this.markupBuilder = new MarkupBuilder(this.writer)
        resetSequenceUtil = new ResetSequenceUtil(dataSource)

        TestDataConfigurationHolder.reset()

        ['ASSAY_ID_SEQ',
                'ASSAY_CONTEXT_ID_SEQ',
                'ASSAY_CONTEXT_ITEM_ID_SEQ',
                'ASSAY_CONTEXT_MEASURE_ID_SEQ',
                'ASSAY_DOCUMENT_ID_SEQ',
                'PANEL_ID_SEQ',
                'PANEL_ASSAY_ID_SEQ',
                'ELEMENT_ID_SEQ',
                'MEASURE_ID_SEQ'].each {
            this.resetSequenceUtil.resetSequence(it)
        }
    }

    void tearDown() {
        // Tear down logic here
    }

    void "test generate AssayContext with contextItems"() {
        given: "Given an Assay Id"
        Assay assay = Assay.build(capPermissionService: null)
        AssayContext assayContext = AssayContext.buildWithoutSave(assay: assay, contextName: 'Context for IC50', contextType: ContextType.UNCLASSIFIED)
        final Element freeTextAttribute = Element.build(label: 'software', expectedValueType: ExpectedValueType.FREE_TEXT)
        AssayContextItem.build(assayContext: assayContext, attributeElement: freeTextAttribute, attributeType: AttributeType.Fixed, valueDisplay: 'Assay Explorer')
        AssayContextItem.build(assayContext: assayContext, attributeElement: Element.build(label: 'a label', expectedValueType: ExpectedValueType.FREE_TEXT), attributeType: AttributeType.Fixed, valueDisplay: "x", valueType: ValueType.FREE_TEXT)

        when: "A service call is made to generate measure contexts for that Assay"
        this.assayExportHelperService.generateAssayContexts(this.markupBuilder, assay.assayContexts)

        then: "An XML is generated that conforms to the expected XML"
        XmlTestAssertions.assertResults(XmlTestSamples.ASSAY_CONTEXTS, this.writer.toString())
    }




    void "test generate Full Assay"() {
        given:
        Element element = Element.build(expectedValueType: ExpectedValueType.FREE_TEXT)
        Assay assay = Assay.build(capPermissionService: null, readyForExtraction: ReadyForExtraction.READY)
        AssayContext assayContext = AssayContext.build(assay: assay, contextType: ContextType.UNCLASSIFIED)
        AssayContextItem.build(assayContext: assayContext, attributeElement: element)
        AssayDocument.build(assay: assay)


        Panel panel = Panel.build()
        PanelAssay.build(assay: assay, panel: panel)
        when:
        this.assayExportHelperService.generateAssay(this.markupBuilder, assay)

        then: "An XML is generated that conforms to the expected XML"
        String actualXml = this.writer.toString()
        XmlTestAssertions.assertResultsWithOverrideAttributes(XmlTestSamples.ASSAY_FULL_DOC, actualXml)
        XmlTestAssertions.validate(schemaResource, actualXml)
    }

    void "test generate Assays readyForExtraction"() {
        given: "Given there is at least one assay ready for extraction"
        Assay.build(readyForExtraction: ReadyForExtraction.READY, capPermissionService: null)

        when: "A service call is made to generate a list of assays ready to be extracted"
        this.assayExportHelperService.generateAssays(this.markupBuilder)

        then: "An XML is generated that conforms to the expected XML"

        XmlTestAssertions.assertResults(XmlTestSamples.ASSAYS, this.writer.toString())
    }

    void "test generate AssayDocument"() {
        given:
        AssayDocument assayDocument = AssayDocument.build(documentName: 'a doc', documentType: DocumentType.DOCUMENT_TYPE_PROTOCOL, documentContent: 'content')

        when:
        this.assayExportHelperService.generateDocument(this.markupBuilder, assayDocument)

        then:
        XmlTestAssertions.assertResults(XmlTestSamples.ASSAY_DOCUMENT, this.writer.toString())


    }

}
