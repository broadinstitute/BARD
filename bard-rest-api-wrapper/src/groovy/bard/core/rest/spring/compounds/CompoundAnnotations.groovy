package bard.core.rest.spring.compounds;


import bard.core.rest.spring.util.JsonUtil
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

@JsonInclude(JsonInclude.Include.NON_NULL)
public class CompoundAnnotations extends JsonUtil {

    @JsonProperty("anno_key")
    private List<String> anno_key = new ArrayList<String>();
    @JsonProperty("anno_val")
    private List<String> anno_val = new ArrayList<String>();

    @JsonProperty("anno_key")
    public List<String> getAnno_key() {
        return anno_key;
    }

    @JsonProperty("anno_key")
    public void setAnno_key(List<String> anno_key) {
        this.anno_key = anno_key;
    }

    @JsonProperty("anno_val")
    public List<String> getAnno_val() {
        return anno_val;
    }

    @JsonProperty("anno_val")
    public void setAnno_val(List<String> anno_val) {
        this.anno_val = anno_val;
    }
    public Map<String,List<String>> getAnnotations(){
        Map<String,List<String>> map = [:]
        for(int index = 0 ; index < anno_key.size();index++){
           String key = this.anno_key.get(index)
           String val = this.anno_val.get(index)
           List<String> vals = map.get(key)
            if(!vals){
                vals = []
            }
            vals.add(val)
            map.put(key,vals)
        }
        return map
    }
    public List<String> getCompoundCollection(){
        return getAnnotations().get("COLLECTION")
    }
    public List<String> getSynonyms(){
        return getAnnotations().get("Synonyms")
    }
    public List<String> getDocuments(){
        return getAnnotations().get("DOCUMENTS")
    }
    public String getUniqueIngredientIdentifier(){
        return getAnnotations().get("CompoundUNII")?.get(0)
    }
    public List<String> getRegistryNumbers(){
        return getAnnotations().get("CompoundCAS")
    }
    public List<String> getTherapeuticIndication(){
        return getAnnotations().get("CompoundIndication")
    }
    public List<String> getPrescriptionDrugLabel(){
        return getAnnotations().get("CompoundDrugLabelRx")
    }
    public List<String> getMechanismOfAction(){
        return getAnnotations().get("CompoundMOA")
    }
    public List<String> getCompoundSpectra(){
        return getAnnotations().get("CompoundSpectra")
    }

}
