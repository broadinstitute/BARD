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
    private BardExpt prevBardExpt;
    @JsonProperty("nextBardExpt")
    private BardExpt nextBardExpt;
    @JsonProperty("bardProjId")
    private Long bardProjId;
    @JsonProperty("stepId")
    private Long stepId;
    @JsonProperty("edgeName")
    private String edgeName;
    @JsonProperty("annotations")
    private List<ProjectStepAnnotation> annotations = new ArrayList<ProjectStepAnnotation>();
    @JsonProperty("resourcePath")
    private String resourcePath;

    @JsonProperty("prevBardExpt")
    public BardExpt getPrevBardExpt() {
        return prevBardExpt;
    }

    @JsonProperty("prevBardExpt")
    public void setPrevBardExpt(BardExpt prevBardExpt) {
        this.prevBardExpt = prevBardExpt;
    }

    @JsonProperty("nextBardExpt")
    public BardExpt getNextBardExpt() {
        return nextBardExpt;
    }

    @JsonProperty("nextBardExpt")
    public void setNextBardExpt(BardExpt nextBardExpt) {
        this.nextBardExpt = nextBardExpt;
    }

    @JsonProperty("bardProjId")
    public Long getBardProjId() {
        return bardProjId;
    }

    @JsonProperty("bardProjId")
    public void setBardProjId(Long bardProjId) {
        this.bardProjId = bardProjId;
    }

    @JsonProperty("stepId")
    public Long getStepId() {
        return stepId;
    }

    @JsonProperty("stepId")
    public void setStepId(Long stepId) {
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






