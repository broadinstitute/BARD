package molspreadsheet

import results.ExperimentalValue

class HillCurveValueHolder {

    static belongsTo = [spreadSheetActivityStorage: SpreadSheetActivityStorage]

        String identifier = ""
        Integer subColumnIndex = 0
        Double s0
        Double sInf
        Double slope
        Double coef
        java.util.List<java.lang.Double> conc = []
        java.util.List<java.lang.Double> response = []

    static constraints = {
        identifier nullable: false
        identifier subColumnIndex: false
        s0 nullable: true
        sInf nullable: true
        slope nullable: true
        coef  nullable: true
        //     conc nullable: false
        //     response nullable: false
    }


    @Override
    public String toString() {
        String returnValue = "Missing data qualifier."

        Double numericalReturnValue = Double.NaN
        if (slope  != null)   {
            numericalReturnValue = slope
        }
        else if ((response  != null) &&
                (response.size()  == 1)) {
            numericalReturnValue = response[0]
        } else  {
            //TODO: find the business rule describing desired actions under these circumstances
           println "Problem identified by HillCurveValueHolder: no slope and as well no single valued response"
        }

        if (numericalReturnValue != Double.NaN)  {
            returnValue  = new ExperimentalValue(numericalReturnValue)
        }

        returnValue
    }

}
