package molspreadsheet

import bard.core.adapter.CompoundAdapter
import bard.core.rest.spring.compounds.CompoundResult
import bard.core.rest.spring.experiment.ExperimentSearch
import org.apache.log4j.Logger

/**
 * Created with IntelliJ IDEA.
 * User: balexand
 * Date: 9/14/12
 * Time: 11:55 AM
 * To change this template use File | Settings | File Templates.
 */

class MolSpreadSheetDataBuilder {
    static final Logger log = Logger.getLogger(MolSpreadSheetDataBuilder.class)
    protected MolecularSpreadSheetService molecularSpreadSheetService
    MolSpreadSheetData molSpreadSheetData
    Object etag
    Map<String, List<MolSpreadSheetCell>> dataMap = [:]
    Map<Long, Long> mapExperimentIdsToCapAssayIds = [:]
    List<SpreadSheetActivity> spreadSheetActivityList = []


    MolSpreadSheetDataBuilder() {

    }

    MolSpreadSheetDataBuilder(MolecularSpreadSheetService molecularSpreadSheetService) {
        this.molecularSpreadSheetService = molecularSpreadSheetService
    }

    MolSpreadSheetData getMolSpreadSheetData() { molSpreadSheetData }

    /**
     *  This implementation does not require a Query Cart
     * @return
     */
    Map deriveListOfExperimentsFromIds(List<Long> pids, List<Long> adids, List<Long> cids,  Map<Long, Long> mapExperimentIdsToCapAssayIds) {
        List<ExperimentSearch> experimentList = []
        MolSpreadsheetDerivedMethod molSpreadsheetDerivedMethod = null

        try {
            // Any projects can be converted to assays, then assays to experiments
            if (!pids.isEmpty()) {
                experimentList = molecularSpreadSheetService.projectIdsToExperiments(pids, mapExperimentIdsToCapAssayIds)
                molSpreadsheetDerivedMethod = MolSpreadsheetDerivedMethod.NoCompounds_NoAssays_Projects
            }

            // Any assays explicitly selected on the cart are added to the  experimentList
            if (!adids.isEmpty()) {
                experimentList = molecularSpreadSheetService.assayIdsToExperiments(experimentList, adids, mapExperimentIdsToCapAssayIds)
                molSpreadsheetDerivedMethod = MolSpreadsheetDerivedMethod.NoCompounds_Assays_NoProjects
            }

            // If we get to this point and have no experiments selected but we DO have a compound (s), then the user
            //  may be looking to derive their assays on the basis of compounds. We can do that.
            if ((experimentList.isEmpty()) && (!cids.isEmpty())) {
                experimentList = molecularSpreadSheetService.compoundIdsToExperiments(cids, mapExperimentIdsToCapAssayIds)
                molSpreadsheetDerivedMethod = MolSpreadsheetDerivedMethod.Compounds_NoAssays_NoProjects
            }


        } catch (Exception exception) {
            // The shopping cart plugins sometimes throwns an exception though it seems to always keep working
            //TODO: If we know the specific exception that it throws then we should catch the specific one
            log.error(exception)
        }

        return [experimentList: experimentList, molSpreadsheetDerivedMethod: molSpreadsheetDerivedMethod]
    }

    void populateMolSpreadSheetWithCids(final List<ExperimentSearch> experimentList,
                                        final MolSpreadsheetDerivedMethod molSpreadsheetDerivedMethod,
                                        final List<Long> cids) {

        // this is the variable we plan to fill
        molSpreadSheetData = new MolSpreadSheetData()
        molSpreadSheetData.molSpreadsheetDerivedMethod = molSpreadsheetDerivedMethod
        molSpreadSheetData.mapExperimentIdsToCapAssayIds = this.mapExperimentIdsToCapAssayIds

        // temp data sheet
        dataMap = [:]

        // use experiment names to provide names for the columns
        molecularSpreadSheetService.populateMolSpreadSheetColumnMetadata(molSpreadSheetData, experimentList)

        // next deal with the compounds
        if (!cids) {
            // Explicitly specified assay, for which we will retrieve all compounds
            etag = molecularSpreadSheetService.retrieveImpliedCompoundsEtagFromAssaySpecification(experimentList)
            spreadSheetActivityList = molecularSpreadSheetService.extractMolSpreadSheetData(molSpreadSheetData, experimentList, etag)
            Map map = molecularSpreadSheetService.convertSpreadSheetActivityToCompoundInformation(spreadSheetActivityList)
            molecularSpreadSheetService.populateMolSpreadSheetRowMetadata(molSpreadSheetData, map, this.dataMap)

        } else {
            final CompoundResult compoundResult = this.molecularSpreadSheetService.compoundRestService.searchCompoundsByIds(cids)
            final List<CompoundAdapter> compoundAdapters = this.molecularSpreadSheetService.queryService.queryHelperService.compoundsToAdapters(compoundResult)
            etag = molecularSpreadSheetService.generateETagFromCids(cids)
            // Explicitly specified assays and explicitly specified compounds
            molecularSpreadSheetService.populateMolSpreadSheetRowMetadataFromCompoundAdapters(molSpreadSheetData, compoundAdapters, this.dataMap)
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

    void constructMolSpreadSheetDataFromIds(final List<Long> cids,
                                            final List<Long> adids,
                                            final List<Long> pids) {

        Map deriveListOfExperiments = molSpreadSheetDataBuilder.deriveListOfExperimentsFromIds(pids, adids, cids, molSpreadSheetDataBuilder.mapExperimentIdsToCapAssayIds)
        List<ExperimentSearch> experimentList = deriveListOfExperiments.experimentList
        MolSpreadsheetDerivedMethod molSpreadsheetDerivedMethod = deriveListOfExperiments.molSpreadsheetDerivedMethod


        molSpreadSheetDataBuilder.populateMolSpreadSheetWithCids(experimentList, molSpreadsheetDerivedMethod, cids)
    }

}