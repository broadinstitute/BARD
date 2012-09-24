package bardqueryapi

import molspreadsheet.MolSpreadSheetCell
import molspreadsheet.MolSpreadSheetData

class ExperimentalResultsService {

    static MolSpreadSheetData molSpreadSheetData

    static {
        molSpreadSheetData = new MolSpreadSheetData()
        molSpreadSheetData.mssHeaders = ["Chemical Structure",
                "CID",
                "DNA polymerase (Q9Y253) ADID : 1 IC50",
                "Serine-protein kinase (Q13315) ADID : 1 IC50",
                "Tyrosine-DNA phosphodiesterase 1 (Q9NUW8) ADID: 514789"]
        molSpreadSheetData.rowPointer.put(5342L,0)
        molSpreadSheetData.rowPointer.put(5345L,0)
        molSpreadSheetData.mssData.put("0_0",new MolSpreadSheetCell("1",MolSpreadSheetCellType.string))
        molSpreadSheetData.mssData.put("0_1",new MolSpreadSheetCell("3888711",MolSpreadSheetCellType.identifier))
        molSpreadSheetData.mssData.put("0_2",new MolSpreadSheetCell("3888711",MolSpreadSheetCellType.greaterThanNumeric))
        molSpreadSheetData.mssData.put("0_3",new MolSpreadSheetCell("3888711",MolSpreadSheetCellType.percentageNumeric))
        molSpreadSheetData.mssData.put("0_4",new MolSpreadSheetCell("3888711",MolSpreadSheetCellType.lessThanNumeric))
        molSpreadSheetData.mssData.put("1_0",new MolSpreadSheetCell("1",MolSpreadSheetCellType.string))
        molSpreadSheetData.mssData.put("1_1",new MolSpreadSheetCell("3888711",MolSpreadSheetCellType.identifier))
        molSpreadSheetData.mssData.put("1_2",new MolSpreadSheetCell("3888711",MolSpreadSheetCellType.greaterThanNumeric))
        molSpreadSheetData.mssData.put("1_3",new MolSpreadSheetCell("3888711",MolSpreadSheetCellType.percentageNumeric))
        molSpreadSheetData.mssData.put("1_4",new MolSpreadSheetCell("3888711",MolSpreadSheetCellType.lessThanNumeric))
    }



    MolSpreadSheetData fakeMe(){
        molSpreadSheetData
    }



 }
