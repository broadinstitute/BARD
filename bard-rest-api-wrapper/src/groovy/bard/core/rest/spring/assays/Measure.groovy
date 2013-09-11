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
 * Serialized usually from an ID search or contained in an expanded element (e.g Experiment)
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Measure extends JsonUtil {

    long id;
    @JsonProperty("name")
    String name;
    @JsonProperty("comps")
    List<Annotation> comps = new ArrayList<Annotation>()

    @JsonIgnore
    Measure parent
    @JsonIgnore
    List<Measure> children = new ArrayList<Measure>()
    @JsonIgnore
    List<Context> relatedContexts = new ArrayList<Context>()

    static final Pattern PARENT_MEASURE_PATTERN = ~/^[\w:,]*\|parentMeasure:(\d+)/
    static final Pattern RELATED_CONTEXTS_PATTERN = ~/^assayContextRefs:([\d,]+)/

    Long parseParentMeasureId() {
        String related = comps.first()?.related
        if (!related) {
            return null
        }
        Matcher m = PARENT_MEASURE_PATTERN.matcher(related)
        if (m && m.groupCount() > 0) {
            return m.group(1) as Long
        }
        return null
    }

    List<Long> parseRelatedContextIds() {
        String related = comps.first()?.related
        if (!related) {
            return []
        }
        Matcher m = RELATED_CONTEXTS_PATTERN.matcher(related)
        if (m && m.groupCount() > 0) {
            return m.group(1).split(",").collect { it as Long}
        }
        return []
    }

    String toString() {
        return "${this.name} (${this.id})"
    }

    int hashCode() {
        def builder = new HashCodeBuilder()
        builder.append id
        builder.append name
        builder.append comps
        builder.toHashCode()
    }

    boolean equals(other) {
        def builder = new EqualsBuilder()
        builder.append(id, other.id)
        builder.append(name, other.name)
        builder.append(comps, other.comps)
        return builder.equals
    }

}
