package bard.core.rest.spring.assays
import bard.core.rest.spring.util.JsonUtil
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * A grouping of context items.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Context extends JsonUtil {

    long id;
    String name;
    @JsonProperty("comps")
    List<Annotation> contextItems = new ArrayList<Annotation>();

    @JsonIgnore
    List<Measure> relatedMeasures = new ArrayList<Measure>()

    /**
     * a hack to try and split the contexts into columns of relatively equal contextItems
     *
     * an attempt at limiting white space and compressing the view
     *
     * @param contexts
     * @return list of up to 2 lists
     */
    static List<List<Context>> splitForColumnLayout(List<Context> contexts) {
        int totalNumContextItems = contexts.collect { it?.getContextItems()?.size() ?: 0 }.sum() ?: 0
        int half = totalNumContextItems / 2
        int count = 0
        List<Context> firstColumnContexts = contexts.findAll { context ->
            count += context.getContextItems().size();
            count <= half
        }
        List<Context> secondColumnContexts = contexts - firstColumnContexts
        def splitContexts = [firstColumnContexts, secondColumnContexts].findAll() // eliminates any empty lists
        return splitContexts
    }

}
