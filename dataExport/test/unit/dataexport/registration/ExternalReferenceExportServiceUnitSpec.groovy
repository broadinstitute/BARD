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

import bard.db.experiment.Experiment
import bard.db.project.Project
import bard.db.registration.ExternalReference
import bard.db.registration.ExternalSystem
import common.tests.XmlTestAssertions
import common.tests.XmlTestSamples
import exceptions.NotFoundException
import grails.buildtestdata.mixin.Build
import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import groovy.xml.MarkupBuilder
import org.codehaus.groovy.grails.web.mapping.LinkGenerator
import spock.lang.Specification
import spock.lang.Unroll

/**
 * Created with IntelliJ IDEA.
 * User: jasiedu
 * Date: 6/19/12
 * Time: 12:52 PM
 * To change this template use File | Settings | File Templates.
 */
@TestFor(ExternalReferenceExportService)
@Build([Experiment, ExternalReference, ExternalSystem, Project, Experiment])
@Mock([Experiment, ExternalReference, ExternalSystem, Project, Experiment])
@Unroll
class ExternalReferenceExportServiceUnitSpec extends Specification {
    Writer writer
    MarkupBuilder markupBuilder

    ExternalReferenceExportService externalReferenceExportService

    void setup() {
        LinkGenerator grailsLinkGenerator = Mock(LinkGenerator.class)
        final MediaTypesDTO mediaTypesDTO =
            new MediaTypesDTO(externalReferenceMediaType: "xml",
                    externalReferencesMediaType: "xml",
                    externalSystemMediaType: "xml",
                    externalSystemsMediaType: "xml",
                    assayMediaType: "xml")
        this.service.mediaTypesDTO = mediaTypesDTO
        this.service.grailsLinkGenerator = grailsLinkGenerator
        this.externalReferenceExportService = this.service
        this.writer = new StringWriter()
        this.markupBuilder = new MarkupBuilder(writer)
    }

    void "test generate ExternalReference with a non-existing Id"() {
        ExternalReference.metaClass.static.get = { id -> null }
        when: "We attempt to generate an External Reference"
        this.externalReferenceExportService.generateExternalReference(this.markupBuilder, new Long("2"))
        then: "An exception should be thrown"
        thrown(NotFoundException)
    }

    void "test generate ExternalSystem with a non-existing Id"() {
        ExternalSystem.metaClass.static.get = { id -> null }
        when: "We attempt to generate an External System"
        this.externalReferenceExportService.generateExternalSystem(this.markupBuilder, new Long("2"))
        then: "An exception should be thrown"
        thrown(NotFoundException)
    }

    void "test generate System with Experiment"() {
        given: "An ExternalSystem"
        ExternalSystem externalSystem = ExternalSystem.build()
        when: "We attempt to generate an ExternalSystem XML document"
        this.externalReferenceExportService.generateExternalSystem(this.markupBuilder, externalSystem.id)
        then: "A valid xml document is generated and is similar to the expected document"
        XmlTestAssertions.assertResults(results, this.writer.toString())
        where:
        label                                | results
        "External Reference with Experiment" | XmlTestSamples.EXTERNAL_SYSTEM

    }

    void "test generate ExternalReference with Experiment"() {
        given: "An ExternalReference"
        ExternalReference externalReference = ExternalReference.build(experiment: Experiment.build())
        when: "We attempt to generate an ExternalReference XML document"
        this.externalReferenceExportService.generateExternalReference(this.markupBuilder, externalReference.id)
        then: "A valid xml document is generated and is similar to the expected document"
        XmlTestAssertions.assertResults(results, this.writer.toString())
        where:
        label                                | results
        "External Reference with Experiment" | XmlTestSamples.EXTERNAL_REFERENCE

    }

    void "test generate ExternalReference with Project"() {
        given: "An ExternalReference"
        ExternalReference externalReference = ExternalReference.build(project: Project.build())
        when: "We attempt to generate an ExternalReference XML document"
        this.externalReferenceExportService.generateExternalReference(this.markupBuilder, externalReference.id)
        then: "A valid xml document is generated and is similar to the expected document"
        XmlTestAssertions.assertResults(results, this.writer.toString())
        where:
        label                             | results
        "External Reference with Project" | XmlTestSamples.EXTERNAL_REFERENCE

    }


    void "test generate ExternalReferences"() {
        given: "An ExternalReference"
        [ExternalReference.build(project: Project.build())]
        when: "We attempt to generate an ExternalReferences XML document"
        this.externalReferenceExportService.generateExternalReferences(this.markupBuilder)
        then: "A valid xml document is generated and is similar to the expected document"
        XmlTestAssertions.assertResults(results, this.writer.toString())
        where:
        label                             | results
        "External Reference with Project" | XmlTestSamples.EXTERNAL_REFERENCES

    }

    void "test generate ExternalSystems"() {
        given: "An ExternalReference"
        [ExternalSystem.build()]
        when: "We attempt to generate an ExternalSystems XML document"
        this.externalReferenceExportService.generateExternalSystems(this.markupBuilder)
        then: "A valid xml document is generated and is similar to the expected document"
        XmlTestAssertions.assertResults(results, this.writer.toString())
        where:
        label                             | results
        "External Reference with Project" | XmlTestSamples.EXTERNAL_SYSTEMS

    }
}
