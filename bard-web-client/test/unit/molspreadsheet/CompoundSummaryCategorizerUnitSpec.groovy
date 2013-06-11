package molspreadsheet

import grails.test.mixin.TestMixin
import grails.test.mixin.support.GrailsUnitTestMixin
import spock.lang.Specification
import spock.lang.Unroll

/**
 * Created with IntelliJ IDEA.
 * User: balexand
 * Date: 6/11/13
 * Time: 8:35 AM
 * To change this template use File | Settings | File Templates.
 */
@TestMixin(GrailsUnitTestMixin)
@Unroll
class CompoundSummaryCategorizerUnitSpec  extends Specification {
    void setup() {

    }

    void tearDown() {
        // Tear down logic here
    }


    void "test deriveAssayFormatsIndex"() {
        given:
        CompoundSummaryCategorizer compoundSummaryCategorizer = new  CompoundSummaryCategorizer ()

        when:
        int index1 = compoundSummaryCategorizer.deriveAssayFormatIndex("cell-based format")
        int index2 = compoundSummaryCategorizer.deriveAssayFormatIndex("biochemical format")
        int index3 = compoundSummaryCategorizer.deriveAssayFormatIndex("biochemical format")
        int index4 = compoundSummaryCategorizer.deriveAssayFormatIndex("single protein format")

        then: "Should equal the expected"
        assert ! (compoundSummaryCategorizer == null)
        assert index1 == 0
        assert index2 == 1
        assert index3 == index2
        assert index4 == 2
    }



    void "test deriveAssayTypeIndex"() {
        given:
        CompoundSummaryCategorizer compoundSummaryCategorizer = new  CompoundSummaryCategorizer ()

        when:
        int index1 = compoundSummaryCategorizer.deriveAssayTypeIndex("cell-based format")
        int index2 = compoundSummaryCategorizer.deriveAssayTypeIndex("biochemical format")
        int index3 = compoundSummaryCategorizer.deriveAssayTypeIndex("biochemical format")
        int index4 = compoundSummaryCategorizer.deriveAssayTypeIndex("single protein format")

        then: "Should equal the expected"
        assert ! (compoundSummaryCategorizer == null)
        assert index1 == 0
        assert index2 == 1
        assert index3 == index2
        assert index4 == 2
    }



    void "test deriveBiologicalProcessIndex"() {
        given:
        CompoundSummaryCategorizer compoundSummaryCategorizer = new  CompoundSummaryCategorizer ()

        when:
        int index1 = compoundSummaryCategorizer.deriveBiologicalProcessIndex("cell-based format")
        int index2 = compoundSummaryCategorizer.deriveBiologicalProcessIndex("biochemical format")
        int index3 = compoundSummaryCategorizer.deriveBiologicalProcessIndex("biochemical format")
        int index4 = compoundSummaryCategorizer.deriveBiologicalProcessIndex("single protein format")

        then: "Should equal the expected"
        assert ! (compoundSummaryCategorizer == null)
        assert index1 == 0
        assert index2 == 1
        assert index3 == index2
        assert index4 == 2
    }



    void "test deriveProteinTargetIndex"() {
        given:
        CompoundSummaryCategorizer compoundSummaryCategorizer = new  CompoundSummaryCategorizer ()

        when:
        int index1 = compoundSummaryCategorizer.deriveProteinTargetIndex("cell-based format")
        int index2 = compoundSummaryCategorizer.deriveProteinTargetIndex("biochemical format")
        int index3 = compoundSummaryCategorizer.deriveProteinTargetIndex("biochemical format")
        int index4 = compoundSummaryCategorizer.deriveProteinTargetIndex("single protein format")

        then: "Should equal the expected"
        assert ! (compoundSummaryCategorizer == null)
        assert index1 == 0
        assert index2 == 1
        assert index3 == index2
        assert index4 == 2
    }




    void "test addNewRecord"() {
        given:
        CompoundSummaryCategorizer compoundSummaryCategorizer = new  CompoundSummaryCategorizer ()

        when:
        compoundSummaryCategorizer.addNewRecord(123L,"cell-based format","transporter assay")
        compoundSummaryCategorizer.addNewRecord(456L,"biochemical format","protein-small molecule interaction assay")
        compoundSummaryCategorizer.addNewRecord(666L,"biochemical format","protein-small molecule interaction assay")
        compoundSummaryCategorizer.addNewRecord(667L,"biochemical format","transporter assay")
        compoundSummaryCategorizer.addNewRecord(789L,"single protein format","direct enzyme activity assay")
        compoundSummaryCategorizer.updateOutcome(123L,2,[26],[])
        compoundSummaryCategorizer.updateOutcome(456L,1,[],[])
        compoundSummaryCategorizer.updateOutcome(666L,1,[146, 145],[])
        compoundSummaryCategorizer.updateOutcome(667L,2,[],[326, 325])
        compoundSummaryCategorizer.updateOutcome(789L,2,[326],[])
        compoundSummaryCategorizer.combineInNewProteinTargetValue(123L,"Transporter")
        compoundSummaryCategorizer.combineInNewBiologicalProcessValue(666L,"Potassium channel")
        compoundSummaryCategorizer.combineInNewProteinTargetValue(666L,"Ion channel")
        compoundSummaryCategorizer.combineInNewBiologicalProcessValue(667L,"G protein receptor")
        compoundSummaryCategorizer.combineInNewBiologicalProcessValue(667L,"G protein")
        compoundSummaryCategorizer.combineInNewProteinTargetValue(789L,"Kinase")

        then: "Should equal the expected"
        assert ! (compoundSummaryCategorizer == null)
     }






}
