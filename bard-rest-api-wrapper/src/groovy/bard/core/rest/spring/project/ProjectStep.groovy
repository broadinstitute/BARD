package bard.core.rest.spring.project

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Created with IntelliJ IDEA.
 * User: jasiedu
 * Date: 2/14/13
 * Time: 9:18 AM
 * To change this template use File | Settings | File Templates.
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProjectStep {

    @JsonProperty("prevBardExpt")
    private ProjectExperiment prevBardExpt;
    @JsonProperty("nextBardExpt")
    private ProjectExperiment nextBardExpt;
    @JsonProperty("bardProjId")
    private long bardProjId;
    @JsonProperty("prevStageRef")
    private String prevStageRef;
    @JsonProperty("nextStageRef")
    private String nextStageRef;
    @JsonProperty("stepId")
    private long stepId;
    @JsonProperty("edgeName")
    private String edgeName;
    @JsonProperty("annotations")
    private List<ProjectStepAnnotation> annotations = new ArrayList<ProjectStepAnnotation>();
    @JsonProperty("resourcePath")
    private String resourcePath;

    @JsonProperty("prevBardExpt")
    public ProjectExperiment getPrevBardExpt() {
        return prevBardExpt;
    }

    @JsonProperty("prevBardExpt")
    public void setPrevBardExpt(ProjectExperiment prevBardExpt) {
        this.prevBardExpt = prevBardExpt;
    }

    @JsonProperty("nextBardExpt")
    public ProjectExperiment getNextBardExpt() {
        return nextBardExpt;
    }

    @JsonProperty("nextBardExpt")
    public void setNextBardExpt(ProjectExperiment nextBardExpt) {
        this.nextBardExpt = nextBardExpt;
    }

    @JsonProperty("bardProjId")
    public long getBardProjId() {
        return bardProjId;
    }

    @JsonProperty("bardProjId")
    public void setBardProjId(long bardProjId) {
        this.bardProjId = bardProjId;
    }

    @JsonProperty("prevStageRef")
    String getPrevStageRef() {
        return prevStageRef
    }

    @JsonProperty("prevStageRef")
    void setPrevStageRef(String prevStageRef) {
        this.prevStageRef = prevStageRef
    }

    @JsonProperty("nextStageRef")
    String getNextStageRef() {
        return nextStageRef
    }

    @JsonProperty("nextStageRef")
    void setNextStageRef(String nextStageRef) {
        this.nextStageRef = nextStageRef
    }

    @JsonProperty("stepId")
    public long getStepId() {
        return stepId;
    }

    @JsonProperty("stepId")
    public void setStepId(long stepId) {
        this.stepId = stepId;
    }

    @JsonProperty("edgeName")
    public String getEdgeName() {
        return edgeName;
    }

    @JsonProperty("edgeName")
    public void setEdgeName(String edgeName) {
        this.edgeName = edgeName;
    }

    @JsonProperty("annotations")
    public List<ProjectStepAnnotation> getAnnotations() {
        return annotations;
    }

    @JsonProperty("annotations")
    public void setAnnotations(List<ProjectStepAnnotation> annotations) {
        this.annotations = annotations;
    }

    @JsonProperty("resourcePath")
    public String getResourcePath() {
        return resourcePath;
    }

    @JsonProperty("resourcePath")
    public void setResourcePath(String resourcePath) {
        this.resourcePath = resourcePath;
    }

}






