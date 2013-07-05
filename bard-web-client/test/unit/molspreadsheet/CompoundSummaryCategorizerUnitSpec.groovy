package molspreadsheet

import bard.core.rest.spring.util.RingNode
import grails.test.mixin.TestMixin
import grails.test.mixin.support.GrailsUnitTestMixin
import groovy.json.JsonSlurper
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

    private  String  proteinTargetTree
    private  String  assayFormatTree
    private  String  assayTypeTree


    void setup() {
        proteinTargetTree = """
 [{"name":"/", "children": [
        {"name":"signaling molecule", "assays": [0] },
        {"name":"enzyme modulator", "assays": [2,4], "children": [
            {"name":"G-protein modulator", "assays": [7]},
            {"name":"G-protein", "assays": [5], "children": [
                {"name":"heterotrimeric G-protein", "assays": [1]},
                {"name":"small GTPase", "assays": [3,6]}
            ]}
        ]},
        {"name":"transporter", "assays": [], "children": [
            {"name":"ATP-binding cassette", "assays": [12,13]},
            {"name":"ion channel", "assays": [11], "children": [
                {"name":"anion channel", "assays": [10,14]},
                {"name":"voltage-gated ion channel", "assays": [9], "children": [
                    {"name":"voltage-gated potassium channel", "assays": [8,15]}
                ]}
            ]}
        ]}
]}]"""


        assayFormatTree = """
 [{"name":"/", "children": [
        {"name":"cell-based format", "assays": [0,2,4,6,10, 11, 12, 13, 14] },
        {"name":"cell-free format", "assays": [], "children": [
            {"name":"whole-cell lysate format", "assays": [3]}
        ]},
        {"name":"biochemical format", "assays": [5, 7, 9], "children": [
            {"name":"protein format", "assays": [], "children": [
                {"name":"single protein format", "assays": [1, 8, 15]}
            ]}
        ]}
]}]"""

        assayTypeTree = """
 [{"name":"/", "children": [
        {"name":"transporter assay", "assays": [0, 5] },
        {"name":"viability assay", "assays": [1, 2, 4], "children": [
            {"name":"cytotoxicity assay", "assays": [3]}
        ]}
]}]"""



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
        compoundSummaryCategorizer.addNewRecord(123L,"cell-based format","transporter assay",null,null)
        compoundSummaryCategorizer.addNewRecord(456L,"biochemical format","protein-small molecule interaction assay",null,null)
        compoundSummaryCategorizer.addNewRecord(666L,"biochemical format","protein-small molecule interaction assay",null,null)
        compoundSummaryCategorizer.addNewRecord(667L,"biochemical format","transporter assay",null,null)
        compoundSummaryCategorizer.addNewRecord(789L,"single protein format","direct enzyme activity assay",null,null)
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
        String json = compoundSummaryCategorizer.toString()

        then: "Should equal the expected"
        assert ! (compoundSummaryCategorizer == null)
        assert ! (json == null)

     }



    /***
     *   Test out basic functionality for linkedVisHierDataFactory
     */
    void "test linkedVisHierDataFactory"() {
        given:
        CompoundSummaryCategorizer compoundSummaryCategorizer = new  CompoundSummaryCategorizer ()

        when:
        LinkedVisHierData linkedVisHierData = compoundSummaryCategorizer.linkedVisHierDataFactory( RingNode.createStubRing(),
                                                                                                   RingNode.createStubRing(),
                                                                                                   RingNode.createStubRing() )
        String linkedHierarchyJson = linkedVisHierData.createCombinedListing()

        then: "Should equal the expected"
        assert ! (linkedVisHierData == null)
        assert ! (linkedHierarchyJson == null)
        def userJson = new JsonSlurper().parseText(linkedHierarchyJson )
        assert  userJson.getClass().name == 'java.util.HashMap'
    }

//    /***
//     * test out corner case in backPopulateTargetNames
//     */
//    void "test backPopulateTargetNames"() {
//        given:
//        CompoundSummaryCategorizer compoundSummaryCategorizer = new  CompoundSummaryCategorizer ()
//
//        when:
//        compoundSummaryCategorizer.totalContents = null
//        LinkedHashMap<Long, String> mapBiologyIdToProteinAscensionNumber = [:]
//        LinkedHashMap<String, String> mapAccessionNumberToTargetClassName = [:]
//        compoundSummaryCategorizer.backPopulateTargetNames(mapBiologyIdToProteinAscensionNumber, mapAccessionNumberToTargetClassName)
//
//        then: "Should equal the expected"
//        assert (mapBiologyIdToProteinAscensionNumber.size() == 0)
//        assert (mapAccessionNumberToTargetClassName.size() == 0)
//    }


    void "test addNewRecord error-checking"() {
        given:
        CompoundSummaryCategorizer compoundSummaryCategorizer = new  CompoundSummaryCategorizer ()

        when:
        compoundSummaryCategorizer.addNewRecord(7L,"", "", "", "")
        compoundSummaryCategorizer.addNewRecord(7L,"", "", "", "")

        then: "Should equal the expected"
        assert (compoundSummaryCategorizer.totalContents.size() == 1)
    }


    void "test combineInNewProteinTargetValue  error-checking"() {
        given:
        CompoundSummaryCategorizer compoundSummaryCategorizer = new  CompoundSummaryCategorizer ()

        when:
        compoundSummaryCategorizer.addNewRecord(123L,"cell-based format","transporter assay",null,null)
        compoundSummaryCategorizer.addNewRecord(456L,"biochemical format","protein-small molecule interaction assay",null,null)
        compoundSummaryCategorizer.addNewRecord(666L,"biochemical format","protein-small molecule interaction assay",null,null)
        compoundSummaryCategorizer.addNewRecord(667L,"biochemical format","transporter assay",null,null)
        compoundSummaryCategorizer.addNewRecord(789L,"single protein format","direct enzyme activity assay",null,null)

        compoundSummaryCategorizer.combineInNewProteinTargetValue (7L,"")  // err1 -- unknown eid
        compoundSummaryCategorizer.combineInNewProteinTargetValue (123L,"")

        then: "Should equal the expected"
        assert (compoundSummaryCategorizer.totalContents.size() == 5)
    }




    void "test combineInNewBiologicalProcessValue   error-checking"() {
        given:
        CompoundSummaryCategorizer compoundSummaryCategorizer = new  CompoundSummaryCategorizer ()

        when:
        compoundSummaryCategorizer.addNewRecord(123L,"cell-based format","transporter assay",null,null)
        compoundSummaryCategorizer.addNewRecord(456L,"biochemical format","protein-small molecule interaction assay",null,null)
        compoundSummaryCategorizer.addNewRecord(666L,"biochemical format","protein-small molecule interaction assay",null,null)
        compoundSummaryCategorizer.addNewRecord(667L,"biochemical format","transporter assay",null,null)
        compoundSummaryCategorizer.addNewRecord(789L,"single protein format","direct enzyme activity assay",null,null)

        compoundSummaryCategorizer.combineInNewBiologicalProcessValue  (7L,"")  // err1 -- unknown eid
        compoundSummaryCategorizer.combineInNewBiologicalProcessValue  (123L,"")

        then: "Should equal the expected"
        assert (compoundSummaryCategorizer.totalContents.size() == 5)
    }

    /***
     * another corner case
     */
    void "test updateOutcome    error-checking"() {
        given:
        CompoundSummaryCategorizer compoundSummaryCategorizer = new  CompoundSummaryCategorizer ()

        when:
        compoundSummaryCategorizer.addNewRecord(123L,"cell-based format","transporter assay",null,null)
        compoundSummaryCategorizer.addNewRecord(456L,"biochemical format","protein-small molecule interaction assay",null,null)
        compoundSummaryCategorizer.addNewRecord(666L,"biochemical format","protein-small molecule interaction assay",null,null)
        compoundSummaryCategorizer.addNewRecord(667L,"biochemical format","transporter assay",null,null)
        compoundSummaryCategorizer.addNewRecord(789L,"single protein format","direct enzyme activity assay",null,null)

        compoundSummaryCategorizer.updateOutcome   (7L,1,["2"] as ArrayList,["4"] as ArrayList)
        compoundSummaryCategorizer.updateOutcome   (123L,1,["3"] as ArrayList,["4"] as ArrayList)

        then: "Should equal the expected"
        assert (compoundSummaryCategorizer.totalContents.size() == 5)
    }

    /***
     * This method creates the Json for the main assay definition data portion of the linked visualization Json, so it is
     * important that this code function
     */
    void "test createLinkedDataAssaySection "() {
        given:
        CompoundSummaryCategorizer compoundSummaryCategorizer = new  CompoundSummaryCategorizer ()

        when:
        compoundSummaryCategorizer.addNewRecord(123L,"cell-based format","transporter assay",null,null)
        String linkedDataAssay  = compoundSummaryCategorizer.createLinkedDataAssaySection()

        then: "Should give us some output"
        def userJson = new JsonSlurper().parseText(linkedDataAssay )
        assert  userJson.getClass().name == 'java.util.ArrayList'
    }

    /***
     * This method creates the Json for the main assay cross-linking data portion of the linked visualization Json, so it is
     * important that this code function
     */
    void "test createLinkedDataCrossAssaySection  "() {
        given:
        CompoundSummaryCategorizer compoundSummaryCategorizer = new  CompoundSummaryCategorizer ()

        when:
        compoundSummaryCategorizer.addNewRecord(123L,"cell-based format","transporter assay",null,null)
        String linkedDataAssay  = compoundSummaryCategorizer.createLinkedDataCrossAssaySection ()

        then: "Should give us some output"
        def userJson = new JsonSlurper().parseText(linkedDataAssay )
        assert  userJson.getClass().name == 'java.util.ArrayList'
    }



    void "test treeAssayLinker "() {
        given:
        CompoundSummaryCategorizer compoundSummaryCategorizer = new  CompoundSummaryCategorizer ()

            RingNode ringNode1 = new RingNode ("B",[ new RingNode ("A",  1500),
                    new RingNode ("ABC",  500),
                    new RingNode ("C",  500) ] )
            RingNode ringNode2 = new RingNode ("accession2",[ new RingNode ("ABC",  1500),
                    ringNode1,
                    new RingNode ("C",  50) ] )
        RingNode ringNode3 =  new RingNode ("/",[ new RingNode ("accession1",  1500),
                    ringNode1,
                    new RingNode ("FLINC",  1500),
                    ringNode2] )


        when:
        compoundSummaryCategorizer.addNewRecord(123L,"cell-based format","transporter assay",null,null)
        compoundSummaryCategorizer.addNewRecord(456L,"biochemical format","protein-small molecule interaction assay",null,null)
        compoundSummaryCategorizer.addNewRecord(666L,"biochemical format","protein-small molecule interaction assay",null,null)

        compoundSummaryCategorizer.updateOutcome(123L,2,["4"],["5","6"])
        LinkedHashMap<Long, String> mapBiologyIdToProteinAscensionNumber = [:]
        mapBiologyIdToProteinAscensionNumber [4L] = "accession1"
        mapBiologyIdToProteinAscensionNumber [5L] = "accession2"
        mapBiologyIdToProteinAscensionNumber [6L] = "accession3"
        LinkedHashMap<String, String> mapAccessionNumberToTargetClassName = [:]
        mapAccessionNumberToTargetClassName ["accession1"] = "accession1"
        mapAccessionNumberToTargetClassName ["accession2"] = "accession2"
        mapAccessionNumberToTargetClassName ["accession3"] = "accession3"
        compoundSummaryCategorizer.backPopulateTargetNames( mapBiologyIdToProteinAscensionNumber,
                                                            mapAccessionNumberToTargetClassName )
        compoundSummaryCategorizer.treeAssayLinker(ringNode3)

        then: "Should give us some output"
        ringNode3.assays.size()==0
        for (RingNode ringNode in ringNode3.children){
           if (ringNode.name=="accession1") {
               assert (ringNode.assays.size() == 1)
           }  else {
               assert (ringNode.assays.size() == 0)
           }
        }

    }



    void "test treeAssayFormatLinker "() {
        given:
        CompoundSummaryCategorizer compoundSummaryCategorizer = new  CompoundSummaryCategorizer ()

        RingNode ringNode1 = new RingNode ("cell-based format",[ new RingNode ("A",  1500),
                new RingNode ("biochemical format",  500),
                new RingNode ("C",  500) ] )
        RingNode ringNode2 = new RingNode ("accession2",[ new RingNode ("ABC",  1500),
                ringNode1,
                new RingNode ("C",  50) ] )
        RingNode ringNode3 =  new RingNode ("/",[ new RingNode ("cell-based format",  1500),
                ringNode1,
                new RingNode ("biochemical format",  1500),
                ringNode2] )


        when:
        compoundSummaryCategorizer.addNewRecord(123L,"cell-based format","transporter assay",null,null)
        compoundSummaryCategorizer.addNewRecord(456L,"biochemical format","protein-small molecule interaction assay",null,null)
        compoundSummaryCategorizer.addNewRecord(666L,"biochemical format","protein-small molecule interaction assay",null,null)

        compoundSummaryCategorizer.updateOutcome(123L,2,["4"],["5","6"])
        compoundSummaryCategorizer.treeAssayFormatLinker(ringNode3)

        then: "Should give us some output"
        ringNode3.assays.size()==0
        for (RingNode ringNode in ringNode3.children){
            if (ringNode.name=="cell-based format") {
                assert (ringNode.assays.size() == 1)
            }  else if (ringNode.name=="biochemical format"){
                assert (ringNode.assays.size() == 2)
            }
        }

    }



    void "test treeAssayTypeLinker "() {
        given:
        CompoundSummaryCategorizer compoundSummaryCategorizer = new  CompoundSummaryCategorizer ()

        RingNode ringNode1 = new RingNode ("cell-based format",[ new RingNode ("A",  1500),
                new RingNode ("transporter assay",  500),
                new RingNode ("C",  500) ] )
        RingNode ringNode2 = new RingNode ("accession2",[ new RingNode ("ABC",  1500),
                ringNode1,
                new RingNode ("C",  50) ] )
        RingNode ringNode3 =  new RingNode ("/",[ new RingNode ("protein-small molecule interaction assay",  1500),
                ringNode1,
                new RingNode ("biochemical format",  1500),
                ringNode2] )


        when:
        compoundSummaryCategorizer.addNewRecord(123L,"cell-based format","transporter assay",null,null)
        compoundSummaryCategorizer.addNewRecord(456L,"biochemical format","protein-small molecule interaction assay",null,null)
        compoundSummaryCategorizer.addNewRecord(666L,"biochemical format","protein-small molecule interaction assay",null,null)

        compoundSummaryCategorizer.updateOutcome(123L,2,["4"],["5","6"])
        compoundSummaryCategorizer.treeAssayTypeLinker(ringNode3)

        then: "Should give us some output"
        ringNode3.assays.size()==0
        for (RingNode ringNode in ringNode3.children){
            if (ringNode.name=="protein-small molecule interaction assay") {
                assert (ringNode.assays.size() == 2)
            }  else {
                assert (ringNode.assays.size() == 0)
            }
        }

    }

    /***
     * corner case
     */
    void "test descendAndLink"() {
        given:
        RingNode nullRingNode = null
        CompoundSummaryCategorizer compoundSummaryCategorizer = new  CompoundSummaryCategorizer ()
        LinkedHashMap<String,List <Integer>> mapProteinClassNameToAssayIndex = [:]
        mapProteinClassNameToAssayIndex["abc"] = [1,2,3]
        mapProteinClassNameToAssayIndex["C"] = [4]

        RingNode ringNode1 = new RingNode ("/",[ new RingNode ("abc",  1500),
                new RingNode ("transporter assay",  500),
                new RingNode ("C",  500) ] )


        when:
        compoundSummaryCategorizer.descendAndLink (nullRingNode,mapProteinClassNameToAssayIndex)
        compoundSummaryCategorizer.descendAndLink (ringNode1,mapProteinClassNameToAssayIndex)

        then: "Should give us some output"
        ringNode1.assays.size()==0
        for (RingNode ringNode in ringNode1.children){
            if (ringNode.name=="abc") {
                assert (ringNode.assays.size() == 3)
            }  else if (ringNode.name=="C") {
                assert (ringNode.assays.size() == 1)
            }
        }

    }




}
