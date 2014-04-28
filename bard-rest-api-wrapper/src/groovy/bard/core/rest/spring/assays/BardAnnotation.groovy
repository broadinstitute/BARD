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

package bard.core.rest.spring.assays;

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import bard.core.rest.spring.util.JsonUtil

/**
 * A set of annotations for a BARD entity.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BardAnnotation extends JsonUtil {

    @JsonProperty("contexts")
    List<Context> contexts = new ArrayList<Context>();
    @JsonProperty("measures")
    List<Measure> measures = new ArrayList<Measure>();
    List<Doc> docs = new ArrayList<Doc>();
    @JsonProperty("misc")
    List<Annotation> otherAnnotations = new ArrayList<Annotation>();

    /**
     * Finds all of the Contexts that have at least one Annotation with a matching key.
     * Key's are matched using String#contains
     *
     * @param keysToFind one or more keys to find
     * @return a list of Contexts
     */
    public List<Context> findAssayContextsContainingKeys(String... keysToFind) {
        contexts.findAll() { Context context ->
            context.contextItems.any { Annotation contextItem ->
                keysToFind.any { contextItem.key.contains(it) } && contextItem.entity == "assay"
            }
        }
    }

    public List<Context> findAssayContextsForExperimentalVariables() {
        contexts.findAll() { Context context ->
            context.contextItems.any { Annotation contextItem ->
                (!contextItem.display || contextItem.key.contains("screening concentration") || contextItem.key.contains("project lead")) && contextItem.entity == "assay"
            }
        }
    }

    public List<Context> findOrphanAssayContexts() {
        List<Context> orphans = new ArrayList<Context>()
        orphans.addAll(contexts)
        orphans.removeAll(findAssayContextsContainingKeys('readout','detection','assay component role','assay method','assay parameter',
                'assay format', 'assay footprint', 'biology', 'assay type', 'wavelength'))
        orphans.removeAll(findAssayContextsForExperimentalVariables())
        return orphans
    }

    /**
     * Hook up relationships between contexts and measures.
     */
    public void populateContextMeasureRelationships() {
        measures.each { Measure currMeasure ->
            if(currMeasure.parseParentMeasureId()) {
                Measure parentMeasure = measures.find { it.id == currMeasure.parseParentMeasureId() }
                currMeasure.setParent(parentMeasure)
                if (!parentMeasure.children.contains(currMeasure)) { // guard against duplicate parent ids
                    parentMeasure.children.add(currMeasure)
                }
            }
            currMeasure.parseRelatedContextIds().each { Long contextId ->
                Context context = contexts.find { it.id == contextId }
                if (context && !currMeasure.relatedContexts.contains(context)) {
                    currMeasure.relatedContexts << context
                }
                if (context && !context.relatedMeasures.contains(currMeasure)) {
                    context.relatedMeasures << currMeasure
                }
            }
        }
        // This second step should be unnecessary since all of the references
        // should have been created in the previous step, but I've added it
        // just in case there are data inconsistencies in the response.
        contexts.each { Context currContext ->
            currContext.parseRelatedMeasureIds().each { Long measureId ->
                Measure measure = measures.find { it.id == measureId }
                if (measure && !currContext.relatedMeasures.contains(measure)) {
                    currContext.relatedMeasures << measure
                }
                if (measure && !measure.relatedContexts.contains(currContext)) {
                    measure.relatedContexts << currContext
                }
            }
        }
    }

    public List<Doc> findPublications() {
        this.docs.findAll { it.name == "Publication" }
    }

    public List<Doc> findExternalUrls() {
        this.docs.findAll { it.name == "External URL" }
    }

    /**
     * Checks if any of the annotations list actually contains some one or more non-empty context items ([Annotations] -> [Context] -> [Context items == Annotation]
     * @param annotations
     * @return
     */
    public static Boolean areAnnotationsEmpty(BardAnnotation annotations) {
        if (annotations && annotations.contexts) {
            return annotations.contexts.isEmpty()
        }
        return true
    }

    /**
     * Checks if any of the annotations list actually contains some one or more non-empty otherAnnotations items ([Annotations] -> [otherAnnotations items == Annotation]
     * @param annotations
     * @return
     */
    public static Boolean areOtherAnnotationsEmpty(BardAnnotation annotations) {
        if (annotations && annotations.otherAnnotations) {
            return annotations.otherAnnotations.isEmpty()
        }
        return true
    }
}
