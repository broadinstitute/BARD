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

package molspreadsheet

import bard.core.util.ExperimentalValueUnitUtil
import bard.core.util.ExperimentalValueUtil
import org.slf4j.LoggerFactory

class HillCurveValueHolder {

    static belongsTo = [spreadSheetActivityStorage: SpreadSheetActivityStorage]
    private final log = LoggerFactory.getLogger(this.class.name)

    String identifier = ""
    Integer subColumnIndex = 0
    Double s0
    Double sInf
    Double slope
    Double coef
    java.util.List<java.lang.Double> conc = []
    java.util.List<java.lang.Double> response = []
    String qualifier = ""
    String stringValue = "" // some cells are non numeric.  Store those values here
    String xAxisLabel = ''
    String yAxisLabel = ''
    static mapping = {
        table('MOL_SS_HILL_CURVE_VALUE_HOLDER')
        id(generator: 'sequence', params: [sequence: 'HILL_CURVE_VALUE_HOLDER_ID_SEQ'])
        spreadSheetActivityStorage(column: "MOL_SS_ACTIVITY_STORAGE_ID")
    }
    static constraints = {
        identifier nullable: false
        subColumnIndex nullable: false
        s0 nullable: true
        sInf nullable: true
        slope nullable: true
        coef nullable: true
        xAxisLabel nullable: true
        yAxisLabel nullable: true
    }


    void setIdentifier(String identifier) {
        this.identifier = identifier
    }



    void setQualifier(MolSpreadSheetCellType molSpreadSheetCellType) {
        if (molSpreadSheetCellType == MolSpreadSheetCellType.greaterThanNumeric) {
            qualifier = ">"
        } else if (molSpreadSheetCellType == MolSpreadSheetCellType.lessThanNumeric) {
            qualifier = "<"
        }
    }


    void setQualifier(String qualifier) {
        this.qualifier = qualifier
    }


    @Override
    public String toString() {
        String returnValue = "Missing data qualifier"
        Boolean preferAStringValue = false

        // Decide which datatype we are dealing with
        Double numericalReturnValue = Double.NaN
        if (stringValue != "") {   // if we have a string value then use it
            returnValue = stringValue
            preferAStringValue = true
        } else if (slope != null) {
            numericalReturnValue = slope
        } else if ((response != null) &&
                (response.size() == 1) &&
                (response[0] != null)) {
            numericalReturnValue = response[0]
        } else  {        // this shouldn't happen
            //TODO: find the business rule describing desired actions under these circumstances
            log.info "Problem identified by HillCurveValueHolder: no slope, no string, and therefore no single valued response"
        }

        // if we have a numerical value we have a little more work to do
        if (!preferAStringValue) {

            // Use an appropriate precision as long as the number is non-null
            if (numericalReturnValue != Double.NaN) {
                returnValue = new ExperimentalValueUtil(numericalReturnValue, ExperimentalValueUnitUtil.Micromolar, ExperimentalValueUnitUtil.Micromolar)
            } else {
                returnValue = '--'
            }

            // add a qualifier if we have one ( otherwise append a null string ).
            if ((!returnValue.contains("--"))&&
                (!returnValue.equals("NA")))  // special case: sometimes we are told the string value="NA".  If so then a qualifier doesn't make sense
                     {
                returnValue = qualifier + " " + returnValue
            }
        }

        returnValue
    }


}
