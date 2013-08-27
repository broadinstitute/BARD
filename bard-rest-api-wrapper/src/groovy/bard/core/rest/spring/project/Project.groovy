package bard.core.rest.spring.project
import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Project extends ProjectAbstract {

    List<Long> eids = new ArrayList<Long>();
    List<Long> aids = new ArrayList<Long>();
    List<Long> publications = new ArrayList<Long>();

}
