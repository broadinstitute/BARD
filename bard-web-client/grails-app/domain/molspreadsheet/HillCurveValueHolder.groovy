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
    String xAxisLabel = ''
    String yAxisLabel = ''
    static mapping = {
        spreadSheetActivityStorage column: "spreadActStore"
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
        this.identifier =  identifier
    }



    void setQualifier (MolSpreadSheetCellType molSpreadSheetCellType)  {
        if (molSpreadSheetCellType == MolSpreadSheetCellType.greaterThanNumeric)  {
            qualifier = ">"
        }  else if (molSpreadSheetCellType == MolSpreadSheetCellType.lessThanNumeric)  {
            qualifier = "<"
        }
    }


    void setQualifier (String qualifier)  {
        this.qualifier = qualifier
    }


    @Override
    public String toString() {
        String returnValue = "Missing data qualifier"

        Double numericalReturnValue = Double.NaN
        if (slope  != null)   {
            numericalReturnValue = slope
        }
        else if ((response  != null) &&
                (response.size()  == 1)  &&
                (response[0]  != null)) {
            numericalReturnValue = response[0]
        } else  {
            //TODO: find the business rule describing desired actions under these circumstances
            log.info "Problem identified by HillCurveValueHolder: no slope and as well no single valued response"
        }

        if (numericalReturnValue != Double.NaN)  {
            returnValue  = new ExperimentalValueUtil(numericalReturnValue,ExperimentalValueUnitUtil.Micromolar, ExperimentalValueUnitUtil.Micromolar)
        } else {
            returnValue  = '--'
        }

        if (returnValue.contains("--")) {
            return returnValue
        }
        else {
            return qualifier+" "+returnValue
        }
    }


}
