package molspreadsheet

import org.slf4j.LoggerFactory
import bard.core.util.ExperimentalValueUtil

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


    @Override
    public String toString() {
        String returnValue = "Missing data qualifier"

        Double numericalReturnValue = Double.NaN
        if (slope != null) {
            numericalReturnValue = slope
        }
        else if ((response != null) &&
                (response.size() == 1) &&
                (response[0] != null)) {
            numericalReturnValue = response[0]
        } else {
            //TODO: find the business rule describing desired actions under these circumstances
            log.info "Problem identified by HillCurveValueHolder: no slope and as well no single valued response"
        }

        if (numericalReturnValue != Double.NaN) {
            returnValue = new ExperimentalValueUtil(numericalReturnValue)
        } else {
            returnValue = '--'
        }

        qualifier + returnValue
    }


}
