package bard.core.rest.spring.util

import com.fasterxml.jackson.annotation.JsonAnyGetter
import com.fasterxml.jackson.annotation.JsonAnySetter
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import org.apache.commons.lang.builder.EqualsBuilder
import org.apache.commons.lang.builder.HashCodeBuilder
import org.apache.commons.lang.builder.ToStringBuilder
import com.fasterxml.jackson.annotation.JsonPropertyOrder

/**
 * Created with IntelliJ IDEA.
 * User: jasiedu
 * Date: 11/23/12
 * Time: 11:29 PM
 * To change this template use File | Settings | File Templates.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder([
    "collection",
    "link"
])
public class ETagCollection extends JsonUtil{

    @JsonProperty("collection")
    private List<ETag> etags = new ArrayList<ETag>();
    @JsonProperty("link")
    private String link;

    @JsonProperty("collection")
    public List<ETag> getEtags() {
        return etags;
    }

    @JsonProperty("collection")
    public void setEtags(List<ETag> etags) {
        this.etags = etags;
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