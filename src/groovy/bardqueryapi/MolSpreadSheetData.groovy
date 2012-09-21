package bardqueryapi

import java.math.MathContext

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
    LinkedHashMap<Long,Integer> columnPointer
    List mssHeaders = new ArrayList()

    MolSpreadSheetData()  {
        mssData = new LinkedHashMap<String,MolSpreadSheetCell> ()
        rowPointer = new LinkedHashMap<Long,Integer>()
        columnPointer = new LinkedHashMap<Long,Integer>()
        mssHeaders = new ArrayList()
    }

    // test data
    MolSpreadSheetData(String s)  {     // temp fake data
        mssData = new LinkedHashMap<String,MolSpreadSheetCell> ()
        rowPointer = new LinkedHashMap<Long,Integer>()
        mssHeaders = new ArrayList()
        mssData.put("0_0", new MolSpreadSheetCell("3-methyl-2-oxopentanoic acid","CCC(C)C(=O)C(O)=O",MolSpreadSheetCellType.image))
        mssData.put("0_1", new MolSpreadSheetCell("3888711",MolSpreadSheetCellType.identifier))
        mssData.put("0_2", new MolSpreadSheetCell("14",MolSpreadSheetCellType.numeric, MolSpreadSheetCellUnit.Micromolar))
        mssData.put("0_3", new MolSpreadSheetCell("20",MolSpreadSheetCellType.percentageNumeric))
        mssData.put("0_4", new MolSpreadSheetCell("10",MolSpreadSheetCellType.percentageNumeric))
        mssData.put("0_5", new MolSpreadSheetCell("2.1",MolSpreadSheetCellType.greaterThanNumeric, MolSpreadSheetCellUnit.Micromolar))
        mssData.put("0_6", new MolSpreadSheetCell("800",MolSpreadSheetCellType.numeric, MolSpreadSheetCellUnit.Nanomolar))
        mssData.put("1_0", new MolSpreadSheetCell("5-amino-2,5-dioxopentanoic acid","NC(=O)CCC(=O)C(O)=O",MolSpreadSheetCellType.image))
        mssData.put("1_1", new MolSpreadSheetCell("3888712",MolSpreadSheetCellType.identifier))
        mssData.put("1_2", new MolSpreadSheetCell("11",MolSpreadSheetCellType.greaterThanNumeric, MolSpreadSheetCellUnit.Micromolar))
        mssData.put("1_3", new MolSpreadSheetCell("25",MolSpreadSheetCellType.percentageNumeric))
        mssData.put("1_4", new MolSpreadSheetCell("30",MolSpreadSheetCellType.percentageNumeric))
        mssData.put("1_5", new MolSpreadSheetCell("1.0",MolSpreadSheetCellType.greaterThanNumeric, MolSpreadSheetCellUnit.Micromolar))
        mssData.put("1_6", new MolSpreadSheetCell("640",MolSpreadSheetCellType.numeric, MolSpreadSheetCellUnit.Nanomolar))
        mssData.put("2_0", new MolSpreadSheetCell("4-benzhydryloxy-1-methylpiperidine hydrochloride","Cl.CN1CCC(CC1)OC(C1=CC=CC=C1)C1=CC=CC=C1",MolSpreadSheetCellType.image))
        mssData.put("2_1", new MolSpreadSheetCell("3888713",MolSpreadSheetCellType.identifier))
        mssData.put("2_2", new MolSpreadSheetCell("6.2",MolSpreadSheetCellType.greaterThanNumeric, MolSpreadSheetCellUnit.Micromolar))
        mssData.put("2_3", new MolSpreadSheetCell("10",MolSpreadSheetCellType.percentageNumeric))
        mssData.put("2_4", new MolSpreadSheetCell("15",MolSpreadSheetCellType.percentageNumeric))
        mssData.put("2_5", new MolSpreadSheetCell("760",MolSpreadSheetCellType.greaterThanNumeric, MolSpreadSheetCellUnit.Nanomolar))
        mssData.put("2_6", new MolSpreadSheetCell("880",MolSpreadSheetCellType.greaterThanNumeric, MolSpreadSheetCellUnit.Nanomolar))
        mssData.put("3_0", new MolSpreadSheetCell("2-oxoethylphosphonic acid","OP(O)(=O)CC=O",MolSpreadSheetCellType.image))
        mssData.put("3_1", new MolSpreadSheetCell("3888713",MolSpreadSheetCellType.identifier))
        mssData.put("3_2", new MolSpreadSheetCell("3.0",MolSpreadSheetCellType.greaterThanNumeric, MolSpreadSheetCellUnit.Micromolar))
        mssData.put("3_3", new MolSpreadSheetCell("8",MolSpreadSheetCellType.percentageNumeric))
        mssData.put("3_4", new MolSpreadSheetCell("10",MolSpreadSheetCellType.percentageNumeric))
        mssData.put("3_5", new MolSpreadSheetCell("550",MolSpreadSheetCellType.greaterThanNumeric, MolSpreadSheetCellUnit.Nanomolar))
        mssData.put("3_6", new MolSpreadSheetCell("600",MolSpreadSheetCellType.greaterThanNumeric, MolSpreadSheetCellUnit.Nanomolar))
        mssHeaders.add("Struct")
        mssHeaders.add("CID")
        mssHeaders.add("DNA polymerase (Q9Y253) ADID : 1 <br/>IC50")
        mssHeaders.add("Serine-protein kinase (Q13315) ADID : <br/>PI")
        mssHeaders.add("Dose Response Assay for Formylpeptide Receptor (Q74RGA) ADID: 496522<br/>PI")
        mssHeaders.add("Tyrosine-DNA phosphodiesterase 1 (Q9NUW8) ADID: 514789<br/>AC50")
        mssHeaders.add("Flop endonuclease 1 (P 39748) ADID: 96789844<br/>IC50")
        rowPointer.put(5342L,0)
        rowPointer.put(5345L,1)
        rowPointer.put(5346L,2)
        rowPointer.put(5347L,3)
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
            if (molSpreadSheetCell.molSpreadSheetCellType == MolSpreadSheetCellType.image) {
                returnValue = molSpreadSheetCell.retrieveValues()
            }  else {
                returnValue["value"] = mssData[key].toString()
            }
        }   else {  // This is a critical error.  Try to cover all the bases so we don't crash at least.
            returnValue.put("value","-")
            returnValue.put("name", "Unknown name")
            returnValue.put("smiles","Unknown smiles")
        }
        returnValue
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

}
