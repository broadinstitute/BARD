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
    Object objInternalValue
    String strInternalValue = "null"
    BigDecimal numInternalValue = 0.0
    Integer  intInternalValue = 0
    MolSpreadSheetCellUnit molSpreadSheetCellUnit =  MolSpreadSheetCellUnit.unknown

    String supplementalInternalValue = null
    // non image, no known units
    MolSpreadSheetCell( String value, MolSpreadSheetCellType molSpreadSheetCellType ){
        this.molSpreadSheetCellType = molSpreadSheetCellType
        switch (this.molSpreadSheetCellType) {
            case MolSpreadSheetCellType.numeric :
                numInternalValue = new BigDecimal(value)
                break;
            case MolSpreadSheetCellType.percentageNumeric :
                numInternalValue = new BigDecimal(value)
                break;
            case MolSpreadSheetCellType.greaterThanNumeric :
                numInternalValue = new BigDecimal(value)
                break;
            case MolSpreadSheetCellType.lessThanNumeric :
                numInternalValue = new BigDecimal(value)
                break;
            case MolSpreadSheetCellType.identifier :
                intInternalValue = new Integer(value)
                break;
            case MolSpreadSheetCellType.string :
                strInternalValue = new String(value)
                break;
            case  MolSpreadSheetCellType.image :
                assert "Images should Not go through the two parameter constructor"
                break;
            default:
                objInternalValue = value
        }
    }

    // numeric elements with unit specifications
    MolSpreadSheetCell( String value, MolSpreadSheetCellType molSpreadSheetCellType, MolSpreadSheetCellUnit molSpreadSheetCellUnit ){
        this.molSpreadSheetCellType = molSpreadSheetCellType
        switch (this.molSpreadSheetCellType) {
            case MolSpreadSheetCellType.numeric :
                numInternalValue = new BigDecimal(value)
                this.molSpreadSheetCellUnit = molSpreadSheetCellUnit
                break;
            case MolSpreadSheetCellType.percentageNumeric :
                numInternalValue = new BigDecimal(value)
                this.molSpreadSheetCellUnit = molSpreadSheetCellUnit
                break;
            case MolSpreadSheetCellType.greaterThanNumeric :
                numInternalValue = new BigDecimal(value)
                this.molSpreadSheetCellUnit = molSpreadSheetCellUnit
                break;
            case MolSpreadSheetCellType.lessThanNumeric :
                numInternalValue = new BigDecimal(value)
                this.molSpreadSheetCellUnit = molSpreadSheetCellUnit
                break;
            case MolSpreadSheetCellType.identifier :
                assert "identifier should Not go through the constructor with unit type"
                break;
            case MolSpreadSheetCellType.string :
                assert "string should Not go through the constructor with unit type"
                break;
            case  MolSpreadSheetCellType.image :
                assert "Images should Not go through the constructor with unit type"
                break;
            default:
                objInternalValue = value
        }
    }
    MolSpreadSheetCell( String value1,String value2, MolSpreadSheetCellType molSpreadSheetCellType ){
            this.molSpreadSheetCellType = molSpreadSheetCellType
            switch (this.molSpreadSheetCellType) {
                case  MolSpreadSheetCellType.image :
                    strInternalValue = new String(value1)
                    supplementalInternalValue =  new String(value2)
                    break;
                default:
                    assert "Non-images should Not go through the three parameter constructor"

            }

        }

    @Override
    String toString() {
        StringBuilder stringBuilder = new StringBuilder()
        switch (molSpreadSheetCellType) {
            case MolSpreadSheetCellType.numeric :
                stringBuilder.append( "${numInternalValue}" )
                break;
            case MolSpreadSheetCellType.percentageNumeric :
                stringBuilder.append( "${numInternalValue} %" )
                break;
            case MolSpreadSheetCellType.greaterThanNumeric :
                stringBuilder.append( "> ${numInternalValue}" )
                break;
            case MolSpreadSheetCellType.lessThanNumeric :
                stringBuilder.append( "< ${numInternalValue}" )
                break;
            case MolSpreadSheetCellType.identifier :
                stringBuilder.append( "${intInternalValue}" )
                break;
            case MolSpreadSheetCellType.string :
                stringBuilder.append( "${strInternalValue}" )
                break;
            case MolSpreadSheetCellType.image :
                assert "Images should not be retrieved using toString"
                stringBuilder.append( "${strInternalValue}" )
                break;
            default:
                stringBuilder.append( "${objInternalValue}" )
        }
        if (molSpreadSheetCellUnit  != MolSpreadSheetCellUnit.unknown)
            stringBuilder.append( " ${molSpreadSheetCellUnit.toString()}" )
        stringBuilder.toString()
    }

    LinkedHashMap<String, String> retrieveValues() {
        def returnValue = new LinkedHashMap<String, String>()
        returnValue.put("name",strInternalValue )
        returnValue.put("smiles",supplementalInternalValue)
        returnValue
    }


        static String imageConvert ( String name, String smiles )   {
    String retVal =
"""<img alt="${smiles}" title="${name}"
  src="\${createLink(controller: 'chemAxon', action: 'generateStructureImage', params: [smiles: '${smiles}', width: 150, height: 120])}"/>"""
    retVal
}

}


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


enum  MolSpreadSheetCellUnit {
    Molar ("M"),
    Millimolar ("mM"),
    Micromolar ("uM"),
    Nanomolar ("nM"),
    Picomolar ("pM"),
    Femtomolar ("fM"),
    Attamolar ("aM"),
    Zeptomolar ("aM"),
    unknown  ("U") ;

    private String value

    MolSpreadSheetCellUnit(String value){
        this.value = value;
    }

    public String toString(){
        return value;
    }

    public static MolSpreadSheetCellUnit getByValue(String value){
        for (final MolSpreadSheetCellUnit element : EnumSet.allOf(MolSpreadSheetCellUnit.class)) {
            if (element.toString().equals(value)) {
                return element;
            }
        }
        return null;
    }


}
