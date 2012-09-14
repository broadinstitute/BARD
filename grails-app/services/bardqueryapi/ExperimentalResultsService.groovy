package bardqueryapi

import bard.core.Experiment
import bard.core.Compound
import bard.core.Value
import bard.core.ServiceIterator

import static junit.framework.Assert.assertNotNull

class ExperimentalResultsService {

    MolSpreadSheetData fakeResultsForNow(){
        def  molSpreadSheetData = new MolSpreadSheetData()
        molSpreadSheetData.mssHeaders = ["Chemical Structure",
                            "CID",
                            "DNA polymerase (Q9Y253) ADID : 1 IC50",
                            "Serine-protein kinase (Q13315) ADID : 1 IC50",
                            "Tyrosine-DNA phosphodiesterase 1 (Q9NUW8) ADID: 514789"]
        int rowCount = 2
        molSpreadSheetData.mssData.put("1_1",new MolSpreadSheetCell("1",MolSpreadSheetCellType.string))
        molSpreadSheetData.mssData.put("1_2",new MolSpreadSheetCell("3888711",MolSpreadSheetCellType.identifier))
        molSpreadSheetData.mssData.put("1_3",new MolSpreadSheetCell("3888711",MolSpreadSheetCellType.greaterThanNumeric))
        molSpreadSheetData.mssData.put("1_4",new MolSpreadSheetCell("3888711",MolSpreadSheetCellType.percentageNumeric))
        molSpreadSheetData.mssData.put("1_5",new MolSpreadSheetCell("3888711",MolSpreadSheetCellType.lessThanNumeric))
        molSpreadSheetData.mssData.put("2_1",new MolSpreadSheetCell("1",MolSpreadSheetCellType.string))
        molSpreadSheetData.mssData.put("2_2",new MolSpreadSheetCell("3888711",MolSpreadSheetCellType.identifier))
        molSpreadSheetData.mssData.put("2_3",new MolSpreadSheetCell("3888711",MolSpreadSheetCellType.greaterThanNumeric))
        molSpreadSheetData.mssData.put("2_4",new MolSpreadSheetCell("3888711",MolSpreadSheetCellType.percentageNumeric))
        molSpreadSheetData.mssData.put("2_5",new MolSpreadSheetCell("3888711",MolSpreadSheetCellType.lessThanNumeric))
    }

 }
