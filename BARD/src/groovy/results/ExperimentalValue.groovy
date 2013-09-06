package results

import bard.core.util.ExperimentalValueTypeUtil
import bard.core.util.ExperimentalValueUnitUtil
import molspreadsheet.MolSpreadSheetCellType
import molspreadsheet.MolSpreadSheetCellUnit
import org.apache.commons.lang.NotImplementedException

/**
 * TODO: Because of common code with REST Wrapper, I have had to move some methods
 * enumeration to hold units
 */
enum ExperimentalValueUnit {

    static ExperimentalValueUnitUtil convert(MolSpreadSheetCellUnit molSpreadSheetCellUnit) {
        switch (molSpreadSheetCellUnit) {
            case MolSpreadSheetCellUnit.Molar:
                return ExperimentalValueUnitUtil.Molar;
            case MolSpreadSheetCellUnit.Millimolar:
                return ExperimentalValueUnitUtil.Millimolar;
            case MolSpreadSheetCellUnit.Micromolar:
                return ExperimentalValueUnitUtil.Micromolar;
            case MolSpreadSheetCellUnit.Nanomolar:
                return ExperimentalValueUnitUtil.Nanomolar;
            case MolSpreadSheetCellUnit.Picomolar:
                return ExperimentalValueUnitUtil.Picomolar;
            case MolSpreadSheetCellUnit.Femtomolar:
                return ExperimentalValueUnitUtil.Femtomolar;
            case MolSpreadSheetCellUnit.Attamolar:
                return ExperimentalValueUnitUtil.Attamolar;
            case MolSpreadSheetCellUnit.Zeptomolar:
                return ExperimentalValueUnitUtil.Zeptomolar;
            case MolSpreadSheetCellUnit.Yoctomolar:
                return ExperimentalValueUnitUtil.Yoctomolar;
            case MolSpreadSheetCellUnit.unknown:
                return ExperimentalValueUnitUtil.unknown;
            default:
                throw new NotImplementedException(molSpreadSheetCellUnit.toString() + " not yet implemented");
        }
    }
}

/**
 * Enumeration to hold the type of the value
 */
enum ExperimentalValueType {

    static ExperimentalValueTypeUtil convert(MolSpreadSheetCellType molSpreadSheetCellType) {
        switch (molSpreadSheetCellType) {
            case MolSpreadSheetCellType.lessThanNumeric:
                return ExperimentalValueTypeUtil.lessThanNumeric;
            case MolSpreadSheetCellType.greaterThanNumeric:
                return ExperimentalValueTypeUtil.greaterThanNumeric;
            case MolSpreadSheetCellType.percentageNumeric:
                return ExperimentalValueTypeUtil.percentageNumeric;
            case MolSpreadSheetCellType.numeric:
                return ExperimentalValueTypeUtil.numeric;
            case MolSpreadSheetCellType.image:
                return ExperimentalValueTypeUtil.image;
            case MolSpreadSheetCellType.string:
                return ExperimentalValueTypeUtil.string;
            case MolSpreadSheetCellType.unknown:
                return ExperimentalValueTypeUtil.unknown;
            default:
                throw new NotImplementedException("${molSpreadSheetCellType} not yet implemented");
        }
    }

}

