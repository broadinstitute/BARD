package bard.core.rest.spring.project

import bard.core.rest.spring.compounds.Compound
import bard.core.rest.spring.util.JsonUtil
import bard.core.rest.spring.util.Target
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import bard.core.rest.spring.util.Document

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Project extends ProjectAbstract {

     @JsonProperty("eids")
    private List<Long> eids = new ArrayList<Long>();
    @JsonProperty("aids")
    private List<Long> aids = new ArrayList<Long>();
    @JsonProperty("eids")
    public List<Long> getEids() {
        return eids
    }
    @JsonProperty("eids")
    public void setEids(List<Long> eids) {
        this.eids = eids
    }
    @JsonProperty("aids")
    public List<Long> getAids() {
        return aids
    }
    @JsonProperty("aids")
    public void setAids(List<Long> aids) {
        this.aids = aids
    }
    @JsonProperty("publications")
    private List<Long> publications;

    @JsonProperty("publications")
    public List<Long> getPublications() {
        return publications;
    }

    @JsonProperty("publications")
    public void setPublications(List<Long> publications) {
        this.publications = publications;
    }

}
