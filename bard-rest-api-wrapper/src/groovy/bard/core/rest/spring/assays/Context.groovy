/* Copyright (c) 2014, The Broad Institute
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
    * Redistributions of source code must retain the above copyright
      notice, this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright
      notice, this list of conditions and the following disclaimer in the
      documentation and/or other materials provided with the distribution.
    * Neither the name of The Broad Institute nor the
      names of its contributors may be used to endorse or promote products
      derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL The Broad Institute BE LIABLE FOR ANY
DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

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

    String toString() {
        return "${this.name} (${this.id})"
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
