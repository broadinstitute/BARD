package bard.core.rest.spring.experiment

import bard.core.rest.spring.util.JsonUtil
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ExperimentData extends JsonUtil {

    @JsonProperty("collection")
    private List<Activity> activities = new ArrayList<Activity>();
    @JsonProperty("link")
    private String link;

    @JsonProperty("collection")
    public List<Activity> getActivities() {
        return activities;
    }

    @JsonProperty("collection")
    public void setActivities(List<Activity> activities) {
        this.activities = activities;
    }

    @JsonProperty("link")
    public String getLink() {
        return link;
    }

    @JsonProperty("link")
    public void setLink(String link) {
        this.link = link;
    }
}
