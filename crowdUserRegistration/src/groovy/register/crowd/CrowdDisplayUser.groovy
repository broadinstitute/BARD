package register.crowd

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Created with IntelliJ IDEA.
 * User: jasiedu
 * Date: 4/3/13
 * Time: 4:16 PM
 * To change this template use File | Settings | File Templates.
 */
public class CrowdDisplayUser {

    @JsonProperty("link")
    private Link link;
    @JsonProperty("name")
    private String name;

    @JsonProperty("link")
    public Link getLink() {
        return link;
    }

    @JsonProperty("link")
    public void setLink(Link link) {
        this.link = link;
    }

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }


}
