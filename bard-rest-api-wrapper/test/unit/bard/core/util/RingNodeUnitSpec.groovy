package bard.core.util

import bard.core.rest.spring.util.RingNode
import spock.lang.Specification
import spock.lang.Unroll

/**
 * See the API for {@link grails.test.mixin.support.GrailsUnitTestMixin} for usage instructions
 */
//@TestFor(RingNode)
@Unroll
class RingNodeUnitSpec extends Specification{


    void setup() {
     }

    void tearDown() {
    }


    void "Test RingNode constructors"() {
        given:
        RingNode ringNodeA = new RingNode("helicase")
        RingNode ringNodeB = new RingNode("helicase",1500)
        RingNode ringNodeC = new RingNode( "nucleic acid binding",
                "PC00171",
                "A molecule that binds a nucleic acid. It can be an enzyme or a binding protein.",
                "1.09.00.00.00",
                "panther")
        RingNode ringNodeD = new RingNode( "nucleic acid binding",
                "PC00171",
                "A molecule that binds a nucleic acid. It can be an enzyme or a binding protein.",
                "1.09.00.00.00",
                "panther",1500)


        when:
        RingNode ringNodeE = new RingNode( "DNA helicase",
                                           [ringNodeA,ringNodeB,ringNodeC,ringNodeD] )

        then:
        ringNodeA.toString().contains("helicase")
        (!ringNodeA.toStringNoText().contains("helicase"))
        ringNodeA.toString().find(/helicase[^\n]+/).find(/size\":\d/).find(/\d/) == '1'
        ringNodeA.toStringNoText().find(/name[^\n]+/).find(/size\":\d/).find(/\d/) == '1'

        ringNodeC.toString().find(/nucleic acid binding[^\n]+/).find(/size\":\d/).find(/\d/) == '1'
        ringNodeC.toStringNoText().find(/name[^\n]+/).find(/size\":\d/).find(/\d/) == '1'
        (!ringNodeC.toStringNoText().contains(/nucleic acid binding/))

        ringNodeE.toString().find(/nucleic acid[^\n]+/).contains(/size/)
        ringNodeE.toString().find(/DNA helicase[^\n]+/).find(/children/) == 'children'
        (!ringNodeE.toStringNoText().contains(/DNA helicase/))
         assert ringNodeE.maximumTreeHeight() == 1
    }



    void "Test hashcode and demonstrate that it gives us a nice spread"() {
        when:
        List<RingNode> ringNodeList = []
        for (i in 1..5) {
            for (j in 1..5) {
                for (k in 1..5) {
                    for (l in 1..5) {
                        ringNodeList << new RingNode( i.toString(),
                                j.toString(),
                                k.toString(),
                                l.toString(),
                                "source" )
                    }
                 }
            }
        }
        Map<Integer, Integer> ringNodeLinkedHashMap = [:]
        for (RingNode ringNode in ringNodeList) {
            int hashCode = ringNode.hashCode()
            if (ringNodeLinkedHashMap.containsKey(hashCode)) {
                ringNodeLinkedHashMap[hashCode] = ringNodeLinkedHashMap[hashCode] + 1
            } else {
                ringNodeLinkedHashMap[hashCode] = 0

            }
        }

        then:
        for (Integer key in ringNodeLinkedHashMap.keySet()) {
            assert ringNodeLinkedHashMap[key] < 1
        }
    }



    void "Test hashcode with degenerate input"() {
        when:
        RingNode ringNode  =  new RingNode( name, identifier, description, levelIdentifier, "source")

        then:
        ringNode.hashCode() != null

        where:
        name            |   identifier          |   description         |   levelIdentifier
        "test name"     |  "test identifier"    |  "test description"   |   "test levelIdentifier"
        null            |  "test identifier"    |  "test description"   |   "test levelIdentifier"
        "test name"     |  null                 |  "test description"   |   "test levelIdentifier"
        "test name"     |  "test identifier"    |  null                 |   "test levelIdentifier"
        "test name"     |  "test identifier"    |  "test description"   |   null
        null            |  null                 |  "test description"   |   "test levelIdentifier"
        null            |  "test identifier"    |  null                 |   "test levelIdentifier"
        null            |  "test identifier"    |  "test description"   |   null
        null            |  "test identifier"    |  "test description"   |   "test levelIdentifier"
        "test name"     |  null                 |  null                 |   "test levelIdentifier"
        "test name"     |  null                 |  "test description"   |   null
        "test name"     |  "test identifier"    |  null                 |   null
        null            |  null                 |  null                 |   "test levelIdentifier"
        null            |  null                 |  "test description"   |   null
        "test name"     |  null                 |  null                 |   null
        "test name"     |  null                 |  null                 |   null
        null            |  null                 |  null                 |   null
    }




    void "Test RingNode equals to see that it does what we expect"() {
        given:
        RingNode ringNodeA = new RingNode( "test name 1",
                "test identifier 1",
                "test description 1",
                "test levelIdentifier 1", "src" )

        when:
        RingNode ringNodeB = new RingNode( name,
                                           identifier,
                                           description,
                                           levelIdentifier, "src" )

        then:
        assert ringNodeA.equals(ringNodeB) == expectedResult
        assert ringNodeB.equals(ringNodeA) == expectedResult

        where:
        name            |   identifier          |   description         |   levelIdentifier         |   expectedResult
        "test name 2"   |  "test identifier 2"  |  "test description 2" |   "test levelIdentifier 2"|   false
        "test name 2"   |  "test identifier 2"  |  "test description 2" |   "test levelIdentifier 1"|   false
        "test name 2"   |  "test identifier 2"  |  "test description 1" |   "test levelIdentifier 2"|   false
        "test name 2"   |  "test identifier 2"  |  "test description 1" |   "test levelIdentifier 1"|   false
        "test name 2"   |  "test identifier 1"  |  "test description 2" |   "test levelIdentifier 2"|   false
        "test name 2"   |  "test identifier 1"  |  "test description 2" |   "test levelIdentifier 1"|   false
        "test name 2"   |  "test identifier 1"  |  "test description 1" |   "test levelIdentifier 2"|   false
        "test name 2"   |  "test identifier 1"  |  "test description 1" |   "test levelIdentifier 1"|   false
        "test name 1"   |  "test identifier 2"  |  "test description 2" |   "test levelIdentifier 2"|   false
        "test name 1"   |  "test identifier 2"  |  "test description 2" |   "test levelIdentifier 1"|   false
        "test name 1"   |  "test identifier 2"  |  "test description 1" |   "test levelIdentifier 2"|   false
        "test name 1"   |  "test identifier 2"  |  "test description 1" |   "test levelIdentifier 1"|   false
        "test name 1"   |  "test identifier 1"  |  "test description 2" |   "test levelIdentifier 2"|   false
        "test name 1"   |  "test identifier 1"  |  "test description 2" |   "test levelIdentifier 1"|   false
        "test name 1"   |  "test identifier 1"  |  "test description 1" |   "test levelIdentifier 2"|   false
        "test name 1"   |  "test identifier 1"  |  "test description 1" |   "test levelIdentifier 1"|   true
    }



    void "Test listOfEverybodyWhoIsAParent"() {
        given:
        RingNode ringNodeA = new RingNode("A")
        RingNode ringNodeB = new RingNode("B",
                 [new RingNode("C")]);


        when:
        RingNode ringNodeD = new RingNode("D",
                [ringNodeA,ringNodeB]);

        then:
        List<String> parentList =  ringNodeD.listOfEverybodyWhoIsAParent()
        parentList.size()==2
        parentList.contains("B")
        parentList.contains("D")
    }

    void "Test maximumTreeHeight"() {
        given:
        RingNode ringNodeA = new RingNode("A")
        RingNode ringNodeB = new RingNode("B",
                [new RingNode("C")]);


        when:
        RingNode ringNodeD = new RingNode("D",
                [ringNodeA,ringNodeB]);

        then:
        int maximumTreeHeight =  ringNodeD.maximumTreeHeight ( )
        maximumTreeHeight==2
    }



    void "test writeHierarchyPath" (){
        given:
        Map<String, RingNode> ringNodeMgr =  [:]
        ringNodeMgr["1."] = new RingNode("\\", "0", "root", "1", "none")
        ringNodeMgr["1.01"] = new RingNode("nameA", "idA", "descriptionA", "levelIdentifierA","sourceA")
        ringNodeMgr["1.02"] = new RingNode("nameB", "idB", "descriptionB", "levelIdentifierB","sourceA")
        ringNodeMgr["1.01.77"] = new RingNode("nameA1", "idA1", "descriptionA1", "levelIdentifierA1","sourceA")
        ringNodeMgr["1."].children <<  ringNodeMgr["1.01"]
        ringNodeMgr["1."].children <<  ringNodeMgr["1.02"]
        ringNodeMgr["1.01"].children <<  ringNodeMgr["1.01.77"]

        when:
        String hierarchyPathA =  ringNodeMgr["1."].writeHierarchyPath(ringNodeMgr)
        String hierarchyPathB =  ringNodeMgr["1.01"].writeHierarchyPath(ringNodeMgr)
        String hierarchyPathC =  ringNodeMgr["1.01.77"].writeHierarchyPath(ringNodeMgr)
        String hierarchyPathD =  ringNodeMgr["1.02"].writeHierarchyPath(ringNodeMgr)

        then:
        hierarchyPathA.equals("\\")
        hierarchyPathB.equals("\\nameA\\")
        hierarchyPathC.equals("\\nameA\\nameA1\\")
        hierarchyPathD.equals("\\nameB\\")

    }


    void "test the core of the toString functionality for a Ringnode" (){
        when:
        RingNode ringNode = RingNode.createStubRing ()

        then:
        ringNode.toString().contains("AA")
        (!ringNode.toStringNoText().contains("AA"))
        ringNode.toString().find(/FLINA[^\n]+/).find(/size\":\d/).find(/\d/) == '1'
        ringNode.toStringNoText().find(/FLINA[^\n]+/) == null
        ringNode.toString().find(/FLINA[^\n]+/).find(/ac\":\"\d/).find(/\d/) == '0'
        ringNode.toString().find(/FLINA[^\n]+/).find(/inac\":\"\d/).find(/\d/) == '0'


    }


    void "test placeSunburstOnPage" (){
        when:
        RingNode ringNode = RingNode.createStubRing ()

        then:
        ringNode.placeSunburstOnPage(10,10,["A","B"],2,47).contains('createASunburst')
        ringNode.placeSunburstOnPage(10,10,["A","B"],2,47).contains('continuousColorScale')
        ringNode.placeSunburstOnPage(10,10,["A","B"],2,47).contains('sunburstdiv')
    }


    void "test corner case for numberOfLevels"() {
        when:
        RingNode ringNode = new RingNode("name",
                "ID",
                "description",
                null,
                "source")

        then:
        ringNode.numberOfLevels() == 0
    }



    void "test deriveColors" (){
        when:
        RingNode ringNode = RingNode.createStubRing ()

        then:
        ringNode.deriveColors(["A","B"],2).trim()=="\"A\",\n\"B\"".toString()
    }

    void "test determineColorMappingRange " (){
        given:
        Map<String, RingNode> ringNodeMgr =  [:]
        ringNodeMgr["1."] = new RingNode("\\", "0", "root", "1", "none")
        ringNodeMgr["1."].actives = ['active1','active2','active3']
        ringNodeMgr["1."].inactives = ['inactive1']
        ringNodeMgr["1.01"] = new RingNode("nameA", "idA", "descriptionA", "levelIdentifierA","sourceA")
        ringNodeMgr["1.01"].actives = ['active1']
        ringNodeMgr["1.01"].inactives = ['inactive1','inactive2']
        ringNodeMgr["1.02"] = new RingNode("nameB", "idB", "descriptionB", "levelIdentifierB","sourceA")
        ringNodeMgr["1.02"].actives = ['active1']
        ringNodeMgr["1.02"].inactives = ['inactive1','inactive2','inactive3']
        // degenerate case -- no actives or inactivates. This is bad data, but we need to respond sensibly
        ringNodeMgr["1.01.77"] = new RingNode("nameA1", "idA1", "descriptionA1", "levelIdentifierA1","sourceA")
        ringNodeMgr["1."].children <<  ringNodeMgr["1.01"]
        ringNodeMgr["1."].children <<  ringNodeMgr["1.02"]
        ringNodeMgr["1.01"].children <<  ringNodeMgr["1.01.77"]

        when:
        LinkedHashMap<String, String>  determineColorMappingRangeA  =  ringNodeMgr["1."].determineColorMappingRange ()
        LinkedHashMap<String, String>  determineColorMappingRangeB   =  ringNodeMgr["1.01"].determineColorMappingRange ()
        LinkedHashMap<String, String>  determineColorMappingRangeC   =  ringNodeMgr["1.01.77"].determineColorMappingRange ()

        then:
        determineColorMappingRangeA["minimumValue"].contains('.33')
        determineColorMappingRangeA["maximumValue"].contains('.75')
        determineColorMappingRangeB["minimumValue"].contains('.33')
        determineColorMappingRangeB["maximumValue"].contains('.33')
        determineColorMappingRangeC["minimumValue"].contains('0')
        determineColorMappingRangeC["maximumValue"].contains('0')
    }


}