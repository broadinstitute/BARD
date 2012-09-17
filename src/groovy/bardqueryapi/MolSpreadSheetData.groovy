package bardqueryapi

/**
 * Created with IntelliJ IDEA.
 * User: balexand
 * Date: 9/14/12
 * Time: 11:55 AM
 * To change this template use File | Settings | File Templates.
 */
class MolSpreadSheetData {
    LinkedHashMap<String,MolSpreadSheetCell> mssData
    LinkedHashMap<Long,Integer> rowPointer
    List mssHeaders = new ArrayList()

    MolSpreadSheetData()  {
        mssData = new LinkedHashMap<String,MolSpreadSheetCell> ()
        rowPointer = new LinkedHashMap<Long,Integer>()
        mssHeaders = new ArrayList()
    }

    // test data
    MolSpreadSheetData(String s)  {
        mssData = new LinkedHashMap<String,MolSpreadSheetCell> ()
        rowPointer = new LinkedHashMap<Long,Integer>()
        mssHeaders = new ArrayList()
        mssData.put("0_0", new MolSpreadSheetCell("1",MolSpreadSheetCellType.string))
        mssData.put("0_1", new MolSpreadSheetCell("3888711",MolSpreadSheetCellType.identifier))
        mssData.put("0_2", new MolSpreadSheetCell("0.0000144",MolSpreadSheetCellType.numeric))
        mssData.put("0_3", new MolSpreadSheetCell("0.00000529",MolSpreadSheetCellType.numeric))
        mssData.put("0_4", new MolSpreadSheetCell("0.000000823",MolSpreadSheetCellType.numeric))
        mssData.put("1_0", new MolSpreadSheetCell("2",MolSpreadSheetCellType.string))
        mssData.put("1_1", new MolSpreadSheetCell("3888712",MolSpreadSheetCellType.identifier))
        mssData.put("1_2", new MolSpreadSheetCell("0.0000543",MolSpreadSheetCellType.numeric))
        mssData.put("1_3", new MolSpreadSheetCell("0.00000566",MolSpreadSheetCellType.numeric))
        mssData.put("1_4", new MolSpreadSheetCell("0.00000101",MolSpreadSheetCellType.numeric))
        mssData.put("2_0", new MolSpreadSheetCell("3",MolSpreadSheetCellType.string))
        mssData.put("2_1", new MolSpreadSheetCell("3888713",MolSpreadSheetCellType.identifier))
        mssData.put("2_2", new MolSpreadSheetCell("0.0000657",MolSpreadSheetCellType.numeric))
        mssData.put("2_3", new MolSpreadSheetCell("0.00000721",MolSpreadSheetCellType.numeric))
        mssData.put("2_4", new MolSpreadSheetCell("0.00000093",MolSpreadSheetCellType.numeric))
        mssHeaders.add("Struct")
        mssHeaders.add("CID")
        mssHeaders.add("DNA polymerase (Q9Y253) ADID : 1 IC50")
        mssHeaders.add("Serine-protein kinase (Q13315) ADID : 1 IC50")
        mssHeaders.add("Tyrosine-DNA phosphodiesterase 1 (Q9NUW8) ADID: 514789")
        rowPointer.put(5342L,0)
        rowPointer.put(5345L,0)
        rowPointer.put(5346L,0)
    }

    String displayValue(int rowCnt, int colCnt) {
        String returnValue
        String key = "${rowCnt}_${colCnt}"
        if (mssData.containsKey(key)) {
            returnValue = mssData[key].toString()
        } else
            returnValue = "error: val unknown"
        returnValue
    }


    int getRowCount(){
        if (rowPointer == null)
            return 0
        else
            return rowPointer.size()
    }

    int getColumnCount(){
        if (mssHeaders == null)
            return 0
        else
            return mssHeaders.size()
    }

}
