/* Copyright (c) 2014, The Broad Institute
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
    * Redistributions of source code must retain the above copyright
      notice, this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright
      notice, this list of conditions and the following disclaimer in the
      documentation and/or other materials provided with the distribution.
    * Neither the name of The Broad Institute nor the
      names of its contributors may be used to endorse or promote products
      derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL The Broad Institute BE LIABLE FOR ANY
DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

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

