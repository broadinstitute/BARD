package bard.core.adapter

import spock.lang.Specification
import spock.lang.Unroll
import bard.core.*

@Unroll
class EntityAdapterUnitSpec extends Specification {
    void setup() {

    }

    void tearDown() {
        // Tear down logic here
    }

    void "getSynonyms()"() {

        given:
        final String synonym = "synonym"
        Value v = new StringValue(new DataSource("name"), Compound.SynonymValue,
                synonym)
        Compound compound = new Compound("name")
        compound.addValue(v)
        CompoundAdapter<Compound> compoundEntityAdapter = new CompoundAdapter(compound)
        when: "We call the toString method"
        final List<String> synonyms = compoundEntityAdapter.getSynonyms()

        then:
        assert synonyms
        assert !synonyms.isEmpty()
        assert synonyms.get(0) == synonym
    }

    void "getSearchHighlight() #label"() {

        given:
        Value v = highlightValue
        Compound compound = new Compound("name")
        compound.addValue(v)
        CompoundAdapter<Compound> compoundEntityAdapter = new CompoundAdapter(compound)
        when:
        final String searchHighlight = compoundEntityAdapter.getSearchHighlight()

        then: "The expected string representation is displayed"
        assert searchHighlight == highlight
        where:
        label                 | highlightValue                                                                    | highlight
        "With Highlight"      | new StringValue(new DataSource("name"), Entity.SearchHighlightValue, "highlight") | "highlight"
        "With Null Highlight" | new StringValue(new DataSource("name"), Compound.PubChemSIDValue, "compound")     | null
    }


}

