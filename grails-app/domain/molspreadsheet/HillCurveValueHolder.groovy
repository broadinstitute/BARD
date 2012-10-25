package molspreadsheet

class HillCurveValueHolder {
    static belongsTo = [spreadSheetActivityStorage: SpreadSheetActivityStorage]

        Integer subColumnIndex
        Double s0
        Double sInf
        Double slope
        Double coef
        java.util.List<java.lang.Double> conc = []
        java.util.List<java.lang.Double> response = []

    static constraints = {
         s0 ()
         sInf()
         slope()
         coef ()
//         conc ()
//         response()
    }
}
