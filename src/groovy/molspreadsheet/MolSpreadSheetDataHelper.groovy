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
    List<SpreadSheetActivity> spreadSheetActivityList = []

    MolSpreadSheetDataBuilder(){

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
    List<Experiment> deriveListOfExperiments() {
        List<Experiment> experimentList = []


        try {
            // Any projects can be converted to assays, then assays to experiments
            if (this.cartProjectList?.size() > 0) {
                experimentList = molecularSpreadSheetService.cartProjectsToExperiments(this.cartProjectList)
            }

            // Any assays explicitly selected on the cart are added to the  experimentList
            if (this.cartAssayList?.size() > 0) {
                experimentList = molecularSpreadSheetService.cartAssaysToExperiments(experimentList, this.cartAssayList)
            }

            // If we get to this point and have no experiments selected but we DO have a compound (s), then the user
            //  may be looking to derive their assays on the basis of compounds. We can do that.
            if ((experimentList.size() == 0) && (this.cartCompoundList?.size() > 0)) {
                experimentList = molecularSpreadSheetService.cartCompoundsToExperiments(this.cartCompoundList)
            }


        }  catch (Exception exception) {
            // The shopping cart plugins sometimes throwns an exception though it seems to always keep working
            exception.printStackTrace()
        }
        experimentList
    }




    void populateMolSpreadSheet(List<Experiment> experimentList) {

        // this is the variable we plan to fill
        molSpreadSheetData = new MolSpreadSheetData()

        // use experiment names to provide names for the columns
        molecularSpreadSheetService.populateMolSpreadSheetColumnMetadata(molSpreadSheetData, experimentList)

        // next deal with the compounds
        if (cartCompoundList.size() > 0) {

            // Explicitly specified assays and explicitly specified compounds
            molecularSpreadSheetService.populateMolSpreadSheetRowMetadata(molSpreadSheetData, cartCompoundList)
            //etag = molecularSpreadSheetService.generateETagFromCartCompounds(cartCompoundList)
            List<Long> compoundsSelected = cartCompoundList.collect {CartCompound cartCompound ->
                cartCompound.compoundId.toLong()
            }
            //spreadSheetActivityList = molecularSpreadSheetService.extractMolSpreadSheetData(molSpreadSheetData, experimentList, etag)
            spreadSheetActivityList = molecularSpreadSheetService.extractMolSpreadSheetData(molSpreadSheetData, experimentList, compoundsSelected)

        } else if (cartCompoundList.size() == 0) {

            // Explicitly specified assay, for which we will retrieve all compounds
            // etag = molecularSpreadSheetService.retrieveImpliedCompoundsEtagFromAssaySpecification(experimentList)
            // spreadSheetActivityList = molecularSpreadSheetService.extractMolSpreadSheetData(molSpreadSheetData, experimentList, etag)
            spreadSheetActivityList = molecularSpreadSheetService.extractMolSpreadSheetData(molSpreadSheetData, experimentList, [])
            Map map = molecularSpreadSheetService.convertSpreadSheetActivityToCompoundInformation(spreadSheetActivityList)
            molecularSpreadSheetService.populateMolSpreadSheetRowMetadata(molSpreadSheetData, map)

        }
        // finally deal with the data
        molecularSpreadSheetService.populateMolSpreadSheetData(molSpreadSheetData, experimentList, spreadSheetActivityList)
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

        List<Experiment> experimentList = molSpreadSheetDataBuilder.deriveListOfExperiments()

        molSpreadSheetDataBuilder.populateMolSpreadSheet(experimentList)

    }

}