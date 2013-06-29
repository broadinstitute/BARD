package register.crowd

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Created with IntelliJ IDEA.
 * User: jasiedu
 * Date: 3/31/13
 * Time: 8:51 PM
 * To change this template use File | Settings | File Templates.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
class CrowdRegistrationUser {
    @JsonProperty("name")
    private String name;
    @JsonProperty("first-name")
    private String first_name;
    @JsonProperty("last-name")
    private String last_name;
    @JsonProperty("display-name")
    private String display_name;
    @JsonProperty("email")
    private String email;
    @JsonProperty("password")
    private CrowdPassword password;
    @JsonProperty("active")
    private boolean active;

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty("first-name")
    public String getFirst_name() {
        return first_name;
    }

    @JsonProperty("first-name")
    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    @JsonProperty("last-name")
    public String getLast_name() {
        return last_name;
    }

    @JsonProperty("last-name")
    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    @JsonProperty("display-name")
    public String getDisplay_name() {
        return display_name;
    }

    @JsonProperty("display-name")
    public void setDisplay_name(String display_name) {
        this.display_name = display_name;
    }

    @JsonProperty("email")
    public String getEmail() {
        return email;
    }

    @JsonProperty("email")
    public void setEmail(String email) {
        this.email = email;
    }

    @JsonProperty("password")
    public CrowdPassword getPassword() {
        return password;
    }

    @JsonProperty("password")
    public void setPassword(CrowdPassword password) {
        this.password = password;
    }

    @JsonProperty("active")
    public boolean isActive() {
        return active;
    }

    @JsonProperty("active")
    public void setActive(boolean active) {
        this.active = active;
    }


}




