package results

import grails.test.mixin.TestMixin
import grails.test.mixin.support.GrailsUnitTestMixin
import molspreadsheet.MolSpreadSheetCellUnit
import org.apache.commons.lang.NotImplementedException
import spock.lang.Specification
import spock.lang.Unroll

@TestMixin(GrailsUnitTestMixin)
@Unroll
class ExperimentalValueUnitUnitSpec extends Specification {


    void "test getByDecimalValue #label"() {
        given:

        when: "#label"
        final ExperimentalValueUnit experimentalValueUnit = ExperimentalValueUnit.getByDecimalValue(value)

        then: "Should equal the expected"
        assert experimentalValueUnit == expectedExperimentValueUnit
        where:
        label        | value | expectedExperimentValueUnit
        "Molar"      | 0     | ExperimentalValueUnit.Molar
        "MilliMolar" | -3    | ExperimentalValueUnit.Millimolar
        "Micromolar" | -6    | ExperimentalValueUnit.Micromolar
        "Nanomolar"  | -9    | ExperimentalValueUnit.Nanomolar
        "Picomolar"  | -12   | ExperimentalValueUnit.Picomolar
        "Femtomolar" | -15   | ExperimentalValueUnit.Femtomolar
        "Attamolar"  | -18   | ExperimentalValueUnit.Attamolar
        "Zeptomolar" | -21   | ExperimentalValueUnit.Zeptomolar
        "Yoctomolar" | -24   | ExperimentalValueUnit.Yoctomolar
        "Null"       | 30    | null


    }

    void "test convert #label"() {


        when: "We call the convert method"
        final ExperimentalValueUnit experimentalValueUnit = ExperimentalValueUnit.convert(molSpreadSheetCellUnit)

        then: "The resulting experimental value type should much the expected one"
        assert experimentalValueUnit == expectedExperimentalValueUnit

        where:
        label                  | molSpreadSheetCellUnit            | expectedExperimentalValueUnit
        "Less Than Numeric"    | MolSpreadSheetCellUnit.Molar      | ExperimentalValueUnit.Molar
        "Greater Than Numeric" | MolSpreadSheetCellUnit.Millimolar | ExperimentalValueUnit.Millimolar
        "Percentage Numeric"   | MolSpreadSheetCellUnit.Micromolar | ExperimentalValueUnit.Micromolar
        "Numeric"              | MolSpreadSheetCellUnit.Nanomolar  | ExperimentalValueUnit.Nanomolar
        "Image"                | MolSpreadSheetCellUnit.Picomolar  | ExperimentalValueUnit.Picomolar
        "String"               | MolSpreadSheetCellUnit.Femtomolar | ExperimentalValueUnit.Femtomolar
        "Unknown"              | MolSpreadSheetCellUnit.Attamolar  | ExperimentalValueUnit.Attamolar
        "Unknown"              | MolSpreadSheetCellUnit.Zeptomolar | ExperimentalValueUnit.Zeptomolar
        "Unknown"              | MolSpreadSheetCellUnit.Yoctomolar | ExperimentalValueUnit.Yoctomolar
        "Unknown"              | MolSpreadSheetCellUnit.unknown    | ExperimentalValueUnit.unknown


    }



    void "test getByValue"() {


        when: "We call the convert method"
        final ExperimentalValueUnit experimentalValueUnit = ExperimentalValueUnit.convert(molSpreadSheetCellUnit)

        then: "The resulting experimental value type should much the expected one"
        assert experimentalValueUnit == expectedExperimentalValueUnit

        where:
        label                  | molSpreadSheetCellUnit            | expectedExperimentalValueUnit
        "Less Than Numeric"    | MolSpreadSheetCellUnit.Molar      | ExperimentalValueUnit.Molar
        "Greater Than Numeric" | MolSpreadSheetCellUnit.Millimolar | ExperimentalValueUnit.Millimolar
        "Percentage Numeric"   | MolSpreadSheetCellUnit.Micromolar | ExperimentalValueUnit.Micromolar
        "Numeric"              | MolSpreadSheetCellUnit.Nanomolar  | ExperimentalValueUnit.Nanomolar
        "Image"                | MolSpreadSheetCellUnit.Picomolar  | ExperimentalValueUnit.Picomolar
        "String"               | MolSpreadSheetCellUnit.Femtomolar | ExperimentalValueUnit.Femtomolar
        "Unknown"              | MolSpreadSheetCellUnit.Attamolar  | ExperimentalValueUnit.Attamolar
        "Unknown"              | MolSpreadSheetCellUnit.Zeptomolar | ExperimentalValueUnit.Zeptomolar
        "Unknown"              | MolSpreadSheetCellUnit.Yoctomolar | ExperimentalValueUnit.Yoctomolar
        "Unknown"              | MolSpreadSheetCellUnit.unknown    | ExperimentalValueUnit.unknown


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

