package bardqueryapi

/**
 * Created with IntelliJ IDEA.
 * User: balexand
 * Date: 9/14/12
 * Time: 11:03 AM
 * To change this template use File | Settings | File Templates.
 */
class MolSpreadSheetCellHelper{
}



enum MolSpreadSheetCellType {
    lessThanNumeric,
    greaterThanNumeric,
    percentageNumeric,
    numeric,
    identifier,
    image,
    string,
    unknown
}


enum MolSpreadSheetCellUnit {
    Molar("M"),
    Millimolar("mM"),
    Micromolar("uM"),
    Nanomolar("nM"),
    Picomolar("pM"),
    Femtomolar("fM"),
    Attamolar("aM"),
    Zeptomolar("aM"),
    unknown("U");

    private String value

    MolSpreadSheetCellUnit(String value) {
        this.value = value;
    }

    public String toString() {
        return value;
    }

    public static MolSpreadSheetCellUnit getByValue(String value) {
        for (final MolSpreadSheetCellUnit element : EnumSet.allOf(MolSpreadSheetCellUnit.class)) {
            if (element.toString().equals(value)) {
                return element;
            }
        }
        return null;
    }
}