package bard.core.rest.spring.assays
import bard.core.rest.spring.biology.BiologyEntity
import bard.core.rest.spring.experiment.ExperimentSearch
import bard.core.rest.spring.project.Project
import bard.core.rest.spring.util.Document
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ExpandedAssay extends AbstractAssay {

    List<Document> documents = new ArrayList<Document>();
    @JsonProperty("targets")
    List<BiologyEntity> biology = new ArrayList<BiologyEntity>();
    List<ExperimentSearch> experiments = new ArrayList<ExperimentSearch>();
    List<Project> projects = new ArrayList<Project>();

}
