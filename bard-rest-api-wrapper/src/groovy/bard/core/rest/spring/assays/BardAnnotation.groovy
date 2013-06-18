package bard.core.rest.spring.assays;

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import bard.core.rest.spring.util.JsonUtil

/**
 * A set of annotations for a BARD entity.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BardAnnotation extends JsonUtil {

    List<Context> contexts = new ArrayList<Context>();
    List<Measure> measures = new ArrayList<Measure>();
    List<Doc> docs = new ArrayList<Doc>();
    @JsonProperty("misc")
    List<Annotation> otherAnnotations = new ArrayList<Annotation>();

    public List<Context> findContextsContainingKey(String keyToFind, String entity) {
        contexts.findAll() {
            it.contextItems.count { it.key.contains(keyToFind) && it.entity == entity } > 0
        }
    }

    /**
     * Hook up relationships between contexts and measures.
     */
    public void postDeserialize() {
        populateParentChildRelationshipsForMeasures();
        populateContextMeasureRelationships();
    }

    private void populateParentChildRelationshipsForMeasures() {

    }

    private void populateContextMeasureRelationships() {
        
    }

    /**
     * Checks if any of the annotations list actually contains some one or more non-empty context items ([Annotations] -> [Context] -> [Context items == Annotation]
     * @param annotations
     * @return
     */
    public static Boolean areAnnotationsEmpty(List<BardAnnotation> annotations) {
        Boolean foundSomething = annotations.find {BardAnnotation annotation ->
            annotation.contexts.find {Context context ->
                context.getContextItems().find()
            }
        }

        return foundSomething ?: false
    }

    /**
     * Checks if any of the annotations list actually contains some one or more non-empty otherAnnotations items ([Annotations] -> [otherAnnotations items == Annotation]
     * @param annotations
     * @return
     */
    public static Boolean areOtherAnnotationsEmpty(List<BardAnnotation> annotations) {
        Boolean foundSomething = annotations.find {BardAnnotation annotation ->
            annotation.otherAnnotations.find() //List<Annotation>
        }

        return foundSomething ?: false
    }
}
