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

package bard.core.adapter
import bard.core.Probe
import bard.core.interfaces.ProjectAdapterInterface
import bard.core.rest.spring.assays.BardAnnotation
import bard.core.rest.spring.biology.BiologyEntity
import bard.core.rest.spring.compounds.Compound
import bard.core.rest.spring.project.ProjectAbstract
import bard.core.rest.spring.project.ProjectExpanded
import bard.core.rest.spring.util.Document
import bard.core.rest.spring.util.NameDescription
import bard.core.util.MatchedTermsToHumanReadableLabelsMapper

public class ProjectAdapter implements ProjectAdapterInterface {
    final ProjectAbstract project
    final Double score
    final NameDescription matchingField
    final BardAnnotation annotations
    final String projectStatus

    public ProjectAdapter(ProjectAbstract project, Double score = 0, NameDescription nameDescription = null, BardAnnotation annotations = null,String projectStatus = null) {
        this.project = project
        this.score = score
        this.matchingField = nameDescription
        this.annotations = annotations
        this.projectStatus = projectStatus
    }
    @Override
    public String getExperimentType(Long experimentId){
        return this.getExperimentTypes()?.get(experimentId)
    }
    @Override
    public Map<Long, String> getExperimentTypes() {
        return this.project?.getExperimentTypes();
    }

    @Override
    String getProjectStatus() {
        return this.projectStatus
    }

    @Override
    String getHighlight() {
        String matchFieldName = getMatchingField()?.getName()
        if (matchFieldName) {
            matchFieldName = MatchedTermsToHumanReadableLabelsMapper.matchTermsToHumanReadableLabels(matchFieldName)
            return "Matched Field: " + matchFieldName
        }
        return ""

    }

    public boolean hasProbes() {
        return project?.hasProbes()
    }

    public Double getScore() {
        return score
    }

    public NameDescription getMatchingField() {
        return matchingField
    }

    public Long getId() {
        return project.bardProjectId

    }

    public String getName() {
        return project.name
    }

    public String getDescription() {
        return project.description
    }


    public List<Probe> getProbes() {
        final List<Probe> probes = new ArrayList<Probe>()
        final List<Compound> compounds = project.getProbes()
        for (Compound compound : compounds) {
            Probe probe = new Probe(compound)
            probes.add(probe)
        }
        return probes
    }


    public Integer getNumberOfExperiments() {
        return project.experimentCount.intValue()
    }

    public List<Document> getDocuments() {
        if (project instanceof ProjectExpanded) {
            return ((ProjectExpanded) project).getPublications()
        }
        return []
    }

    public List<BiologyEntity> getBiology() {
        return project.getBiology()
    }

    public BardAnnotation getAnnotations() {

        return this.annotations
    }

    public Long getCapProjectId() {
        return project.getCapProjectId()
    }
}
