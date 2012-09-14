package bardqueryapi

/**
 * Created with IntelliJ IDEA.
 * User: balexand
 * Date: 9/14/12
 * Time: 11:03 AM
 * To change this template use File | Settings | File Templates.
 */
class MolSpreadSheetCell {
    MolSpreadSheetCellType molSpreadSheetCellType = MolSpreadSheetCellType.unknown
    def internalValue = ""
    MolSpreadSheetCell( String value, MolSpreadSheetCellType molSpreadSheetCellType ){
        switch (molSpreadSheetCellType) {
            case MolSpreadSheetCellType.numeric :
                internalValue = new BigDecimal(value)
                break;
            case MolSpreadSheetCellType.percentageNumeric :
                internalValue = new BigDecimal(value)
                break;
            case MolSpreadSheetCellType.greaterThanNumeric :
                internalValue = new BigDecimal(value)
                break;
            case MolSpreadSheetCellType.lessThanNumeric :
                internalValue = new BigDecimal(value)
                break;
            case MolSpreadSheetCellType.identifier :
                internalValue = new Integer(value)
                break;
            default:
                internalValue = value
        }
    }
    @Override
    String toString() {
        String returnValue = ""
        switch (molSpreadSheetCellType) {
            case MolSpreadSheetCellType.numeric :
                returnValue = "${internalValue}"
                break;
            case MolSpreadSheetCellType.percentageNumeric :
                returnValue = "${internalValue} %"
                break;
            case MolSpreadSheetCellType.greaterThanNumeric :
                returnValue = "> ${internalValue}"
                break;
            case MolSpreadSheetCellType.lessThanNumeric :
                returnValue = "< ${internalValue}"
                break;
            case MolSpreadSheetCellType.identifier :
                returnValue = "${internalValue}"
                break;
            default:
                returnValue = "${internalValue}"
        }
        return internalValue.toString()    //To change body of overridden methods use File | Settings | File Templates.
    }

}
//MolSpreadSheetCell molSpreadSheetCell = new MolSpreadSheetCell("4.2",MolSpreadSheetCellType.numeric)

enum  MolSpreadSheetCellType {
    lessThanNumeric,
    greaterThanNumeric,
    percentageNumeric,
    numeric,
    identifier,
    image,
    string,
    unknown
}