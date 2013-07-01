package register.crowd

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Created with IntelliJ IDEA.
 * User: jasiedu
 * Date: 4/3/13
 * Time: 4:16 PM
 * To change this template use File | Settings | File Templates.
 */
public class Link {

    @JsonProperty("href")
    private String href;
    @JsonProperty("rel")
    private String rel;

    @JsonProperty("href")
    public String getHref() {
        return href;
    }

    @JsonProperty("href")
    public void setHref(String href) {
        this.href = href;
    }

    @JsonProperty("rel")
    public String getRel() {
        return rel;
    }

    @JsonProperty("rel")
    public void setRel(String rel) {
        this.rel = rel;
    }
}
