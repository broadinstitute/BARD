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

package dataexport.experiment

import bard.db.project.Project
import common.tests.XmlTestAssertions
import dataexport.registration.BardHttpResponse
import dataexport.util.ResetSequenceUtil
import exceptions.NotFoundException
import grails.buildtestdata.TestDataConfigurationHolder
import grails.plugin.spock.IntegrationSpec
import groovy.xml.MarkupBuilder
import org.custommonkey.xmlunit.XMLAssert
import org.springframework.core.io.FileSystemResource
import org.springframework.core.io.Resource
import spock.lang.Unroll

import javax.sql.DataSource

import static bard.db.enums.ReadyForExtraction.COMPLETE
import static bard.db.enums.ReadyForExtraction.READY
import static javax.servlet.http.HttpServletResponse.*

@Unroll
class ProjectExportServiceIntegrationSpec extends IntegrationSpec {
    ProjectExportService projectExportService
    Writer writer
    MarkupBuilder markupBuilder
    DataSource dataSource
    ResetSequenceUtil resetSequenceUtil
    def grailsApplication
    Resource schemaResource = new FileSystemResource(new File("web-app/schemas/projectSchema.xsd"))

    void setup() {
        this.writer = new StringWriter()
        this.markupBuilder = new MarkupBuilder(this.writer)

        TestDataConfigurationHolder.reset()
        resetSequenceUtil = new ResetSequenceUtil(dataSource)
        ['PROJECT_ID_SEQ'].each {
            this.resetSequenceUtil.resetSequence(it)
        }
    }

    void tearDown() {
        // Tear down logic here
    }

    void "test update Not Found Status"() {
        given: "Given a non-existing Project"
        when: "We call the project service to update this project"
        this.projectExportService.update(new Long(100000), 0, READY.COMPLETE)

        then: "An exception is thrown, indicating that the project does not exist"
        thrown(NotFoundException)
    }

    void "test update #label"() {
        given: "Given a Project with id #id and version #version"
        Project.build(readyForExtraction: initialReadyForExtraction,capPermissionService:null)

        when: "We call the project service to update this assay"
        final BardHttpResponse bardHttpResponse = this.projectExportService.update(projectId, version, READY.COMPLETE)

        then: "An ETag of #expectedETag is returned together with an HTTP Status of #expectedStatusCode"
        assert bardHttpResponse
        assert bardHttpResponse.ETag == expectedETag
        assert bardHttpResponse.httpResponseCode == expectedStatusCode
        assert Project.get(projectId).readyForExtraction == expectedReadyForExtraction

        where:
        label                                             | expectedStatusCode     | expectedETag | projectId | version | initialReadyForExtraction | expectedReadyForExtraction
        "Return OK and ETag 1"                            | SC_OK                  | 1            | 1         | 0       | READY                     | COMPLETE
        "Return CONFLICT and ETag 0"                      | SC_CONFLICT            | 0            | 1         | -1      | READY                     | READY
        "Return PRECONDITION_FAILED and ETag 0"           | SC_PRECONDITION_FAILED | 0            | 1         | 2       | READY                     | READY
        "Return OK and ETag 0, Already completed Project" | SC_OK                  | 0            | 1         | 0       | COMPLETE                  | COMPLETE
    }

    void "test generate and validate Project "() {
        given: "Given a Project"
        final Project project = Project.build(capPermissionService:null)

        when: "A service call is made to generate the project"
        this.projectExportService.generateProject(this.markupBuilder, project.id)

        then: "An XML is generated that conforms to the expected XML"
        XmlTestAssertions.validate(schemaResource, this.writer.toString())
    }

    void "test generate and validate Project given an id #label"() {
        given: "Given a Project"
        final Project project = Project.build(capPermissionService:null)

        when: "A service call is made to generate the project"
        this.projectExportService.generateProject(this.markupBuilder, project.id)

        then: "An XML is generated that conforms to the expected XML"
        XmlTestAssertions.validate(schemaResource, this.writer.toString())
    }

    void "test generate and validate Projects"() {
        given: "Given there is at least one project ready for extraction"
        Project project = Project.build(readyForExtraction: READY,capPermissionService:null)
        project.save(flush: true)

        when: "A service call is made to generate a list of projects ready to be extracted"
        this.projectExportService.generateProjects(this.markupBuilder)

        then: "An XML is generated that conforms to the expected XML"
        XMLAssert.assertXpathEvaluatesTo("1", "//projects/@count", this.writer.toString());

    }
}
