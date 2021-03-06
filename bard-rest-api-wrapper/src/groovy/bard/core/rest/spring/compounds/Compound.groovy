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
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Compound extends JsonUtil {

    private String etag;

    private CompoundAnnotations compoundAnnotations


    @JsonProperty("probeAnnotations")
    private List<ProbeAnnotation> probeAnnotations = new ArrayList<ProbeAnnotation>();
    @JsonProperty("bardProjectId")
    private Long bardProjectId
    @JsonProperty("capProjectId")
    private Long capProjectId
    @JsonProperty("cid")
    private Long cid;
    @JsonProperty("probeId")
    private String probeId;
    @JsonProperty("url")
    private String url;
    @JsonProperty("smiles")
    private String smiles;
    @JsonProperty("name")
    private String name;
    @JsonProperty("iupacName")
    private String iupacName;
    @JsonProperty("mwt")
    private Double mwt;
    @JsonProperty("tpsa")
    private Double tpsa;
    @JsonProperty("exactMass")
    private Double exactMass;
    @JsonProperty("xlogp")
    private Double xlogp;
    @JsonProperty("complexity")
    private Integer complexity;
    @JsonProperty("rotatable")
    private Integer rotatable;
    @JsonProperty("hbondAcceptor")
    private Integer hbondAcceptor;
    @JsonProperty("hbondDonor")
    private Integer hbondDonor;
    @JsonProperty("compoundClass")
    private String compoundClass;
    @JsonProperty("numAssay")
    private Integer numAssay;
    @JsonProperty("numActiveAssay")
    private Integer numActiveAssay;
    @JsonProperty("highlight")
    private Object highlight;
    @JsonProperty("resourcePath")
    private String resourcePath;
    @JsonProperty("anno_key")
    private List<String> anno_key = new ArrayList<String>();
    @JsonProperty("anno_val")
    private List<String> anno_val = new ArrayList<String>();


    public Compound() {

    }
    CompoundAnnotations getCompoundAnnotations() {
        return compoundAnnotations
    }

    void setCompoundAnnotations(CompoundAnnotations compoundAnnotations) {
        this.compoundAnnotations = compoundAnnotations
    }
    @JsonProperty("probeAnnotations")
    public List<ProbeAnnotation> getProbeAnnotations() {
        return this.probeAnnotations
    }
    @JsonProperty("probeAnnotations")
    public void setProbeAnnotations(List<ProbeAnnotation> probeAnnotations) {
        this.probeAnnotations = probeAnnotations
    }

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

    @JsonProperty("cid")
    public Long getCid() {
        return cid;
    }

    @JsonProperty("cid")
    public void setCid(Long cid) {
        this.cid = cid;
    }

    @JsonProperty("probeId")
    public String getProbeId() {
        return probeId;
    }

    @JsonProperty("probeId")
    public void setProbeId(String probeId) {
        this.probeId = probeId;
    }

    @JsonProperty("url")
    public String getUrl() {
        return url;
    }

    @JsonProperty("url")
    public void setUrl(String url) {
        this.url = url;
    }

    @JsonProperty("smiles")
    public String getSmiles() {
        return smiles
    }

    @JsonProperty("smiles")
    public void setSmiles(String smiles) {
        this.smiles = smiles;
    }

    @JsonProperty("name")
    public String getName() {
        return name
    }

    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty("iupacName")
    public String getIupacName() {
        return iupacName
    }

    @JsonProperty("iupacName")
    public void setIupacName(String iupacName) {
        this.iupacName = iupacName;
    }

    @JsonProperty("mwt")
    public Double getMwt() {
        return mwt;
    }

    @JsonProperty("mwt")
    public void setMwt(Double mwt) {
        this.mwt = mwt;
    }

    @JsonProperty("tpsa")
    public Double getTpsa() {
        return tpsa;
    }

    @JsonProperty("tpsa")
    public void setTpsa(Double tpsa) {
        this.tpsa = tpsa;
    }

    @JsonProperty("exactMass")
    public Double getExactMass() {
        return exactMass;
    }

    @JsonProperty("exactMass")
    public void setExactMass(Double exactMass) {
        this.exactMass = exactMass;
    }

    @JsonProperty("xlogp")
    public Double getXlogp() {
        return xlogp;
    }

    @JsonProperty("xlogp")
    public void setXlogp(Double xlogp) {
        this.xlogp = xlogp;
    }

    @JsonProperty("complexity")
    public Integer getComplexity() {
        return complexity;
    }

    @JsonProperty("complexity")
    public void setComplexity(Integer complexity) {
        this.complexity = complexity;
    }

    @JsonProperty("rotatable")
    public Integer getRotatable() {
        return rotatable;
    }

    @JsonProperty("rotatable")
    public void setRotatable(Integer rotatable) {
        this.rotatable = rotatable;
    }

    @JsonProperty("hbondAcceptor")
    public Integer getHbondAcceptor() {
        return hbondAcceptor;
    }

    @JsonProperty("hbondAcceptor")
    public void setHbondAcceptor(Integer hbondAcceptor) {
        this.hbondAcceptor = hbondAcceptor;
    }

    @JsonProperty("hbondDonor")
    public Integer getHbondDonor() {
        return hbondDonor;
    }

    @JsonProperty("hbondDonor")
    public void setHbondDonor(Integer hbondDonor) {
        this.hbondDonor = hbondDonor;
    }

    @JsonProperty("compoundClass")
    public String getCompoundClass() {
        return compoundClass
    }

    @JsonIgnore
    public boolean isDrug() {
        return this.getCompoundClass() == "Drug"
    }

    @JsonIgnore
    public boolean isProbe() {
        return this.probeId != null
    }

    @JsonProperty("compoundClass")
    public void setCompoundClass(String compoundClass) {
        this.compoundClass = compoundClass;
    }

    @JsonProperty("numAssay")
    public Integer getNumAssay() {
        return numAssay;
    }

    @JsonProperty("numAssay")
    public void setNumAssay(Integer numAssay) {
        this.numAssay = numAssay;
    }

    @JsonProperty("numActiveAssay")
    public Integer getNumActiveAssay() {
        return numActiveAssay;
    }

    @JsonProperty("numActiveAssay")
    public void setNumActiveAssay(Integer numActiveAssay) {
        this.numActiveAssay = numActiveAssay;
    }

    @JsonProperty("preferred_term")
    String getFreeTextName() {
        return name
    }

    @JsonProperty("preferred_term")
    void setFreeTextName(String freeTextName) {
        this.name = freeTextName
    }

    @JsonProperty("iupac_name")
    String getFreeTextIupacName() {
        return this.iupacName
    }

    @JsonProperty("iupac_name")
    void setFreeTextIupacName(String freeTextIupacName) {
        this.iupacName = freeTextIupacName
    }

    @JsonProperty("compound_class")
    String getFreeTextCompoundClass() {
        return this.compoundClass
    }

    @JsonProperty("compound_class")
    void setFreeTextCompoundClass(String freeTextCompoundClass) {
        this.compoundClass = freeTextCompoundClass
    }

    @JsonProperty("iso_smiles")
    String getFreeTextSmiles() {
        return this.smiles
    }

    @JsonProperty("iso_smiles")
    void setFreeTextSmiles(String freeTextSmiles) {
        this.smiles = freeTextSmiles
    }


    @JsonProperty("highlight")
    public Object getHighlight() {
        return highlight;
    }

    @JsonProperty("highlight")
    public void setHighlight(Object highlight) {
        this.highlight = highlight;
    }

    @JsonProperty("resourcePath")
    public String getResourcePath() {
        return resourcePath;
    }

    @JsonProperty("resourcePath")
    public void setResourcePath(String resourcePath) {
        this.resourcePath = resourcePath;
    }
    /**
     * Properties set by Probes
     * @return
     */
    @JsonProperty("bardProjectId")
    public Long getBardProjectId(){
        return this.bardProjectId
    }
    @JsonProperty("bardProjectId")
    public void setBardProjectId(Long bardProjectId){
        this.bardProjectId=bardProjectId
    }

    @JsonProperty("capProjectId")
    public Long getCapProjectId(){
        return this.capProjectId
    }
    @JsonProperty("capProjectId")
    public void setCapProjectId(Long capProjectId){
        this.capProjectId=capProjectId
    }
    public String getEtag() {
        return etag
    }

    public void setEtag(String etag) {
        this.etag = etag
    }

    public Long getId() {
        return this.cid;
    }
    ProbeAnnotation getProbeCid(){
        for(ProbeAnnotation probeAnnotation: getProbeAnnotations()){
            if(probeAnnotation.isPubChemCIDLink()){
                return probeAnnotation
            }
        }
    }
    ProbeAnnotation getProbe(){
        for(ProbeAnnotation probeAnnotation: getProbeAnnotations()){
            if(probeAnnotation.isProbeLink()){
                return probeAnnotation
            }
        }
    }
    ProbeAnnotation getProbeSid(){
        for(ProbeAnnotation probeAnnotation: getProbeAnnotations()){
            if(probeAnnotation.isPubChemSIDLink()){
                return probeAnnotation
            }
        }
    }

}

