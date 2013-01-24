package bard.core

import spock.lang.Specification
import spock.lang.Unroll
import bard.core.rest.spring.compounds.Scaffold
import bard.core.rest.spring.compounds.WarningLevel

@Unroll
class ScaffoldUnitSpec extends Specification {
    void setup() {

    }

    void tearDown() {
        // Tear down logic here
    }

    void "test Get WarningLevel #label"() {
        when: "We call the getWarningLevel() method on the given scaffold"
        final WarningLevel warningLevel = scaffold.warningLevel
        then: "The expected to get back the expected warning level"
        assert warningLevel
        warningLevel == expectedWarningLevel
        where:
        label                | scaffold                                                | expectedWarningLevel
        "With pScore=0"      | new Scaffold(scafsmi: "CC", pScore: new Double(0))      | WarningLevel.none
        "With pScore=99.99"  | new Scaffold(scafsmi: "CC", pScore: new Double(99.99))  | WarningLevel.none
        "With pScore=100"    | new Scaffold(scafsmi: "CC", pScore: new Double(100))    | WarningLevel.moderate
        "With pScore=299.99" | new Scaffold(scafsmi: "CC", pScore: new Double(299.99)) | WarningLevel.moderate
        "With pScore=300"    | new Scaffold(scafsmi: "CC", pScore: new Double(300))    | WarningLevel.severe
        "With pScore=20000"  | new Scaffold(scafsmi: "CC", pScore: new Double(20000))  | WarningLevel.severe
    }

    void "scaffold.hashCode #label"() {
        given: "A valid Scaffold"
        Scaffold scaffold =
            new Scaffold(scafsmi: "CC", pScore: new Double(0), sTested: 1, scafid: 1, sActive: 1,
                    aTested: 1, aActive: 1, wTested: 1, wActive: 1, inDrug: false)

        when: "We call the hashCode method"
        final int hashCode = scaffold.hashCode()
        then: "The expected hashCode is returned"
        assert hashCode
        hashCode == 371101637
    }

    void "scaffold.compareTo #label"() {
        given: "Valid Scafold Objects"
        Scaffold scaffold1 =
            new Scaffold(scafsmi: "CC", pScore: new Double(0), sTested: cTested1, scafid: 1, sActive: 1,
                    aTested: 1, aActive: 1, wTested: 1, wActive: 1, inDrug: false)
        Scaffold scaffold2 =
            new Scaffold(scafsmi: "CC", pScore: new Double(0), sTested: cTested2, scafid: 1, sActive: 1,
                    aTested: 1, aActive: 1, wTested: 1, wActive: 1, inDrug: false)
        when: "We call the compareTo method with objects"
        final int compareToVal = scaffold1.compareTo(scaffold2)
        then: "We expected the method to return the expected value"
        assert compareToVal == expectedAnswer
        where:
        label                 | cTested1 | cTested2 | expectedAnswer
        "cTested1==cTested2"  | 200      | 200      | 0
        "cTested1 > cTested2" | 201      | 200      | 1
        "cTested1 < cTested2" | 201      | 205      | -1
    }



    void "scaffold.equals #label"() {
        when: "We call the equals method with scaffold1 and scaffold2"
        final boolean returnedValue = scaffold1.equals(scaffold2)
        then: "We expected method to return the expected value"
        assert returnedValue == expectedAnswer
        where:
        label                      | scaffold1                  | scaffold2                  | expectedAnswer
        "this equals that"         | new Scaffold(sTested: 200) | new Scaffold(sTested: 200) | true
        "that is null"             | new Scaffold(sTested: 201) | null                       | false
        "this != that"             | new Scaffold(sTested: 201) | new Scaffold(sTested: 205) | false
        "this.class != that.class" | new Scaffold(sTested: 201) | 200                        | false
    }

}

