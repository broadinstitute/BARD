package results

import grails.test.mixin.TestMixin
import grails.test.mixin.support.GrailsUnitTestMixin
import molspreadsheet.MolSpreadSheetCellUnit
import org.apache.commons.lang.NotImplementedException
import spock.lang.Specification
import spock.lang.Unroll
import bard.core.util.ExperimentalValueUnitUtil

@TestMixin(GrailsUnitTestMixin)
@Unroll
class ExperimentalValueUnitUnitSpec extends Specification {


    void "test getByDecimalValue #label"() {
        given:

        when: "#label"
        final ExperimentalValueUnitUtil experimentalValueUnit = ExperimentalValueUnitUtil.getByDecimalValue(value)

        then: "Should equal the expected"
        assert experimentalValueUnit == expectedExperimentValueUnit
        where:
        label        | value | expectedExperimentValueUnit
        "Molar"      | 0     | ExperimentalValueUnitUtil.Molar
        "MilliMolar" | -3    | ExperimentalValueUnitUtil.Millimolar
        "Micromolar" | -6    | ExperimentalValueUnitUtil.Micromolar
        "Nanomolar"  | -9    | ExperimentalValueUnitUtil.Nanomolar
        "Picomolar"  | -12   | ExperimentalValueUnitUtil.Picomolar
        "Femtomolar" | -15   | ExperimentalValueUnitUtil.Femtomolar
        "Attamolar"  | -18   | ExperimentalValueUnitUtil.Attamolar
        "Zeptomolar" | -21   | ExperimentalValueUnitUtil.Zeptomolar
        "Yoctomolar" | -24   | ExperimentalValueUnitUtil.Yoctomolar
        "Null"       | 30    | null


    }

    void "test convert #label"() {


        when: "We call the convert method"
        final ExperimentalValueUnitUtil experimentalValueUnit = ExperimentalValueUnit.convert(molSpreadSheetCellUnit)

        then: "The resulting experimental value type should much the expected one"
        assert experimentalValueUnit == expectedExperimentalValueUnit

        where:
        label                  | molSpreadSheetCellUnit            | expectedExperimentalValueUnit
        "Less Than Numeric"    | MolSpreadSheetCellUnit.Molar      | ExperimentalValueUnitUtil.Molar
        "Greater Than Numeric" | MolSpreadSheetCellUnit.Millimolar | ExperimentalValueUnitUtil.Millimolar
        "Percentage Numeric"   | MolSpreadSheetCellUnit.Micromolar | ExperimentalValueUnitUtil.Micromolar
        "Numeric"              | MolSpreadSheetCellUnit.Nanomolar  | ExperimentalValueUnitUtil.Nanomolar
        "Image"                | MolSpreadSheetCellUnit.Picomolar  | ExperimentalValueUnitUtil.Picomolar
        "String"               | MolSpreadSheetCellUnit.Femtomolar | ExperimentalValueUnitUtil.Femtomolar
        "Unknown"              | MolSpreadSheetCellUnit.Attamolar  | ExperimentalValueUnitUtil.Attamolar
        "Unknown"              | MolSpreadSheetCellUnit.Zeptomolar | ExperimentalValueUnitUtil.Zeptomolar
        "Unknown"              | MolSpreadSheetCellUnit.Yoctomolar | ExperimentalValueUnitUtil.Yoctomolar
        "Unknown"              | MolSpreadSheetCellUnit.unknown    | ExperimentalValueUnitUtil.unknown


    }



    void "test getByValue"() {


        when: "We call the convert method"
        final ExperimentalValueUnitUtil experimentalValueUnit = ExperimentalValueUnit.convert(molSpreadSheetCellUnit)

        then: "The resulting experimental value type should much the expected one"
        assert experimentalValueUnit == expectedExperimentalValueUnit

        where:
        label                  | molSpreadSheetCellUnit            | expectedExperimentalValueUnit
        "Less Than Numeric"    | MolSpreadSheetCellUnit.Molar      | ExperimentalValueUnitUtil.Molar
        "Greater Than Numeric" | MolSpreadSheetCellUnit.Millimolar | ExperimentalValueUnitUtil.Millimolar
        "Percentage Numeric"   | MolSpreadSheetCellUnit.Micromolar | ExperimentalValueUnitUtil.Micromolar
        "Numeric"              | MolSpreadSheetCellUnit.Nanomolar  | ExperimentalValueUnitUtil.Nanomolar
        "Image"                | MolSpreadSheetCellUnit.Picomolar  | ExperimentalValueUnitUtil.Picomolar
        "String"               | MolSpreadSheetCellUnit.Femtomolar | ExperimentalValueUnitUtil.Femtomolar
        "Unknown"              | MolSpreadSheetCellUnit.Attamolar  | ExperimentalValueUnitUtil.Attamolar
        "Unknown"              | MolSpreadSheetCellUnit.Zeptomolar | ExperimentalValueUnitUtil.Zeptomolar
        "Unknown"              | MolSpreadSheetCellUnit.Yoctomolar | ExperimentalValueUnitUtil.Yoctomolar
        "Unknown"              | MolSpreadSheetCellUnit.unknown    | ExperimentalValueUnitUtil.unknown


    }





    void "test convert with Exception"() {


        when: "We call the convert method"
        ExperimentalValueUnit.convert(molSpreadSheetCellUnit)

        then: "We expect an error to  be thrown"
        Exception ee = thrown()
        assert ee instanceof NotImplementedException


        where:
        label            | molSpreadSheetCellUnit
        "Unhandled Enum" | MolSpreadSheetCellUnit.unhandled


    }
}

