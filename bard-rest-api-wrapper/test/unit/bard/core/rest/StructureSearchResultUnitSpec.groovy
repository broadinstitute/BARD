package bard.core.rest

import bard.core.rest.spring.util.StructureSearchParams
import spock.lang.Specification
import spock.lang.Unroll

@Unroll
class StructureSearchResultUnitSpec extends Specification {

    void setup() {

    }

    void tearDown() {
        // Tear down logic here
    }

    void "test createURL #label"() {
        given:
        StructureSearchResult structureSearchResult = new StructureSearchResult(null, null)
        when:
        final String url = structureSearchResult.createURL("resource", "query", structureType, 2.0)
        then:
        url == expectedURL
        where:
        label            | structureType                             | expectedURL
        "Exact Search"   | StructureSearchParams.Type.Exact          | "resource?filter=query[structure]&type=exact&expand=true"
        "Similarity"     | StructureSearchParams.Type.Similarity     | "resource?filter=query[structure]&type=sim&cutoff=2.000&expand=true"
        "Substructure"   | StructureSearchParams.Type.Substructure   | "resource?filter=query[structure]&type=sub&expand=true"
        "SuperStructure" | StructureSearchParams.Type.Superstructure | "resource?filter=query[structure]&type=sup&expand=true"
    }

    void "test createURL with Exceptions #label"() {
        given:
        StructureSearchResult structureSearchResult = new StructureSearchResult(null, null)
        when:
        structureSearchResult.createURL(resource, "query", structureType, threshold)
        then:
        thrown(IllegalArgumentException)
        where:
        label        | structureType                         | resource   | threshold
        "Similarity" | StructureSearchParams.Type.Similarity | "resource" | null
    }

}

