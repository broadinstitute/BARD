package bard.core

import spock.lang.Specification
import spock.lang.Unroll
import bard.core.rest.spring.util.StructureSearchParams

@Unroll
class StructureSearchParamsUnitSpec extends Specification {


    void setup() {

    }

    void tearDown() {
        // Tear down logic here
    }

    void "test Constructors #label"() {
        when:
        final StructureSearchParams currentStructureSearchParams = structureSearchParams
        then:
        currentStructureSearchParams.type == structureSearchParams.type
        currentStructureSearchParams.query == structureSearchParams.query
        currentStructureSearchParams.threshold == 0.7
        currentStructureSearchParams.method == StructureSearchParams.Method.Search;
        currentStructureSearchParams.metric == StructureSearchParams.Metric.Tanimoto;

        where:
        label                    | structureSearchParams
        "Empty Constructor"      | new StructureSearchParams()
        "Single arg Constructor" | new StructureSearchParams("query")
        "Two arg Constructor"    | new StructureSearchParams("query", StructureSearchParams.Type.Exact)
    }

    void "test all setters #label"() {
        given:
        final StructureSearchParams currentStructureSearchParams = new StructureSearchParams()
        final StructureSearchParams.Method method = StructureSearchParams.Method.Count
        final StructureSearchParams.Metric metric = StructureSearchParams.Metric.Cosine
        final Double threshold = 2.0
        final StructureSearchParams.Type type = StructureSearchParams.Type.Similarity
        when:
        currentStructureSearchParams.setMethod(method)
        currentStructureSearchParams.setMetric(metric)
        currentStructureSearchParams.setThreshold(threshold)
        currentStructureSearchParams.setType(type)
        then:

        currentStructureSearchParams.threshold == threshold
        currentStructureSearchParams.method == method
        currentStructureSearchParams.metric == metric
        currentStructureSearchParams.type == type


    }


}

