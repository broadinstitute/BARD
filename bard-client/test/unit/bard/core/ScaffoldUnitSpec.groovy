package bard.core

import spock.lang.Specification
import spock.lang.Unroll

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
        label                 | scaffold                                                 | expectedWarningLevel
        "With pScore=0"       | new Scaffold(scafsmi: "CC", pScore: new Double(0))       | WarningLevel.none
        "With pScore=3999.99" | new Scaffold(scafsmi: "CC", pScore: new Double(399.99))  | WarningLevel.none
        "With pScore=4000"    | new Scaffold(scafsmi: "CC", pScore: new Double(4000))    | WarningLevel.moderate
        "With pScore=9999.99" | new Scaffold(scafsmi: "CC", pScore: new Double(9999.99)) | WarningLevel.moderate
        "With pScore=10000"   | new Scaffold(scafsmi: "CC", pScore: new Double(10000))   | WarningLevel.severe
        "With pScore=20000"   | new Scaffold(scafsmi: "CC", pScore: new Double(20000))   | WarningLevel.severe
    }

    void "scaffold.hashCode #label"() {
        given: "A valid Scaffold"
        Scaffold scaffold =
            new Scaffold(scafsmi: "CC", pScore: new Double(0), cTested: 1, scafid: 1, cActive: 1,
                    aTested: 1, aActive: 1, sTested: 1, sActive: 1, inDrug: false)

        when: "We call the hashCode method"
        final int hashCode = scaffold.hashCode()
        then: "The expected hashCode is returned"
        assert hashCode
        hashCode == 371101637
    }

    void "scaffold.compareTo #label"() {
        given: "Valid Scafold Objects"
        Scaffold scaffold1 =
            new Scaffold(scafsmi: "CC", pScore: new Double(0), cTested: cTested1, scafid: 1, cActive: 1,
                    aTested: 1, aActive: 1, sTested: 1, sActive: 1, inDrug: false)
        Scaffold scaffold2 =
            new Scaffold(scafsmi: "CC", pScore: new Double(0), cTested: cTested2, scafid: 1, cActive: 1,
                    aTested: 1, aActive: 1, sTested: 1, sActive: 1, inDrug: false)
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
        "this equals that"         | new Scaffold(cTested: 200) | new Scaffold(cTested: 200) | true
        "that is null"             | new Scaffold(cTested: 201) | null                       | false
        "this != that"             | new Scaffold(cTested: 201) | new Scaffold(cTested: 205) | false
        "this.class != that.class" | new Scaffold(cTested: 201) | 200                        | false
    }

}

