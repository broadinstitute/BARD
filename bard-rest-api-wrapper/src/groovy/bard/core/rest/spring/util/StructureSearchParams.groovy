package bard.core.rest.spring.util

import bard.core.SearchParams

/**
 * Created with IntelliJ IDEA.
 * User: jasiedu
 * Date: 11/16/12
 * Time: 4:29 PM
 * To change this template use File | Settings | File Templates.
 */
class StructureSearchParams extends SearchParams {
   // private static final long serialVersionUID = 0xaeddb6dac8a37ae8l;

    /*
     * Different search types supported at the moment
     */
    public enum Type {
        Substructure,
        Superstructure,
        Exact,
        Similarity;
    }

    /*
     * Actual search or we're only care about count
     */
    public enum Method {
        Search,
        Count
    }


    /*
     * Future we support different metrics
     */
    public enum Metric {
        Tanimoto, // similarity
        Dice, // similarity
        Cosine, // similarity
        Hamming; // distance
    }

    protected Type type = Type.Substructure;
    protected Method method = Method.Search;
    protected Metric metric = Metric.Tanimoto;
    protected Double threshold = 0.7; // for similarity

    public StructureSearchParams() {
       super()
    }
   public StructureSearchParams(final StructureSearchParams structureSearchParams){
       super()
       this.threshold = structureSearchParams.threshold
       this.query = structureSearchParams.query
       this.type = structureSearchParams.type
       this.skip = structureSearchParams.skip
       this.top = structureSearchParams.top
       this.method = structureSearchParams.method
       this.metric = structureSearchParams.metric
       this.filters = structureSearchParams.filters //filters are not cloned
   }
    public StructureSearchParams(String query) {
        this(query, Type.Substructure);
    }

    public StructureSearchParams(String query, Type type) {
        super(query);
        this.type = type;
    }

    public Type getType() {
        return type;
    }

    public StructureSearchParams setType(Type type) {
        this.type = type;
        return this;
    }

    public Method getMethod() {
        return method;
    }

    public StructureSearchParams setMethod(Method method) {
        this.method = method;
        return this;
    }

    public Metric getMetric() {
        return metric;
    }

    public StructureSearchParams setMetric(Metric metric) {
        this.metric = metric;
        return this;
    }

    public Double getThreshold() {
        return threshold;
    }

    public StructureSearchParams setThreshold(Double threshold) {
        this.threshold = threshold;
        return this;
    }
}
