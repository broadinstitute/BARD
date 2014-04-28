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

package bard.core.adapter;


import bard.core.interfaces.CompoundAdapterInterface
import bard.core.rest.spring.compounds.Compound
import bard.core.rest.spring.compounds.CompoundAnnotations
import bard.core.rest.spring.compounds.ProbeAnnotation
import bard.core.rest.spring.util.NameDescription
import bard.core.util.MatchedTermsToHumanReadableLabelsMapper
import org.apache.commons.lang3.StringUtils

public class CompoundAdapter implements CompoundAdapterInterface {
    final Compound compound
    final Double score
    final NameDescription matchingField


    public CompoundAdapter(Compound compound, Double score = 0, NameDescription matchingField = null) {
        this.compound = compound
        this.score = score
        this.matchingField = matchingField
    }
    public List<ProbeAnnotation> getProbeAnnotations(){
        return compound.getProbeAnnotations();
    }

    @Override
    ProbeAnnotation getProbeCid() {
        return compound.getProbeCid()
    }

    @Override
    ProbeAnnotation getProbe() {
        return compound.getProbe()
    }

    @Override
    ProbeAnnotation getProbeSid() {
        return compound.getProbeSid()
    }

    @Override
    public boolean hasProbeAnnotations(){
        return !compound.getProbeAnnotations().isEmpty()
    }
    public List<String> getSynonyms(){
        return this.compound.getCompoundAnnotations()?.getSynonyms() ?: []
    }
    public List<String> getRegistryNumbers(){
        return this.compound.getCompoundAnnotations()?.getRegistryNumbers()?:[]
    }
    public String getUniqueIngredientIdentifier(){
        return this.compound.compoundAnnotations?.getUniqueIngredientIdentifier()?: ""
    }
    public List<String> getMechanismOfAction(){
        return this.compound.compoundAnnotations?.getMechanismOfAction()?:[]
    }
    public List<String> getTherapeuticIndication(){
        return this.compound.compoundAnnotations?.getTherapeuticIndication()?:[]
    }
    public List<String> getPrescriptionDrugLabel(){
        return this.compound.compoundAnnotations?.getPrescriptionDrugLabel()?:[]
    }
    public List<String> getOtherAnnotationValue(String key){

        CompoundAnnotations compoundAnnotations = this.compound.compoundAnnotations
        if(compoundAnnotations){
            return compoundAnnotations.annotations.get(key)?:[]
        }
        return []
    }
    public Double getScore(){
        return this.score
    }
    public NameDescription  getMatchingField(){
        return this.matchingField
    }

    @Override
    String getHighlight() {
        String matchFieldName = getMatchingField()?.getName()
        if(matchFieldName){
            matchFieldName = MatchedTermsToHumanReadableLabelsMapper.matchTermsToHumanReadableLabels(matchFieldName)
            //TODO: Talk to Steve about formatting
            return "Matched Field: " + matchFieldName
        }
        return ""

    }

    public boolean isDrug() {
        return this.compound.compoundClass == 'Drug'
    }

    public String getProbeId() {
        return this.compound.probeId
    }

    public boolean isProbe() {
        return StringUtils.isNotBlank(this.getProbeId())
    }

    public Long getId() {
        return this.compound.cid
    }

    @Deprecated //use getId instead
    public Long getPubChemCID() {
        return this.compound.cid
    }
    /**
     *
     * @return
     */
    //use this.compound.getSmiles() instead
    public String getSmiles() {
        return this.compound.smiles
    }
    /**
     *
     * @return
     */
    @Deprecated //use this.compound.getSmiles() instead
    public String getStructureSMILES() {
        return this.compound.smiles
    }

    @Deprecated //No longer used
    public String getStructureMOL() {
        return null
    }

    @Deprecated // No longer used
    public Object getStructureNative() {
        return null
    }

    @Deprecated //No longer available in pay load
    public String formula() {
        return "";
    }

    public Double mwt() {
        return this.compound.mwt
    }

    public Double exactMass() {
        return this.compound.exactMass
    }

    public Integer hbondDonor() {
        return compound.hbondDonor
    }

    public Integer hbondAcceptor() {
        return compound.hbondAcceptor
    }

    public Integer rotatable() {
        return compound.rotatable
    }

    @Deprecated // No longer used
    public Integer definedStereo() {
        return null;
    }

    @Deprecated //No longer used
    public Integer stereocenters() {
        return null;
    }

    public Double TPSA() {
        return compound.tpsa
    }

    public Double logP() {
        return compound.xlogp
    }


    public String getName() {
        return compound.name;
    }

    public String getIupacName() {
        return compound.iupacName;
    }

    public String getUrl() {
        return compound.url
    }

    public Integer getComplexity() {
        return compound.complexity
    }
    //TODO: Make compound class an Enum
    public String getCompoundClass() {
        return compound.compoundClass
    }

    public int getNumberOfAssays() {
        return compound.getNumAssay() ? compound.getNumAssay().intValue() : 0 as int
    }

    public int getNumberOfActiveAssays() {
        return compound.getNumActiveAssay() ? compound.getNumActiveAssay().intValue() : 0 as int
    }

    public String resourcePath() {
        return compound.resourcePath
    }
    public Long getBardProjectId(){
        return compound.getBardProjectId()
    }
    public Long getCapProjectId(){
        return compound.getCapProjectId()
    }
}
