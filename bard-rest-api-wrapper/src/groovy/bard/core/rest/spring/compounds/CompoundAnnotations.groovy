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
