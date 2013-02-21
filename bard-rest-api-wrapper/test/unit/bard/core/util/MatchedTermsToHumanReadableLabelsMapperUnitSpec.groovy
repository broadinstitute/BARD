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
        final String mappedTerm = MatchedTermsToHumanReadableLabelsMapper.matchTermsToHumanReadableLabels(term, clazz)

        then:
        assert mappedTerm == expectedMappedTerm

        where:
        label                                  | term            | clazz                 | expectedMappedTerm
        "assay existing term"                  | "assay_type"    | AssayAdapter.class    | 'Assay Type'
        "assay non-mapped term (null value)"   | "av_dict_label" | AssayAdapter.class    | "No mapping found for term: 'av_dict_label'"
        "assay not-in-map term"                | "myTerm"        | AssayAdapter.class    | 'myTerm'
        "assay empty-string term"              | ""              | AssayAdapter.class    | ''
        "assay null term"                      | null            | AssayAdapter.class    | null
        "compound existing term"               | "compoundClass" | CompoundAdapter.class | 'Compound Class'
        "compound not-in-map term"             | "myTerm"        | CompoundAdapter.class | 'myTerm'
        "compound empty-string term"           | ""              | CompoundAdapter.class | ''
        "compound null term"                   | null            | CompoundAdapter.class | null
        "project existing term"                | "capProjectId"  | ProjectAdapter.class  | 'Project ID'
        "project non-mapped term (null value)" | "av_dict_label" | ProjectAdapter.class  | "No mapping found for term: 'av_dict_label'"
        "project not-in-map term"              | "myTerm"        | ProjectAdapter.class  | 'myTerm'
        "project empty-string term"            | ""              | ProjectAdapter.class  | ''
        "project null term"                    | null            | ProjectAdapter.class  | null
    }
}

