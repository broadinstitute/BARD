package molspreadsheet

import spock.lang.Specification
import bardqueryapi.*

import static org.junit.Assert.assertNotNull

/**
 * Created with IntelliJ IDEA.
 * User: balexand
 * Date: 10/10/12
 * Time: 2:24 PM
 * To change this template use File | Settings | File Templates.
 */
class MolSpreadSheetDataBuilderDirectorUnitSpec  extends Specification {

    void setup() {
    }

    void tearDown() {
    }


    void "test set mol spreadsheet data builder"() {
        when:
        MolSpreadSheetDataBuilder molSpreadSheetDataBuilder = new  MolSpreadSheetDataBuilder ()
        MolSpreadSheetData molSpreadSheetData = new MolSpreadSheetData()
        molSpreadSheetDataBuilder.molSpreadSheetData =  molSpreadSheetData
        MolSpreadSheetDataBuilderDirector molSpreadSheetDataBuilderDirector =  new MolSpreadSheetDataBuilderDirector()
        molSpreadSheetDataBuilderDirector.setMolSpreadSheetDataBuilder(molSpreadSheetDataBuilder)

        then:
        assertNotNull molSpreadSheetDataBuilderDirector.getMolSpreadSheetData()
        assert   molSpreadSheetDataBuilderDirector.getMolSpreadSheetData()==molSpreadSheetDataBuilder.getMolSpreadSheetData()

    }



}
