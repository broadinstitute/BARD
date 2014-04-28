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

package bard.db.registration

import bard.db.BardIntegrationSpec
import bard.db.experiment.Experiment
import registration.AssayService
import spock.lang.Unroll

/**
 * Created with IntelliJ IDEA.
 * User: ddurkin
 * Date: 8/21/12
 * Time: 9:18 AM
 * To change this template use File | Settings | File Templates.
 */
@Unroll
class AssayServiceIntegrationSpec extends BardIntegrationSpec {

    AssayService assayService

    void "test findByPubChemAid with fixtures #label"() {

        given:
        final Assay assay1 = Assay.build(assayName: 'assay1', capPermissionService:null)
        2.times { i ->
            i++
            final String experimentsAlias = "experiment${i}"
            final Experiment experiment = Experiment.build(experimentName: experimentsAlias, assay: assay1, capPermissionService:null)
            final ExternalReference externalReference = ExternalReference.build(extAssayRef: "aid=-${i}", experiment: experiment)
        }
        final Assay assay2 = Assay.build(assayName: 'assay2', capPermissionService:null)
        final Experiment experiment3 = Experiment.build(experimentName: 'experiment3', assay: assay2, capPermissionService:null)
        final ExternalReference externalReference = ExternalReference.build(extAssayRef: 'aid=-1', experiment: experiment3)

        when:
        List<Assay> foundAssays = assayService.findByPubChemAid(aid)

        then:
        assert foundAssays*.assayName.sort() == expectedAssayNames

        where:
        label                                           | aid       | expectedAssayNames
        'find an ADID with two AIDs associated with it' | -2        | ['assay1']
        'find a non-exiting aid'                        | 123456789 | []
        'find an exiting aid associated with two ADIDs' | -1        | ['assay1', 'assay2']
    }
}
