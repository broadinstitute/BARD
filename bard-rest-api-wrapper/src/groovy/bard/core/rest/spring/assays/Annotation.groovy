package bard.core.rest.spring.assays
import bard.core.rest.spring.util.JsonUtil
import com.fasterxml.jackson.annotation.JsonInclude

/**
 * Serialized usually from an ID search or contained in an expanded element (e.g Experiment)
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Annotation extends JsonUtil {

    Object entityId;
    String entity;
    String source;
    long id;
    String display;
    String contextRef;
    String key;
    String value;
    String extValueId;
    String url;
    long displayOrder;
    String related;

    static List<List<Annotation>> splitForColumnLayout(List<Annotation> comps) {
        int totalNumContextItems = comps.size()
        int half = Math.ceil(totalNumContextItems / 2) //make the first column the longest in case of an odd number of elements.

        if (totalNumContextItems) {
            List<Annotation> firstColumnComps = comps[0..(half - 1)]
            List<Annotation> secondColumnComps = comps - firstColumnComps
            def splitComps = [firstColumnComps, secondColumnComps].findAll() // eliminates any empty lists
            return splitComps
        }

        return []
    }
}
