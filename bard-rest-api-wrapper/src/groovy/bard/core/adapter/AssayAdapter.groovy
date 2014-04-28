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
import bard.core.interfaces.*
import bard.core.rest.spring.assays.*
import bard.core.rest.spring.biology.BiologyEntity
import bard.core.rest.spring.util.Document
import bard.core.rest.spring.util.NameDescription
import bard.core.util.MatchedTermsToHumanReadableLabelsMapper

public class AssayAdapter implements AssayAdapterInterface {
    final AbstractAssay assay
    final Double score
    final NameDescription matchingField
    final BardAnnotation annotations;


    public AssayAdapter(final AbstractAssay assay, final Double score = 0, final NameDescription nameDescription = null, final BardAnnotation annotations = null) {
        this.assay = assay
        this.score = score
        this.matchingField = nameDescription
        this.annotations = annotations
    }
    @Override
    String getAssayStatus(){
        return this.assay.getAssayStatus();
    }
    @Override
    String getDesignedBy(){
        return this.assay.getDesignedBy();
    }
    @Override
    String getAssayTypeString(){
        return this.assay.getAssayType();
    }
    @Override
    String getHighlight() {
        String matchFieldName = getMatchingField()?.getName()
        if (matchFieldName) {
            matchFieldName = MatchedTermsToHumanReadableLabelsMapper.matchTermsToHumanReadableLabels(matchFieldName)
            //TODO: Talk to Steve about formatting
            return "Matched Field: " + matchFieldName
        }
        return ""

    }

    Double getScore() {
        return this.score
    }

    NameDescription getMatchingField() {
        return this.matchingField
    }

    public String getName() {
        return assay.name
    }

    public String getTitle() {
        return assay.title
    }

    public Long getCapAssayId() {
        return assay.getCapAssayId()
    }

    public Long getBardAssayId() {
        return assay.getBardAssayId()
    }



    public AssayType getType() {
        int assayType = this.assay.getType()
        return AssayType.valueOf(assayType)
    }

    public AssayRole getRole() {
        return AssayRole.valueOf(assay.classification.intValue());
    }

    public AssayCategory getCategory() {
        return AssayCategory.valueOf(assay.category.intValue())
    }

    public String getLastUpdatedDate() {
        return assay.updated
    }

    public String getDescription() {
        return assay.description
    }

    public Long getId() {
        return assay.getId()
    }

    public String getProtocol() {
        return assay.protocol
    }

    public String getComments() {
        return assay.getComments()
    }

    public Long getAid() {
        return assay.aid
    }

    public String getSource() {
        return assay.source
    }

    public BardAnnotation getAnnotations() {
        return annotations
    }

    public MinimumAnnotation getMinimumAnnotation() {
        return assay.getMinimumAnnotation()
    }

    public List<String> getKeggDiseaseNames() {
        return assay.getKegg_disease_names()
    }
    //TODO Perhaps convert to values
    public List<String> getKeggDiseaseCategories() {
        return assay.getKegg_disease_cat()
    }

    //TODO: Change to values?
    public Map<String, List<String>> getKeggAnnotations() {
        Map<String, List<String>> annos = new HashMap<String, List<String>>()
        annos.put(EntityNamedSources.KEGGDiseaseCategoryAnnotationSource, assay.getKegg_disease_cat())
        annos.put(EntityNamedSources.KEGGDiseaseNameAnnotationSource, assay.getKegg_disease_names())
        return annos;
    }

    public List<Document> getDocuments() {
        if (assay instanceof ExpandedAssay) {
            ExpandedAssay expandedAssay = (ExpandedAssay) assay
            return expandedAssay.getDocuments()
        }
        return []
    }

    public List<BiologyEntity> getBiology() {
        if (assay instanceof ExpandedAssay) {
            ExpandedAssay expandedAssay = (ExpandedAssay) assay
            return expandedAssay.getBiology()
        }
        return []
    }

    public List<String> getBiologyIds() {
        List<String> biologyIds = []
        if (assay instanceof ExpandedAssay) {
            ExpandedAssay expandedAssay = (ExpandedAssay) assay
            expandedAssay.biology.collect { BiologyEntity biology ->
                if (biology.serial) {
                  biologyIds.add(biology.serial)
                }
            }
        }
        else if (assay instanceof Assay) {
            Assay assay1 = (Assay) assay
            biologyIds.addAll(assay1.biologyIds)
        }
        return biologyIds
    }

    public List<String> getDocumentIds() {
        List<String> documentIds = []
        if (assay instanceof ExpandedAssay) {
            ExpandedAssay expandedAssay = (ExpandedAssay) assay
            expandedAssay.documents.collect { Document document ->
                if (document.pubmedId) {
                    documentIds.add(document.pubmedId)
                }
            }
        }
        if (assay instanceof Assay) {
            Assay assay1 = (Assay) assay
            documentIds.addAll(assay1.documentIds)
        }
        return documentIds
    }

}
