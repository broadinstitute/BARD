package molspreadsheet


enum MolSpreadSheetCellType {
    lessThanNumeric,
    greaterThanNumeric,
    percentageNumeric,
    numeric,
    identifier,
    image,
    string,
    unknown,
    unhandled;
}


enum MolSpreadSheetCellUnit {
    Molar("M", 0),
    Millimolar("mM", 3),
    Micromolar("uM", 6),
    Nanomolar("nM", 9),
    Picomolar("pM", 12),
    Femtomolar("fM", 15),
    Attamolar("aM", 18),
    Zeptomolar("zM", 21),
    Yoctomolar("yM", 24),
    unknown("U", 0),
    unhandled("Unhandled Exception",-5)  ;//We should never use this. This is for exception handling and allows us to test all branches of the code

    private String value
    private  int decimalPlacesFromMolar

    MolSpreadSheetCellUnit(String value,int decimalPlacesFromMolar) {
        this.value = value;
        this.decimalPlacesFromMolar = decimalPlacesFromMolar;
    }

    public String toString() {
        return value;
    }
}