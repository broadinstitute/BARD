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

package mockServices
import bard.core.Probe
import bard.core.interfaces.ProjectAdapterInterface
import bard.core.rest.spring.assays.BardAnnotation
/**
 * Created with IntelliJ IDEA.
 * User: jasiedu
 * Date: 3/10/13
 * Time: 8:36 PM
 * To change this template use File | Settings | File Templates.
 */
class MockProjectAdapter implements ProjectAdapterInterface {
    Long id
    String name
    String description
    Integer numExperiments
    BardAnnotation annotations = null
    String projectStatus="Approved"

    @Override
    String getHighlight() {
        return "Score: 2.0, Matched Field: Name"
    }

    @Override
    Long getId() {
        return id ?: 2  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    String getName() {
        return name ?: "Project Name" //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    String getDescription() {
        return description ?: '''
                    Assay Provider: David M. Wilson, III, National Institute on Aging, NIH Screening Center PI: Austin, C.P. Screening Center: NIH Chemical Genomics Center [NCGC]
                    The apurinic/apyrimidinic endonuclease APE1 is the primary mammalian enzyme responsible for the removal of abasic (or AP) sites in DNA and functions centrally
                    in the base excision DNA repair (BER) pathway. Recent studies suggested a link between an overexpression of APE1 in many cancers and resistance of these tumor
                    cells to radio- and chemotherapy. Thus, targeting APE1 could improve the efficacy of current treatment paradigms by promoting selective sensitization or
                    protection of diseased and normal cells, respectively. This assay will summarize the probe development efforts that are currently ongoing.
                 '''
    }

    @Override
    List<Probe> getProbes() {
        return [new Probe(2, "ML18", "http://bard.org", "CCC", 1, 2),
                new Probe(28, "ML20", "http://bard.org", "CCCC", 2,2)]  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    Integer getNumberOfExperiments() {
        return numExperiments ?: 2  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    BardAnnotation getAnnotations() {
        return annotations
    }

    @Override
    String getExperimentType(Long experimentId) {
        return null  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    Map<Long, String> getExperimentTypes() {
        return null  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    String getProjectStatus() {
        return this.projectStatus
    }
}
