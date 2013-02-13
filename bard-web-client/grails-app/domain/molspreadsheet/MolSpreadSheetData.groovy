package molspreadsheet

class MolSpreadSheetData {

    static hasMany = [molSpreadSheetCell: MolSpreadSheetCell]
    static transients = ['rowCount', 'columnCount']

    Map<String, MolSpreadSheetCell> mssData = [:]
    Map<Long, Integer> rowPointer = [:]
    Map<Long, Integer> columnPointer = [:]
    Map<Long, Long> mapExperimentIdsToCapAssayIds = [:]
    List<MolSpreadSheetColumnHeader> mssHeaders = []
    List<String> experimentNameList = []
    List<String> experimentFullNameList = []
    Map<Integer, String> mapColumnsToAssay = [:]
    Map<Integer, String> mapColumnsToAssayName = [:]
    MolSpreadsheetDerivedMethod molSpreadsheetDerivedMethod

    static mapping = {
        table 'MSData'

        mapColumnsToAssay column: "colsToAssay"
        rowPointer column: "rowPter"
        columnPointer column: "columnPter"
        mapExperimentIdsToCapAssayIds indexColumn: [name: "expToCapAssay_idx", type: Integer],
                joinTable: [name: 'mexptocap', column: "expToCapAssayIds" ,key : 'exp_id', column: 'maptoexp']
        mapColumnsToAssayName indexColumn: [name: "colsToAssayName_idx", type: Integer],
                joinTable: [name: 'mcolsassayname', column: "colsToAssayName"]


    }
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
            return getColumns().size()
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
        if (experimentCount < getSuperColumnCount())
            subColumns = this.mssHeaders[experimentCount].molSpreadSheetColSubHeaderList*.columnTitle
        subColumns
    }

    List<MolSpreadSheetColSubHeader> getSubColumnList(int experimentCount) {
        List<String> subColumns = []
        if (experimentCount < getSuperColumnCount())
            subColumns = this.mssHeaders[experimentCount].molSpreadSheetColSubHeaderList
        subColumns
    }



    List<String> getColumns() {
        if (mssHeaders) {
            return mssHeaders*.molSpreadSheetColSubHeaderList*.columnTitle.flatten()
        }
        return []

    }


    List<String> getColumnsDescr() {
        if (mssHeaders) {
            return mssHeaders*.molSpreadSheetColSubHeaderList*.unitsInColumn.flatten()
        }
        return []

    }




    List<LinkedHashMap<String, String>> determineResponseTypesPerAssay() {
        List<LinkedHashMap<String, String>> returnValue = []
        LinkedHashMap<String, Integer> accumulator = [:]
        List<String> assayNames = []
        if (mapColumnsToAssay.size() > 4) {
            for (int i in 4..(mapColumnsToAssay.size() - 1)) {
                if (accumulator.containsKey(mapColumnsToAssay[i])) {
                    (accumulator[mapColumnsToAssay[i]])++
                } else {
                    (accumulator[mapColumnsToAssay[i]]) = 0
                    assayNames << mapColumnsToAssay[i]
                }
            }
        }

        if (assayNames) {
            for (int i in 0..(assayNames.size() - 1)) {
                String fullAssayName = 'Data error: please contact your system administrator'   // This message should never be displayed
                if (assayNames[i] != null) {    // Assay name should never be null -- this is a safety measure
                    int columnOfAssay = mapColumnsToAssay.find { it.value == assayNames[i]}.key
                    fullAssayName = mapColumnsToAssayName[columnOfAssay]
                }
                //convert assay id to cap id
                String capId = "U"
                if (assayNames[i]) {
                    Long assayId = assayNames[i].toLong()
                    if (mapExperimentIdsToCapAssayIds.containsKey(assayId)) {
                        capId = mapExperimentIdsToCapAssayIds[assayId].toString()
                    }
                }
                returnValue << ["assayName": assayNames[i], "bardAssayId": capId, "numberOfResultTypes": (accumulator[assayNames[i]] + 1), "fullAssayName": fullAssayName]
            }
        }
        returnValue
    }






    static constraints = {
        mssData(nullable: false)
        rowPointer(nullable: false)
        columnPointer(nullable: false)
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
