package bard.core.rest.spring.assays
import bard.core.rest.spring.experiment.ExperimentSearch
import bard.core.rest.spring.project.Project
import bard.core.rest.spring.util.Document
import bard.core.rest.spring.util.Target
import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ExpandedAssay extends AbstractAssay {

    List<Document> documents = new ArrayList<Document>();
    List<Target> targets = new ArrayList<Target>();
    List<ExperimentSearch> experiments = new ArrayList<ExperimentSearch>();
    List<Project> projects = new ArrayList<Project>();

}
