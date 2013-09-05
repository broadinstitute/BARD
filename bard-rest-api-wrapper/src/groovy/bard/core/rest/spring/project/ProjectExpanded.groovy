package bard.core.rest.spring.project

import bard.core.rest.spring.assays.Assay

import bard.core.rest.spring.experiment.ExperimentSearch

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import bard.core.rest.spring.util.Document

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProjectExpanded extends ProjectAbstract {

    @JsonProperty("eids")
    List<ExperimentSearch> experiments = new ArrayList<ExperimentSearch>();
    @JsonProperty("aids")
    List<Assay> assays = new ArrayList<Assay>();
    List<Document> publications = new ArrayList<Document>();

}
