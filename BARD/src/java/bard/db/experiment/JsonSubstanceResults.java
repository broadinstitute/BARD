package bard.db.experiment;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: pmontgom
 * Date: 2/26/13
 * Time: 8:51 PM
 * To change this template use File | Settings | File Templates.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class JsonSubstanceResults {
    Long sid;
    List<JsonResult> rootElem;

    public Long getSid() {
        return sid;
    }

    public void setSid(Long sid) {
        this.sid = sid;
    }

    public List<JsonResult> getRootElem() {
        return rootElem;
    }

    public void setRootElem(List<JsonResult> rootElem) {
        this.rootElem = rootElem;
    }
}
