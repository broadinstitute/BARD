package molspreadsheet

class MolSpreadSheetData {

    static hasMany = [molSpreadSheetCell: MolSpreadSheetCell]
    static transients = ['rowCount', 'columnCount']

    Map<String, MolSpreadSheetCell> mssData = [:]
    Map<Long, Integer> rowPointer = [:]
    Map<Long, Integer> columnPointer = [:]
    List<List<String>> mssHeaders = []
    List<String> experimentNameList = []
    List<String> experimentFullNameList = []
    Map<Integer, String> mapColumnsToAssay = [:]
    Map<Integer, String> mapColumnsToAssayName = [:]
    MolSpreadsheetDerivedMethod molSpreadsheetDerivedMethod

    /**
     * Display a cell, as specified by a row and column
     * @param rowCnt
     * @param colCnt
     * @return
     */
    Map displayValue(int rowCnt, int colCnt) {
        Map<String, String> returnValue = [:]
        String key = "${rowCnt}_${colCnt}"
        MolSpreadSheetCell molSpreadSheetCell
        if (mssData.containsKey(key)) {
            molSpreadSheetCell = mssData[key]
            returnValue = molSpreadSheetCell.mapForMolecularSpreadsheet()
        } else {  // This is a critical error.  Try to cover all the bases so we don't crash at least.
            returnValue.put("value", "-")
            returnValue.put("name", "Unknown name")
            returnValue.put("smiles", "Unknown smiles")
        }
        returnValue
    }


    SpreadSheetActivityStorage findSpreadSheetActivity(int rowCnt, int colCnt) {
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
    int getRowCount() {
        if (rowPointer) {
            return rowPointer.size()
        }
        return 0
    }

    /**
     *
     * @return
     */
    int getColumnCount() {
        if (mssHeaders) {
            return mssHeaders.flatten().size()
        }
        return 0

    }


    int getSuperColumnCount() {
        if (mssHeaders) {
            return mssHeaders.size()
        }
        return 0

    }

    List<String> getSubColumns(int experimentCount) {
        List<String> subColumns = []
        if (experimentCount < this.mssHeaders.size())
            subColumns = this.mssHeaders[experimentCount]
        subColumns
    }




    List<String> getColumns() {
        if (mssHeaders) {
            return mssHeaders.flatten()
        }
        return []

    }



    List <LinkedHashMap<String,String>> determineResponseTypesPerAssay () {
        List <LinkedHashMap<String,String>> returnValue = []
        LinkedHashMap<String,Integer> accumulator =  [:]
        List<String> assayNames = []
        if (mapColumnsToAssay.size()>4)  {
            for (int i in 4..(mapColumnsToAssay.size()-1))  {
                  if (accumulator.containsKey(mapColumnsToAssay[i]))  {
                      (accumulator[mapColumnsToAssay[i]]) ++
                  } else {
                    (accumulator[mapColumnsToAssay[i]]) = 0
                    assayNames << mapColumnsToAssay[i]
                }
            }
        }
        //TODO: Assay Names seems to be zero in some cases. Is it possible?
        for (int i in 0..(assayNames.size()-1))  {
            returnValue << ["assayName":assayNames[i],"numberOfResultTypes":(accumulator[assayNames[i]]+1),"fullAssayName":experimentFullNameList[i]]

        }
        returnValue
    }






    static constraints = {
        mssData(nullable: false)
        rowPointer(nullable: false)
        columnPointer(nullable: false)
        //mssHeaders (nullable: false)
        molSpreadsheetDerivedMethod(nullable: true)
    }



}









enum MolSpreadsheetDerivedMethod {
    Compounds_NoAssays_NoProjects("Only compound(s); no assays or projects"),
    NoCompounds_Assays_NoProjects("Only assay(s); no compounds or projects"),
    NoCompounds_NoAssays_Projects("Only project(s); no assays or compounds");

    private final String description

    MolSpreadsheetDerivedMethod(String desc) {
        this.description = desc
    }

    public String description() {
        return description
    }
}
