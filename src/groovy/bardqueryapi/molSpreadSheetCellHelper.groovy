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
    Molar("M", 0),
    Millimolar("mM", 3),
    Micromolar("uM", 6),
    Nanomolar("nM", 9),
    Picomolar("pM", 12),
    Femtomolar("fM", 15),
    Attamolar("aM", 18),
    Zeptomolar("aM", 21),
    unknown("U", 0);

    private String value
    private  int decimalPlacesFromMolar

    MolSpreadSheetCellUnit(String value,int decimalPlacesFromMolar) {
        this.value = value;
        this.decimalPlacesFromMolar = decimalPlacesFromMolar;
    }

    public int getDecimalPlacesFromMolar() {
        decimalPlacesFromMolar
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