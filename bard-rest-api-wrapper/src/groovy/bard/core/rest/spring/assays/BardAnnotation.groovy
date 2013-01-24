package bard.core.rest.spring.assays;



import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import bard.core.rest.spring.util.JsonUtil

/**
 * Serialized usually from an ID search or contained in an expanded element (e.g Experiment)
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BardAnnotation extends JsonUtil {

    @JsonProperty("contexts")
    private List<Context> contexts = new ArrayList<Context>();
    @JsonProperty("measures")
    private List<Measure> measures = new ArrayList<Measure>();
    @JsonProperty("docs")
    private List<Doc> docs = new ArrayList<Doc>();
    @JsonProperty("misc")
    private List<Comp> otherAnnotations = new ArrayList<Comp>();

    @JsonProperty("misc")
    public List<Comp> getOtherAnnotations() {
        return otherAnnotations;
    }

    @JsonProperty("misc")
    public void setOtherAnnotations(List<Comp> otherAnnotations) {
        this.otherAnnotations = otherAnnotations;
    }

    @JsonProperty("contexts")
    public List<Context> getContexts() {
        return contexts;
    }

    @JsonProperty("contexts")
    public void setContexts(List<Context> contexts) {
        this.contexts = contexts;
    }

    @JsonProperty("measures")
    public List<Measure> getMeasures() {
        return measures;
    }

    @JsonProperty("measures")
    public void setMeasures(List<Measure> measures) {
        this.measures = measures;
    }

    @JsonProperty("docs")
    public List<Doc> getDocs() {
        return docs;
    }

    @JsonProperty("docs")
    public void setDocs(List<Doc> docs) {
        this.docs = docs;
    }

    /**
     * Checks if any of the annotations list actually contains some one or more non-empty context items ([Annotations] -> [Context] -> [Context items == Comp]
     * @param annotations
     * @return
     */
    public static Boolean areAnnotationsEmpty(List<BardAnnotation> annotations) {
        Boolean foundSomething = annotations.find {BardAnnotation annotation ->
            annotation.contexts.find {Context context ->
                context.getComps().find()
            }
        }

        return foundSomething ?: false
    }

    /**
     * Checks if any of the annotations list actually contains some one or more non-empty otherAnnotations items ([Annotations] -> [otherAnnotations items == Comp]
     * @param annotations
     * @return
     */
    public static Boolean areOtherAnnotationsEmpty(List<BardAnnotation> annotations) {
        Boolean foundSomething = annotations.find {BardAnnotation annotation ->
            annotation.otherAnnotations.find() //List<Comp>
        }

        return foundSomething ?: false
    }
}
