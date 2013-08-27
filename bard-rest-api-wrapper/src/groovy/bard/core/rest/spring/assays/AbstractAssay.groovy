package bard.core.rest.spring.assays

import bard.core.rest.spring.util.JsonUtil
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

@JsonInclude(JsonInclude.Include.NON_NULL)
public class AbstractAssay extends JsonUtil {

    String designedBy
    String assayType
    String assayStatus
    String comments;
    long aid;
    long bardAssayId;
    long capAssayId;
    long category;
    long type;
    long summary;
    long assays;
    long classification;
    String name;
    String title;
    String source;
    String grantNo;
    String deposited;
    String updated;
    List<String> kegg_disease_names = new ArrayList<String>();
    List<String> kegg_disease_cat = new ArrayList<String>();
    String resourcePath;
    String description;
    String protocol;
    @JsonProperty("minimumAnnotations")
    MinimumAnnotation minimumAnnotation;

    @Deprecated
    @JsonProperty("assay_id")
    long assayId;

    public long getId() {
        if (this.bardAssayId) {
            return this.bardAssayId
        }
        return this.assayId
    }

}
