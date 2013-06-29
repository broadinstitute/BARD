package register.crowd

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Created with IntelliJ IDEA.
 * User: jasiedu
 * Date: 3/31/13
 * Time: 8:53 PM
 * To change this template use File | Settings | File Templates.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CrowdPassword {

    @JsonProperty("value")
    private String value;

    @JsonProperty("value")
    public String getValue() {
        return value;
    }

    @JsonProperty("value")
    public void setValue(String value) {
        this.value = value;
    }
}
