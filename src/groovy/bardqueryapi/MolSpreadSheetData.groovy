package bardqueryapi

import bard.core.Experiment

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

    SpreadSheetActivity findSpreadSheetActivity(int rowCnt, int colCnt){
        SpreadSheetActivity spreadSheetActivity = null
        String key = "${rowCnt}_${colCnt}"
        MolSpreadSheetCell molSpreadSheetCell
        if (mssData.containsKey(key)) {
            molSpreadSheetCell = mssData[key]
            spreadSheetActivity = molSpreadSheetCell.spreadSheetActivity
        }
        return spreadSheetActivity
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



class MolSpreadSheetDataBuilder{
    protected MolecularSpreadSheetService molecularSpreadSheetService
    protected  MolSpreadSheetData molSpreadSheetData
    List<CartCompound> cartCompoundList
    List<CartAssay> cartAssayList
    List<CartProject> cartProjectList
    Object etag
    List<SpreadSheetActivity> SpreadSheetActivityList

    public MolSpreadSheetDataBuilder(MolecularSpreadSheetService molecularSpreadSheetService){
        this.molecularSpreadSheetService = molecularSpreadSheetService
    }

    public  MolSpreadSheetData getMolSpreadSheetData() {    molSpreadSheetData  }
    public void createNewMolSpreadSheetData() {
        molSpreadSheetData=new MolSpreadSheetData()
    }

    public void holdCartResults(List<CartCompound> cartCompoundList,List<CartAssay> cartAssayList,List<CartProject> cartProjectList){
        this.cartCompoundList = cartCompoundList
        this.cartAssayList =  cartAssayList
        this.cartProjectList = cartProjectList
    }



    public List<Experiment> deriveListOfExperiments() {
        List<Experiment> experimentList

        // Any projects can be converted to assays, then assays to experiments
        if (this.cartProjectList?.size() > 0) {
//            Collection<Assay> assayCollection = molecularSpreadSheetService.cartProjectsToAssays(cartProjectList)
//            experimentList = molecularSpreadSheetService.assaysToExperiments(assayCollection)
            experimentList = molecularSpreadSheetService.cartProjectsToExperiments(this.cartProjectList)
        }

        // Any assays explicitly selected on the cart are added to the  experimentList
        if (this.cartAssayList?.size() > 0) {
            experimentList = molecularSpreadSheetService.cartAssaysToExperiments(experimentList, this.cartAssayList)
        }


        experimentList
    }




    public void populateMolSpreadSheet(List<Experiment> experimentList) {
        molSpreadSheetData = new MolSpreadSheetData()
        // next deal with the compounds
        if (experimentList.size() > 0) {

            if (cartCompoundList.size() > 0) {
                // Explicitly specified assays and explicitly specified compounds
                molSpreadSheetData = molecularSpreadSheetService.populateMolSpreadSheetRowMetadata(molSpreadSheetData, cartCompoundList)
                molSpreadSheetData = molecularSpreadSheetService.populateMolSpreadSheetColumnMetadata(molSpreadSheetData, experimentList)
                etag = molecularSpreadSheetService.generateETagFromCartCompounds(cartCompoundList)
                SpreadSheetActivityList = molecularSpreadSheetService.extractMolSpreadSheetData(molSpreadSheetData, experimentList, etag)
            } else if (cartCompoundList.size() == 0) {
                // Explicitly specified assay, for which we will retrieve all compounds
                etag = molecularSpreadSheetService.retrieveImpliedCompoundsEtagFromAssaySpecification(experimentList)
                molSpreadSheetData = molecularSpreadSheetService.populateMolSpreadSheetColumnMetadata(molSpreadSheetData, experimentList)
                SpreadSheetActivityList = molecularSpreadSheetService.extractMolSpreadSheetData(molSpreadSheetData, experimentList, etag)
                Map map = molecularSpreadSheetService.convertSpreadSheetActivityToCompoundInformation(SpreadSheetActivityList)
                molSpreadSheetData = molecularSpreadSheetService.populateMolSpreadSheetRowMetadata(molSpreadSheetData, map)
            }
            // finally deal with the data
            molecularSpreadSheetService.populateMolSpreadSheetData(molSpreadSheetData, experimentList, SpreadSheetActivityList)
        }
    }


}

class MolSpreadSheetDataBuilderDirector {
    private MolSpreadSheetDataBuilder molSpreadSheetDataBuilder

    public setMolSpreadSheetDataBuilder(MolSpreadSheetDataBuilder molSpreadSheetDataBuilder) {
        this.molSpreadSheetDataBuilder = molSpreadSheetDataBuilder
    }

    public MolSpreadSheetData getMolSpreadSheetData() {
        molSpreadSheetDataBuilder.getMolSpreadSheetData()
    }

    public void constructMolSpreadSheetData(List<CartCompound> cartCompoundList,
                                            List<CartAssay> cartAssayList,
                                            List<CartProject> cartProjectList) {

        molSpreadSheetDataBuilder.holdCartResults(cartCompoundList, cartAssayList, cartProjectList)

        List<Experiment> experimentList = molSpreadSheetDataBuilder.deriveListOfExperiments()

        molSpreadSheetDataBuilder.populateMolSpreadSheet(experimentList)

    }

}