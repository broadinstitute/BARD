package molspreadsheet

import java.math.MathContext

class MolSpreadSheetData {

    static hasMany = [ molSpreadSheetCell : MolSpreadSheetCell ]
    static transients = ['rowCount','columnCount']

    LinkedHashMap<String,MolSpreadSheetCell> mssData   = new LinkedHashMap<String,MolSpreadSheetCell> ()
    LinkedHashMap<Long,Integer> rowPointer  = new LinkedHashMap<Long,Integer>()
    LinkedHashMap<Long,Integer> columnPointer   = new LinkedHashMap<Long,Integer>()
    List<String> mssHeaders   = new ArrayList<String>()

    MolSpreadSheetData()  {
    }


    MathContext mathContext

    /**
     * Display a cell, as specified by a row and column
     * @param rowCnt
     * @param colCnt
     * @return
     */
    LinkedHashMap displayValue(int rowCnt, int colCnt) {
        def returnValue = new  LinkedHashMap<String, String>()
        String key = "${rowCnt}_${colCnt}"
        MolSpreadSheetCell molSpreadSheetCell
        if (mssData.containsKey(key)) {
            molSpreadSheetCell = mssData[key]
            returnValue = molSpreadSheetCell.mapForMolecularSpreadsheet ()
        }   else {  // This is a critical error.  Try to cover all the bases so we don't crash at least.
            returnValue.put("value","-")
            returnValue.put("name", "Unknown name")
            returnValue.put("smiles","Unknown smiles")
        }
        returnValue
    }

    SpreadSheetActivityStorage findSpreadSheetActivity(int rowCnt, int colCnt){
        SpreadSheetActivityStorage spreadSheetActivityStorage = null
        String key = "${rowCnt}_${colCnt}"
        MolSpreadSheetCell molSpreadSheetCell
        if (mssData.containsKey(key)) {
            molSpreadSheetCell = mssData[key]
            spreadSheetActivityStorage = molSpreadSheetCell.spreadSheetActivityStorage
        }
        return spreadSheetActivityStorage
    }


    /**
     *
     * @return
     */
    int getRowCount(){
        if (rowPointer == null)
            return 0
        else
            return rowPointer.size()
    }

    /**
     *
     * @return
     */
    int getColumnCount(){
        if (mssHeaders == null)
            return 0
        else
            return mssHeaders.size()
    }

    static constraints = {
        mssData  (nullable: false)
        rowPointer  (nullable: false)
        columnPointer  (nullable: false)
        //mssHeaders (nullable: false)
    }
}


