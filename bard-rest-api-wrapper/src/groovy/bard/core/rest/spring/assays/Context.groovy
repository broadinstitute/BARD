package bard.core.rest.spring.assays
import bard.core.rest.spring.util.JsonUtil
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import org.apache.commons.lang.builder.EqualsBuilder
import org.apache.commons.lang.builder.HashCodeBuilder

import java.util.regex.Matcher
import java.util.regex.Pattern

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

    static final Pattern RELATED_MEASURES_PATTERN = ~/^measureRefs:([\d,]+)/

    List<Long> parseRelatedMeasureIds() {
        def measureIds = []
        contextItems.each { Annotation contextItem ->
            if (contextItem.related) {
                Matcher m = RELATED_MEASURES_PATTERN.matcher(contextItem.related)
                if (m && m.groupCount() > 0) {
                    m.group(1).split(",").each {
                        Long id = it as Long
                        if (!measureIds.contains(id)) {
                            measureIds << id
                        }
                    }
                }
            }
        }
        return measureIds
    }

    int hashCode() {
        def builder = new HashCodeBuilder()
        builder.append id
        builder.append name
        builder.append contextItems
        builder.toHashCode()
    }

    boolean equals(other) {
        def builder = new EqualsBuilder()
        builder.append(id, other.id)
        builder.append(name, other.name)
        builder.append(contextItems, other.contextItems)
        return builder.equals
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
        int totalNumContextItems = contexts.collect { it?.getContextItems()?.size() ?: 0 }.sum() ?: 0
        int half = totalNumContextItems / 2
        int count = 0
        def sortedContexts = contexts.sort {
            it.contextItems.size()
        }
        List<Context> firstColumnContexts = sortedContexts.findAll { context ->
            count += context.getContextItems().size();
            count <= half
        }
        List<Context> secondColumnContexts = sortedContexts - firstColumnContexts
        def splitContexts = [firstColumnContexts, secondColumnContexts].findAll() // eliminates any empty lists
        return splitContexts
    }

}
