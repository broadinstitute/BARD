package bard.core.rest.spring.compounds;


import org.apache.commons.lang.builder.EqualsBuilder
import org.apache.commons.lang.builder.HashCodeBuilder
import org.apache.commons.lang.builder.ToStringBuilder
import com.fasterxml.jackson.annotation.*

@JsonInclude(JsonInclude.Include.NON_NULL)
//@JsonIgnoreProperties
public class Compound {

    private String etag;
    @JsonProperty("preferred_term")
    private String freeTextName;
    @JsonProperty("iupac_name")
    private String freeTextIupacName
    @JsonProperty("compound_class")
    private String freeTextCompoundClass;
    @JsonProperty("iso_smiles")
    private String freeTextSmiles;


    @JsonProperty("cid")
    private Integer cid;
    @JsonProperty("probeId")
    private Object probeId;
    @JsonProperty("url")
    private Object url;
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
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();
    @JsonProperty("anno_key")
    private List<String> anno_key = new ArrayList<String>();
    @JsonProperty("anno_val")
    private List<String> anno_val = new ArrayList<String>();


    public Compound() {

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
    public Integer getCid() {
        return cid;
    }

    @JsonProperty("cid")
    public void setCid(Integer cid) {
        this.cid = cid;
    }

    @JsonProperty("probeId")
    public Object getProbeId() {
        return probeId;
    }

    @JsonProperty("probeId")
    public void setProbeId(Object probeId) {
        this.probeId = probeId;
    }

    @JsonProperty("url")
    public Object getUrl() {
        return url;
    }

    @JsonProperty("url")
    public void setUrl(Object url) {
        this.url = url;
    }

    @JsonProperty("smiles")
    public String getSmiles() {
        return smiles ?: this.freeTextSmiles;
    }

    @JsonProperty("smiles")
    public void setSmiles(String smiles) {
        this.smiles = smiles;
    }

    @JsonProperty("name")
    public String getName() {
        return name ?: this.freeTextName;
    }

    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty("iupacName")
    public String getIupacName() {
        return iupacName ?: this.freeTextIupacName;
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
        return compoundClass ?: this.freeTextCompoundClass;
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
        return freeTextName
    }

    @JsonProperty("preferred_term")
    void setFreeTextName(String freeTextName) {
        this.freeTextName = freeTextName
    }

    @JsonProperty("iupac_name")
    String getFreeTextIupacName() {
        return freeTextIupacName
    }

    @JsonProperty("iupac_name")
    void setFreeTextIupacName(String freeTextIupacName) {
        this.freeTextIupacName = freeTextIupacName
    }

    @JsonProperty("compound_class")
    String getFreeTextCompoundClass() {
        return freeTextCompoundClass
    }

    @JsonProperty("compound_class")
    void setFreeTextCompoundClass(String freeTextCompoundClass) {
        this.freeTextCompoundClass = freeTextCompoundClass
    }

    @JsonProperty("iso_smiles")
    String getFreeTextSmiles() {
        return freeTextSmiles
    }

    @JsonProperty("iso_smiles")
    void setFreeTextSmiles(String freeTextSmiles) {
        this.freeTextSmiles = freeTextSmiles
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

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public boolean equals(Object other) {
        return EqualsBuilder.reflectionEquals(this, other);
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperties(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    public String getEtag() {
        return etag
    }

    public void setEtag(String etag) {
        this.etag = etag
    }


}

