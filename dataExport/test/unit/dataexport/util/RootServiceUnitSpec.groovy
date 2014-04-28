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

package dataexport.util

import common.tests.XmlTestAssertions
import common.tests.XmlTestSamples
import dataexport.registration.MediaTypesDTO
import groovy.xml.MarkupBuilder
import org.codehaus.groovy.grails.web.mapping.LinkGenerator
import spock.lang.Specification
import spock.lang.Unroll

/**
 *
 */
@Unroll
class RootServiceUnitSpec extends Specification {
    RootService rootService = new RootService()
    LinkGenerator grailsLinkGenerator
    Writer writer
    MarkupBuilder markupBuilder

    void setup() {
        grailsLinkGenerator = Mock(LinkGenerator.class)
        final MediaTypesDTO mediaTypesDTO =
            new MediaTypesDTO(
                    dictionaryMediaType: "dictionaryMediaType",
                    assaysMediaType: "assaysMediaType",
                    projectsMediaType: "projectsMediaType",
                    experimentsMediaType: "experimentsMediaType",
                    externalSystemsMediaType: "externalSystems",
                    externalReferencesMediaType: "externalReferences"
            )
        this.rootService.mediaTypesDTO = mediaTypesDTO
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
