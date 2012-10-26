package molspreadsheet

class HillCurveValueHolder {
    static belongsTo = [spreadSheetActivityStorage: SpreadSheetActivityStorage]
        String identifier
        Integer subColumnIndex
        Double s0
        Double sInf
        Double slope
        Double coef
        java.util.List<java.lang.Double> conc = []
        java.util.List<java.lang.Double> response = []

    @Override
    public String toString() {
        String returnValue = "unknown value"
        if ((slope  != null)   &&
            (slope != Double.NaN))
            returnValue =  slope.toString()
        else if ((response  != null) &&
                (response.size()  == 1))
            returnValue =  response[0].toString()
        else
            print "foo"

        returnValue
    }

    static constraints = {
         s0 ()
         sInf()
         slope()
         coef ()
//         conc ()
//         response()
    }
}
