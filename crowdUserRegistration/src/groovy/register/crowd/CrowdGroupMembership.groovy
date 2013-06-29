package register.crowd

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Created with IntelliJ IDEA.
 * User: jasiedu
 * Date: 3/31/13
 * Time: 8:53 PM
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CrowdGroupMembership {

    @JsonProperty("expand")
    private String expand;

    @JsonProperty("users")
    private List<CrowdDisplayUser> users = new ArrayList<CrowdDisplayUser>();

    @JsonProperty("expand")
    public String getExpand() {
        return expand;
    }

    @JsonProperty("expand")
    public void setExpand(String expand) {
        this.expand = expand;
    }

    @JsonProperty("users")
    public List<CrowdDisplayUser> getUsers() {
        return users;
    }

    @JsonProperty("users")
    public void setUsers(List<CrowdDisplayUser> users) {
        this.users = users;
    }

}
