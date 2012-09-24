package molspreadsheet

class SpreadSheetActivityStorage {

    static belongsTo = [molSpreadSheetCell : MolSpreadSheetCell]

    Long eid
    Long cid
    Long sid
    String hillCurveValueId
    Double  hillCurveValueSInf
    Double  hillCurveValueS0
    Double  hillCurveValueSlope
    Double  hillCurveValueCoef
    List<Double>  hillCurveValueConc
    List<Double>  hillCurveValueResponse

    static constraints = {
    }
}
