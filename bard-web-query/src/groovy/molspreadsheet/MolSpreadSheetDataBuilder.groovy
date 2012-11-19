package molspreadsheet

import bard.core.Experiment
import querycart.CartAssay
import querycart.CartCompound
import querycart.CartProject

/**
 * Created with IntelliJ IDEA.
 * User: balexand
 * Date: 9/14/12
 * Time: 11:55 AM
 * To change this template use File | Settings | File Templates.
 */

class MolSpreadSheetDataBuilder {
    protected MolecularSpreadSheetService molecularSpreadSheetService
    MolSpreadSheetData molSpreadSheetData
    List<CartCompound> cartCompoundList = []
    List<CartAssay> cartAssayList = []
    List<CartProject> cartProjectList = []
    Object etag
    Map<String, MolSpreadSheetCell> dataMap = [:]
    List<SpreadSheetActivity> spreadSheetActivityList = []

    MolSpreadSheetDataBuilder() {

    }

    MolSpreadSheetDataBuilder(MolecularSpreadSheetService molecularSpreadSheetService) {
        this.molecularSpreadSheetService = molecularSpreadSheetService
    }

    MolSpreadSheetData getMolSpreadSheetData() { molSpreadSheetData }


    void holdCartResults(List<CartCompound> cartCompoundList, List<CartAssay> cartAssayList, List<CartProject> cartProjectList) {
        this.cartCompoundList = cartCompoundList
        this.cartAssayList = cartAssayList
        this.cartProjectList = cartProjectList
    }

    /**
     *
     * @return
     */
    Map deriveListOfExperiments() {
        List<Experiment> experimentList = []
        MolSpreadsheetDerivedMethod molSpreadsheetDerivedMethod

        try {
            // Any projects can be converted to assays, then assays to experiments
            if (!this.cartProjectList?.isEmpty()) {
                experimentList = molecularSpreadSheetService.cartProjectsToExperiments(this.cartProjectList)
                molSpreadsheetDerivedMethod = MolSpreadsheetDerivedMethod.NoCompounds_NoAssays_Projects
            }

            // Any assays explicitly selected on the cart are added to the  experimentList
            if (!this.cartAssayList?.isEmpty()) {
                experimentList = molecularSpreadSheetService.cartAssaysToExperiments(experimentList, this.cartAssayList)
                molSpreadsheetDerivedMethod = MolSpreadsheetDerivedMethod.NoCompounds_Assays_NoProjects
            }

            // If we get to this point and have no experiments selected but we DO have a compound (s), then the user
            //  may be looking to derive their assays on the basis of compounds. We can do that.
            if ((experimentList.isEmpty()) && (!this.cartCompoundList?.isEmpty())) {
                experimentList = molecularSpreadSheetService.cartCompoundsToExperiments(this.cartCompoundList)
                molSpreadsheetDerivedMethod = MolSpreadsheetDerivedMethod.Compounds_NoAssays_NoProjects
            }


        } catch (Exception exception) {
            // The shopping cart plugins sometimes throwns an exception though it seems to always keep working
            //TODO: If we know the specific exception that it throws then we should catch the specific one
            exception.printStackTrace()
        }

        return [experimentList: experimentList, molSpreadsheetDerivedMethod: molSpreadsheetDerivedMethod]
    }




    void populateMolSpreadSheet(List<Experiment> experimentList, MolSpreadsheetDerivedMethod molSpreadsheetDerivedMethod) {

        // this is the variable we plan to fill
        molSpreadSheetData = new MolSpreadSheetData()
        molSpreadSheetData.molSpreadsheetDerivedMethod = molSpreadsheetDerivedMethod

        // temp data sheet
        dataMap = [:]

        // use experiment names to provide names for the columns
        molecularSpreadSheetService.populateMolSpreadSheetColumnMetadata(molSpreadSheetData, experimentList)

        // next deal with the compounds
        if (cartCompoundList.isEmpty()) {
            // Explicitly specified assay, for which we will retrieve all compounds
            etag = molecularSpreadSheetService.retrieveImpliedCompoundsEtagFromAssaySpecification(experimentList)
            spreadSheetActivityList = molecularSpreadSheetService.extractMolSpreadSheetData(molSpreadSheetData, experimentList, etag)
            // spreadSheetActivityList = molecularSpreadSheetService.extractMolSpreadSheetData(molSpreadSheetData, experimentList, [])
            Map map = molecularSpreadSheetService.convertSpreadSheetActivityToCompoundInformation(spreadSheetActivityList)
            molecularSpreadSheetService.populateMolSpreadSheetRowMetadata(molSpreadSheetData, map, this.dataMap)

        } else {

            // Explicitly specified assays and explicitly specified compounds
            molecularSpreadSheetService.populateMolSpreadSheetRowMetadata(molSpreadSheetData, cartCompoundList, this.dataMap)
            etag = molecularSpreadSheetService.generateETagFromCartCompounds(cartCompoundList)
            spreadSheetActivityList = molecularSpreadSheetService.extractMolSpreadSheetData(molSpreadSheetData, experimentList, etag)
        }

        // finally deal with the data
        molecularSpreadSheetService.populateMolSpreadSheetData(molSpreadSheetData, experimentList, spreadSheetActivityList, this.dataMap)
        molecularSpreadSheetService.fillInTheMissingCellsAndConvertToExpandedMatrix(molSpreadSheetData, this.dataMap)
        molecularSpreadSheetService.prepareMapOfColumnsToAssay(molSpreadSheetData)
    }


}

class MolSpreadSheetDataBuilderDirector {
    private MolSpreadSheetDataBuilder molSpreadSheetDataBuilder

    void setMolSpreadSheetDataBuilder(MolSpreadSheetDataBuilder molSpreadSheetDataBuilder) {
        this.molSpreadSheetDataBuilder = molSpreadSheetDataBuilder
    }

    MolSpreadSheetData getMolSpreadSheetData() {
        this.molSpreadSheetDataBuilder.molSpreadSheetData
    }

    void constructMolSpreadSheetData(List<CartCompound> cartCompoundList,
                                     List<CartAssay> cartAssayList,
                                     List<CartProject> cartProjectList) {

        molSpreadSheetDataBuilder.holdCartResults(cartCompoundList, cartAssayList, cartProjectList)

        Map deriveListOfExperiments = molSpreadSheetDataBuilder.deriveListOfExperiments()
        List<Experiment> experimentList =  deriveListOfExperiments.experimentList
        MolSpreadsheetDerivedMethod molSpreadsheetDerivedMethod = deriveListOfExperiments.molSpreadsheetDerivedMethod

        molSpreadSheetDataBuilder.populateMolSpreadSheet(experimentList, molSpreadsheetDerivedMethod)
    }

}