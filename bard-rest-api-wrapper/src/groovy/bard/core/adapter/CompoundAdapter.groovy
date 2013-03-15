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
        return this.getPubChemCID()
    }

    @Deprecated //use getId instead
    public Long getPubChemCID() {
        return this.compound.cid
    }
    /**
     *
     * @return
     */
    @Deprecated //use getSmiles instead
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
