package bard.core.util


import grails.test.mixin.TestMixin
import grails.test.mixin.support.GrailsUnitTestMixin
import spock.lang.Specification
import spock.lang.Unroll
import bard.core.adapter.AssayAdapter
import bard.core.adapter.CompoundAdapter
import bard.core.adapter.ProjectAdapter

@TestMixin(GrailsUnitTestMixin)
@Unroll
class MatchedTermsToHumanReadableLabelsMapperUnitSpec extends Specification {
    void setup() {

    }

    void tearDown() {
        // Tear down logic here
    }

    void "test mapped terms #label"() {

        when:
        final String mappedTerm = MatchedTermsToHumanReadableLabelsMapper.matchTermsToHumanReadableLabels(term)

        then:
        assert mappedTerm == expectedMappedTerm

        where:
        label                                | term            | expectedMappedTerm
        "assay existing term"                | "assay_type"    | 'Assay Type'
        "assay not-in-map term"              | "myTerm"        | 'myTerm'
        "assay empty-string term"            | ""              | ''
        "assay null term"                    | null            | null
    }
}

