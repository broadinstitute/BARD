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

package maas

import bard.db.experiment.Experiment
import org.springframework.transaction.support.DefaultTransactionStatus
import org.junit.Ignore

/**
 * Created with IntelliJ IDEA.
 * User: xiaorong
 * Date: 3/2/13
 * Time: 3:15 PM
 * To change this template use File | Settings | File Templates.
 */
class ExperimentHandlerServiceIntegrationTest extends GroovyTestCase {
    def experimentHandlerService
    def projectExperimentStageHandlerService

    @Ignore
    void testLoadExperimentsContext() {
        File file = new File("data/maas/maasDataset1")
        def inputFiles = [file]
        experimentHandlerService.loadExperimentsContext("xiaorong", inputFiles)
    }

    @Ignore
    void testLoadProjectsContextFrom(){
        final List<Long> mustLoadAids = MustLoadAid.mustLoadedAids('test/exampleData/maas/most_recent_probe_aids.csv')
        def dirs = ['test/exampleData/maas/what_we_should_load']
        experimentHandlerService.handle('xiaorong', dirs, mustLoadAids)
    }

    void testLoad() {
        final List<Long> mustLoadAids = MustLoadAid.mustLoadedAids('data/maas/maasDataset2/aids.csv')
        def dirs = ['data/maas/maasDataset2']
            experimentHandlerService.handle('xiaorong', dirs, mustLoadAids)
    }

    void testStage() {
        final List<Long> mustLoadAids = MustLoadAid.mustLoadedAids('data/maas/maasDataset2/aids.csv')
        def dirs = ['data/maas/maasDataset2']
        projectExperimentStageHandlerService.handle('xiaorong', dirs, mustLoadAids)
    }
}
