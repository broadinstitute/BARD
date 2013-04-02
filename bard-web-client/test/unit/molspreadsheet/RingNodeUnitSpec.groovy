package molspreadsheet

import spock.lang.Specification
import spock.lang.Unroll

/**
 * See the API for {@link grails.test.mixin.support.GrailsUnitTestMixin} for usage instructions
 */
//@TestFor(RingNode)
@Unroll
class RingNodeUnitSpec  extends Specification{

 //   MolSpreadSheetCell molSpreadSheetCell

    void setup() {
 //       molSpreadSheetCell = new MolSpreadSheetCell()
    }

    void tearDown() {
        // Tear down logic here
    }

    void "Test RingNode works"() {
        when:
        int mexicanWine = 1

        then:
        assert mexicanWine == 1
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
        assert ringNodeA.toString()== """{"name":"helicase"}""".toString()
        assert ringNodeC.toString()== """{"name":"nucleic acid binding"}""".toString()
        assert ringNodeE.toString()== """{"name":"DNA helicase", "children": [
{"name":"helicase"},
{"name":"helicase", "size":1500},
{"name":"nucleic acid binding"},
{"name":"nucleic acid binding", "size":1500}
]}""".toString()
        assert ringNodeE.maximumTreeHeight() == 1

//        where:
//        hillCurveValueIndex     |   identifier  |   columnIndex
//        0                       |  "a"          |   1
//        1                       |  "b"          |   2
//        2                       |  null         |   null
    }
}