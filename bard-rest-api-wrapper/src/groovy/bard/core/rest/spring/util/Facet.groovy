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

package bard.core.rest.spring.util

import bard.core.DataSource
import bard.core.IntValue
import bard.core.Value
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Created with IntelliJ IDEA.
 * User: jasiedu
 * Date: 11/23/12
 * Time: 11:29 PM
 * To change this template use File | Settings | File Templates.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Facet extends JsonUtil {

    @JsonProperty("facetName")
    private String facetName;
    @JsonProperty("counts")
    private Counts counts;

    public static final Map<String, FacetToValueTranslationDTO> FACET_TO_VALUE_TRANSLATION_MAP = [
            'assay_component_role': new FacetToValueTranslationDTO(label: 'assay_component_role', displayOrder: 0), //hiding 'assay_component_role'
            'assay_format': new FacetToValueTranslationDTO(label: 'assay_format', displayOrder: 1), //display 'assay_format' at the top
            'assay_type': new FacetToValueTranslationDTO(label: 'assay_type', displayOrder: 2), //display 'assay_type' 2nd
            'detection_method_type': new FacetToValueTranslationDTO(label: 'detection_method_type', displayOrder: 3), //display 'detection_method_type' 3rd
            'target_name': new FacetToValueTranslationDTO(label: 'target_name', displayOrder: 0), //hide 'target_name'
            'kegg_disease_cat': new FacetToValueTranslationDTO(label: 'kegg_disease_category', displayOrder: 4), //rename 'kegg_disease_cat' to 'kegg_disease_category'
            'biology': new FacetToValueTranslationDTO(label: 'biology', displayOrder: 0), //hide 'biology'
            'target_name_process': new FacetToValueTranslationDTO(label: 'biological_process', displayOrder: 5), //rename 'target_name_process' to 'biological process'
            'target_name_protein': new FacetToValueTranslationDTO(label: 'target_protein', displayOrder: 6), //rename 'target_name_protein' to 'target protein'
            'target_name_gene': new FacetToValueTranslationDTO(label: 'target_gene', displayOrder: 7), //rename 'target_name_gene' to 'target_gene'
            'class_name': new FacetToValueTranslationDTO(label: 'panther_db_protein_class', displayOrder: 8) //rename 'target_name_gene' to 'target_gene'
    ]

    public static final Map<String, String> VALUE_TO_FACET_TRANSLATION_MAP = [
            'kegg_disease_category': 'kegg_disease_cat',
            'biological_process': 'target_name_process',
            'target_protein': 'target_name_protein',
            'target_gene': 'target_name_gene',
            'panther_db_protein_class': 'class_name'
    ]

    @JsonProperty("counts")
    public Counts getCounts() {
        return counts;
    }

    @JsonProperty("counts")
    public void setCounts(Counts counts) {
        this.counts = counts;
    }

    @JsonProperty("facetName")
    public String getFacetName() {
        return facetName;
    }

    @JsonProperty("facetName")
    public void setFacetName(String facetName) {
        this.facetName = facetName;
    }


    public Value toValue() {
        final Value facet = new Value(DataSource.DEFAULT, this.facetName);
        final Counts counts = this.getCounts()
        final Map<String, Object> additionalProperties = counts.getAdditionalProperties()
        boolean hasAtleastOneValue = false//We will ignore empty facets
        for (String key : additionalProperties.keySet()) {
            final Object facetCount = additionalProperties.get(key)
            if (facetCount) {
                if (facetCount.toString().isInteger()) {
                    new IntValue(facet, key, (Integer) facetCount);
                    hasAtleastOneValue = true
                }
            }
        }
        if (hasAtleastOneValue) {
            return facet
        }
        return null
    }

    public Value toValueWithTranslation() {
        final Value facet = this.toValue()
        //Run the facet name through the mapping/translation table
        FacetToValueTranslationDTO facetToValueTranslationDTO = FACET_TO_VALUE_TRANSLATION_MAP[facet?.id]
        if (facetToValueTranslationDTO) {
            facet.id = facetToValueTranslationDTO.label
            new IntValue(facet, 'displayOrder', facetToValueTranslationDTO.displayOrder) //add a new IntValue child to facet's children to describe the display-ordering of this facet group in the UI.
        }

        return facet
    }
}

public class FacetToValueTranslationDTO {
    String label
    Integer displayOrder
}
