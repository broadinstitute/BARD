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

package bard.db.experiment

import org.codehaus.groovy.grails.commons.GrailsApplication
import spock.lang.Specification

import static org.junit.Assert.*

import grails.test.mixin.*
import grails.test.mixin.support.*
import org.junit.*

/**
 * See the API for {@link grails.test.mixin.support.GrailsUnitTestMixin} for usage instructions
 */
@TestMixin(GrailsUnitTestMixin)
class ArchivePathServiceSpec extends Specification {

    File expectedFinalPath = new File("out/testArchivePathService/testArchivePathServiceSpec.txt")

    void testPathResolution() {
        setup:
        GrailsApplication grailsApplication = Mock(GrailsApplication)
        grailsApplication.config >> [bard: [services: [resultService: [archivePath: "out/testArchivePathService"]]]]

        if (expectedFinalPath.exists())
            expectedFinalPath.delete()

        ArchivePathService service = new ArchivePathService()
        service.grailsApplication = grailsApplication

        Experiment experiment = new Experiment()
        experiment.id = 2

        when:
        String exportPath = service.constructExportResultPath(experiment)

        then:
        exportPath ==~ /exported-results\/0\/2\/exp-2-\d+-\d+.json.gz/

        when:
        String uploadPath = service.constructUploadResultPath(experiment)

        then:
        uploadPath ==~ /uploaded-results\/0\/2\/exp-2-\d+-\d+.txt.gz/

        when:
        File fullPath = service.prepareForWriting("testArchivePathServiceSpec.txt")

        then:
        fullPath.parentFile.exists()
        fullPath.equals(expectedFinalPath)
    }
}
