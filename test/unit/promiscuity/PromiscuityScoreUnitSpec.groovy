package promiscuity

import spock.lang.Specification
import spock.lang.Unroll

@Unroll
class PromiscuityScoreUnitSpec extends Specification {
    void setup() {

    }

    void tearDown() {
        // Tear down logic here
    }

    void "promiscuityScore.toString #label"() {
        given: "A valid PromiscuityScore Object"

        PromiscuityScore promiscuityScore = new PromiscuityScore(cid: cid, scaffolds: scaffolds)
        when: "We call the toString method"
        final String promiscuityScoreString = promiscuityScore.toString()
        then: "The expected string representation is displayed"
        assert promiscuityScoreString
        promiscuityScoreString == expectedString
        where:
        label            | cid | scaffolds                     | expectedString
        "With Scaffolds" | 200 | [new Scaffold(scafsmi: "CC")] | "CID: 200\nCC"
        "No Scaffolds"   | 201 | null                          | "CID: 201"
    }

    void "promiscuityScore.hashCode #label"() {
        given: "A valid PromiscuityScore Object"

        PromiscuityScore promiscuityScore = new PromiscuityScore(cid: cid, scaffolds: scaffolds)
        when: "We call the hashCode method"
        final int hashCode = promiscuityScore.hashCode()
        then: "The expected hashCode is returned"
        assert hashCode
        hashCode == expectedHashCode
        where:
        label            | cid | scaffolds                     | expectedHashCode
        "With Scaffolds" | 200 | [new Scaffold(scafsmi: "CC")] | -481319079
        "No Scaffolds"   | 201 | null                          | 22568
    }

    void "promiscuityScore.compareTo #label"() {
        given: "A valid PromiscuityScore Object"
        PromiscuityScore promiscuityScore1 = new PromiscuityScore(cid: cid1, scaffolds: null)
        PromiscuityScore promiscuityScore2 = new PromiscuityScore(cid: cid2, scaffolds: null)
        when: "We call the compareTo method with promiscuityScore1 and promiscuityScore1"
        final int compareToVal = promiscuityScore1.compareTo(promiscuityScore2)
        then: "We expected the method to return the expected value"
        assert compareToVal == expectedAnswer
        where:
        label         | cid1 | cid2 | expectedAnswer
        "CID1==CID2"  | 200  | 200  | 0
        "CID1 > CID2" | 201  | 200  | 1
        "CID1 < CID2" | 201  | 205  | -1
    }


    void "promiscuityScore.equals #label"() {
        when: "We call the equals method with promiscuityScore1 and promiscuityScore1"
        final boolean returnedValue = promiscuityScore1 == promiscuityScore2
        then: "We expected method to return the expected value"
        assert returnedValue == expectedAnswer
        where:
        label                      | promiscuityScore1              | promiscuityScore2              | expectedAnswer
        "this equals that"         | new PromiscuityScore(cid: 200) | new PromiscuityScore(cid: 200) | true
        "that is null"             | new PromiscuityScore(cid: 201) | null                           | false
        "this != that"             | new PromiscuityScore(cid: 201) | new PromiscuityScore(cid: 205) | false
        "this.class != that.class" | new PromiscuityScore(cid: 201) | 200                            | false
    }
}

