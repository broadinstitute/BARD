package bard.core.rest.spring.compounds;


import bard.core.rest.spring.util.JsonUtil
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Compound extends JsonUtil {

    private String etag;

    private CompoundAnnotations compoundAnnotations
    @JsonProperty("bardProjectid")
    private String bardProjectid
    @JsonProperty("cid")
    private Integer cid;
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
    @JsonProperty("bardProjectid")
    public String getBardProjectid(){
        return this.bardProjectid
    }
    @JsonProperty("bardProjectid")
    public void setBardProjectid(String bardProjectId){
        this.bardProjectid=bardProjectId
    }
    public String getEtag() {
        return etag
    }

    public void setEtag(String etag) {
        this.etag = etag
    }

    public Integer getId() {
        return this.cid;
    }

}

