package bard.core.rest.spring.assays
import bard.core.rest.spring.util.JsonUtil
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Serialized usually from an ID search or contained in an expanded element (e.g Experiment)
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Context extends JsonUtil {

    @JsonProperty("id")
    private long id;
    @JsonProperty("name")
    private String name;
    @JsonProperty("comps")
    private List<Comp> comps = new ArrayList<Comp>();

    @JsonProperty("id")
    public long getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(long id) {
        this.id = id;
    }

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty("comps")
    public List<Comp> getComps() {
        return comps;
    }

    @JsonProperty("comps")
    public void setComps(List<Comp> comps) {
        this.comps = comps;
    }

    /**
     * a hack to try and split the contexts into columns of relatively equal contextItems
     *
     * an attempt at limiting white space and compressing the view
     *
     * @param contexts
     * @return list of up to 2 lists
     */
    static List<List<Context>> splitForColumnLayout(List<Context> contexts) {
        int totalNumContextItems = contexts.collect { it.getComps().size() }.sum()
        int half = totalNumContextItems / 2
        int count = 0
        List<Context> firstColumnContexts = contexts.findAll { context ->
            count += context.getComps().size();
            count <= half
        }
        List<Context> secondColumnContexts = contexts - firstColumnContexts
        def splitContexts = [firstColumnContexts, secondColumnContexts].findAll() // eliminates any empty lists
        splitContexts
    }

}
