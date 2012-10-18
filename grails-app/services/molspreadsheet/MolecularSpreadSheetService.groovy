package molspreadsheet

import bard.core.adapter.CompoundAdapter
import bard.core.rest.RESTAssayService
import bard.core.rest.RESTExperimentService
import bard.core.rest.RESTProjectService
import bardqueryapi.ActivityOutcome
import bardqueryapi.IQueryService
import bardqueryapi.QueryServiceWrapper
import bardqueryapi.SearchFilter
import com.metasieve.shoppingcart.ShoppingCartService
import org.apache.commons.lang.NotImplementedException
import querycart.CartAssay
import querycart.CartCompound
import querycart.CartProject
import querycart.QueryCartService
import bard.core.*

class MolecularSpreadSheetService {

    QueryCartService queryCartService
    QueryServiceWrapper queryServiceWrapper
    ShoppingCartService shoppingCartService
    IQueryService queryService

    /**
     * High-level routine to pull information out of the query cart and store it into a data structure suitable
     * for passing to the molecular spreadsheet.
     *
     * @return MolSpreadSheetData
     */
    MolSpreadSheetData retrieveExperimentalData() {

        MolSpreadSheetDataBuilderDirector molSpreadSheetDataBuilderDirector = new MolSpreadSheetDataBuilderDirector()

        MolSpreadSheetDataBuilder molSpreadSheetDataBuilder = new MolSpreadSheetDataBuilder(this)

        molSpreadSheetDataBuilderDirector.setMolSpreadSheetDataBuilder(molSpreadSheetDataBuilder)
        molSpreadSheetDataBuilderDirector.constructMolSpreadSheetData(retrieveCartCompoundFromShoppingCart(),
                retrieveCartAssayFromShoppingCart(),
                retrieveCartProjectFromShoppingCart())

        molSpreadSheetDataBuilderDirector.molSpreadSheetData
    }

    /**
     * For a given SpreadSheetActivityList, pull back a map specifying all compounds, along with structures, CIDs, etc
     * @param SpreadSheetActivityList
     * @return
     */
    Map convertSpreadSheetActivityToCompoundInformation(List<SpreadSheetActivity> SpreadSheetActivityList) {
        List<Long> compoundIds = []
        for (SpreadSheetActivity spreadSheetActivity in SpreadSheetActivityList) {
            compoundIds.add(spreadSheetActivity.cid)
        }
        List<SearchFilter> filters = []
        queryService.findCompoundsByCIDs(compoundIds, filters)
    }
    /**
     * When do we have sufficient data to charge often try to build a spreadsheet?
     *
     * @return
     */
    Boolean weHaveEnoughDataToMakeASpreadsheet() {
        boolean returnValue = false
        Map<String, List> itemsInShoppingCart = queryCartService.groupUniqueContentsByType(shoppingCartService)
        if (queryCartService?.totalNumberOfUniqueItemsInCart(itemsInShoppingCart, QueryCartService.cartProject) > 0) {
            returnValue = true
        }
        else if (queryCartService?.totalNumberOfUniqueItemsInCart(itemsInShoppingCart,
                QueryCartService.cartAssay) > 0) {
            returnValue = true
        }
        else if (queryCartService?.totalNumberOfUniqueItemsInCart(itemsInShoppingCart,
                QueryCartService.cartCompound) > 0) {
            returnValue = true
        }
        returnValue
    }

    /**
     * For a set of experiments
     * @param experimentList
     * @param etag
     * @return
     */
    protected List<SpreadSheetActivity> extractMolSpreadSheetData(MolSpreadSheetData molSpreadSheetData, List<Experiment> experimentList, List<Long> compounds) {
        // now step through the data and place into molSpreadSheetData
        List<SpreadSheetActivity> spreadSheetActivityList = []

        // we need to handle each experiment separately ( until NCGC can do this in the background )
        // Note that each experiment corresponds to a column in our spreadsheet
        int columnCount = 0
        for (Experiment experiment in experimentList) {
            // List<Long> collectCompounds = []
            final ServiceIterator<Compound> compoundsTestedInExperimentIter = queryServiceWrapper.restExperimentService.compounds(experiment)
//            while (compoundsTestedInExperimentIter.hasNext()) {
//                final Compound compound = compoundsTestedInExperimentIter.next()
//                CompoundAdapter c = new CompoundAdapter(compound)
//                collectCompounds.add(c.pubChemCID)
//            }
            List<Long> collectCompounds = compoundsTestedInExperimentIter.collect {Compound compound -> new CompoundAdapter(compound).pubChemCID }
            //only do this if compounds is not empty
            if (!compounds.isEmpty()) {
                //find the intersection of the two Compound lists
                collectCompounds = compounds.intersect(collectCompounds)
            }
            if (!collectCompounds.isEmpty()) {
                ServiceIterator<Value> experimentIterator = queryServiceWrapper.restExperimentService.activities(experiment)
                // Now step through the result set and pull back  one value for each compound
                Value experimentValue
                while (experimentIterator.hasNext()) {
                    experimentValue = experimentIterator.next()
                    Long translation = new Long(experimentValue.id.split("\\.")[0])
                    if (!molSpreadSheetData.columnPointer.containsKey(translation)) {
                        molSpreadSheetData.columnPointer.put(translation, columnCount)
                    }
                    spreadSheetActivityList.add(extractActivitiesFromExperiment(experimentValue, new Long(experiment.id.toString())))
                }
            } else {
                molSpreadSheetData.columnPointer.put(experiment.id as Long, columnCount)
            }
            columnCount++
        }
        spreadSheetActivityList
    }
    /**
     *
     * @param molSpreadSheetData
     * @param experimentList
     * @param spreadSheetActivityList
     */
    protected void populateMolSpreadSheetData(MolSpreadSheetData molSpreadSheetData, List<Experiment> experimentList, List<SpreadSheetActivity> spreadSheetActivityList) {
        // now step through the data and place into molSpreadSheetData
        int columnPointer = 0
        Map<String, MolSpreadSheetCell> map = [:]
        // we need to handle each experiment separately ( until NCGC can do this in the background )
        // Note that each experiment corresponds to a column in our spreadsheet
        for (Experiment experiment in experimentList) {

            for (SpreadSheetActivity spreadSheetActivity in spreadSheetActivityList) {
                if (molSpreadSheetData.rowPointer.containsKey(spreadSheetActivity.cid)) {
                    int innerRowPointer = molSpreadSheetData.rowPointer[spreadSheetActivity.cid]
                    int innerColumnCount = molSpreadSheetData.columnPointer[spreadSheetActivity.experimentId]
                    String arrayKey = innerRowPointer.toString() + "_" + (innerColumnCount + 3).toString()
                    SpreadSheetActivityStorage spreadSheetActivityStorage = new SpreadSheetActivityStorage(spreadSheetActivity)

                    MolSpreadSheetCell molSpreadSheetCell = new MolSpreadSheetCell(spreadSheetActivity.interpretHillCurveValue().toString(),
                            MolSpreadSheetCellType.numeric,
                            MolSpreadSheetCellUnit.Molar,
                            spreadSheetActivityStorage)
                    map.put(arrayKey, molSpreadSheetCell)
                }
                // else {
                //println "did not expect cid = ${spreadSheetActivity.cid}"
                //}

            }
            columnPointer++
        }
        molSpreadSheetData.mssData.putAll(map)
        molSpreadSheetData
    }


    List<CartCompound> retrieveCartCompoundFromShoppingCart() {
        List<CartCompound> cartCompoundList = []
        for (CartCompound cartCompound in (queryCartService.groupUniqueContentsByType(shoppingCartService)[(QueryCartService.cartCompound)])) {
            cartCompoundList.add(cartCompound)
        }
        cartCompoundList
    }

    List<CartAssay> retrieveCartAssayFromShoppingCart() {
        List<CartAssay> cartAssayList = []
        for (CartAssay cartAssay in (queryCartService.groupUniqueContentsByType(shoppingCartService)[(QueryCartService.cartAssay)])) {
            cartAssayList.add(cartAssay)
        }
        cartAssayList
    }

    List<CartProject> retrieveCartProjectFromShoppingCart() {
        List<CartProject> cartProjectList = []
        for (CartProject cartProject in (queryCartService.groupUniqueContentsByType(shoppingCartService)[(QueryCartService.cartProject)])) {
            cartProjectList.add(cartProject)
        }
        cartProjectList
    }

    /**
     *
     * @param molSpreadSheetData
     * @param cartCompoundList
     */
    protected void populateMolSpreadSheetRowMetadata(final MolSpreadSheetData molSpreadSheetData, final List<CartCompound> cartCompoundList) {

        // add specific values for the cid column
        int rowCount = 0
        for (CartCompound cartCompound in cartCompoundList) {
            updateMolSpreadSheetDataToReferenceCompound(
                    molSpreadSheetData,
                    rowCount++,
                    cartCompound.compoundId as Long,
                    cartCompound.name,
                    cartCompound.smiles
            )
        }

    }

    /**
     *
     * @param molSpreadSheetData
     * @param compoundAdapterMap
     * @return
     */
    protected void populateMolSpreadSheetRowMetadata(final MolSpreadSheetData molSpreadSheetData, final Map compoundAdapterMap) {

        // Add every compound we can find in the compound adapters map
        List<CompoundAdapter> compoundAdaptersList = compoundAdapterMap.compoundAdapters
        int rowCount = 0
        for (CompoundAdapter compoundAdapter in compoundAdaptersList) {
            String smiles = compoundAdapter.structureSMILES
            Long cid = compoundAdapter.pubChemCID
            String name = compoundAdapter.name
            updateMolSpreadSheetDataToReferenceCompound(molSpreadSheetData, rowCount++, cid, name, smiles)
        }

    }

    /**
     *  add a pointer to a row along with the first two columns
     * @param molSpreadSheetData
     * @param rowCount
     * @param compoundId
     * @param compoundName
     * @param compoundSmiles
     * @return
     */
    protected MolSpreadSheetData updateMolSpreadSheetDataToReferenceCompound(final MolSpreadSheetData molSpreadSheetData,
                                                                             final int rowCount,
                                                                             final Long compoundId,
                                                                             final String compoundName,
                                                                             final String compoundSmiles) {
        // need to be able to map from CID to row location
        molSpreadSheetData.rowPointer.put(compoundId, rowCount)

        // add values for the cid column
        molSpreadSheetData.mssData.put("${rowCount}_0".toString(), new MolSpreadSheetCell(compoundName, compoundSmiles, MolSpreadSheetCellType.image))
        molSpreadSheetData.mssData.put("${rowCount}_1".toString(), new MolSpreadSheetCell(compoundId.toString(), MolSpreadSheetCellType.identifier))
        //we will use this to get the promiscuity score
        molSpreadSheetData.mssData.put("${rowCount}_2".toString(), new MolSpreadSheetCell(compoundId.toString(), MolSpreadSheetCellType.identifier))

        molSpreadSheetData

    }

    /**
     *
     * @param molSpreadSheetData
     * @param experimentList
     */
    protected void populateMolSpreadSheetColumnMetadata(MolSpreadSheetData molSpreadSheetData, List<Experiment> experimentList) {

        // now retrieve the header names from the assays
        molSpreadSheetData.mssHeaders.add("Struct")
        molSpreadSheetData.mssHeaders.add("CID")
        molSpreadSheetData.mssHeaders.add("UNM Promiscuity Analysis")
        for (Experiment experiment in experimentList) {
            molSpreadSheetData.mssHeaders.add(experiment.name)
        }
    }

    /**
     * Convert Assay ODs to expt ids
     * @param cartAssays
     * @return
     */
    protected List<Experiment> assaysToExperiments(final Collection<Assay> assays) {
        final RESTAssayService restAssayService = this.queryServiceWrapper.restAssayService
        List<Experiment> allExperiments = []
        if (!assays.isEmpty()) {
            Iterator<Assay> assayIterator = assays.iterator()
            while (assayIterator.hasNext()) {
                Assay assay = assayIterator.next()
                final ServiceIterator<Experiment> serviceIterator = restAssayService.iterator(assay, Experiment)
                Collection<Experiment> experimentList = serviceIterator.collect()
                allExperiments.addAll(experimentList)
            }
        }

        return allExperiments
    }

    /**
     * Convert Cart assays to Experiments starting with a list of Assay IDs
     * @param cartAssays
     * @return
     */
    protected List<Experiment> assaysToExperiments(List<Experiment> incomingExperimentList, final List<Long> assayIds) {

        List<Experiment> allExperiments
        if (incomingExperimentList == null) {
            allExperiments = []
        }
        else {
            allExperiments = incomingExperimentList
        }
        final RESTAssayService restAssayService = queryServiceWrapper.restAssayService

        for (Long individualAssayIds in assayIds) {
            Assay assay = restAssayService.get(individualAssayIds)
            final ServiceIterator<Experiment> serviceIterator = restAssayService.iterator(assay, Experiment)
            Collection<Experiment> experimentList = serviceIterator.collect()
            allExperiments.addAll(experimentList)
        }

        return allExperiments
    }

    /**
     * Convert Cart assays to Experiments, starting this time with cart Assays
     * @param cartAssays
     * @return
     */
    protected List<Experiment> cartAssaysToExperiments(List<Experiment> incomingExperimentList, final List<CartAssay> cartAssays) {
        List<Long> assayIds = []
        for (CartAssay cartAssay : cartAssays) {
            long assayId = cartAssay.assayId
            assayIds.add(assayId)
        }

        assaysToExperiments(incomingExperimentList, assayIds)
    }

    /**
     *
     * @param incomingExperimentList
     * @param cartCompounds
     * @return
     */
    protected List<Experiment> cartCompoundsToExperiments(final List<CartCompound> cartCompounds) {
        List<Long> compoundIds = []
        for (CartCompound cartCompound in cartCompounds) {
            int compoundId = cartCompound.compoundId
            compoundIds.add(compoundId as Long)
        }


        List<Assay> allAssays = []
        for (Long individualCompoundId in compoundIds) {
            Compound compound = queryServiceWrapper.restCompoundService.get(individualCompoundId)
            Collection<Assay> activeAssaysForThisCompound = queryServiceWrapper.restCompoundService.getTestedAssays(compound, true)  // true = active only
            for (Assay assay in activeAssaysForThisCompound) {
                allAssays << assay
            }
        }

        assaysToExperiments(allAssays)
    }

/**
 *
 * @param cartProjects
 * @return list of Experiment's from a list of CartProject's
 */
    protected List<Experiment> cartProjectsToExperiments(final List<CartProject> cartProjects) {
        List<Long> projectIds = []
        for (CartProject cartProject : cartProjects) {
            long projectId = cartProject.projectId
            projectIds.add(projectId)
        }
        List<Experiment> allExperiments = []
        final RESTProjectService restProjectService = queryServiceWrapper.restProjectService
        final RESTAssayService restAssayService = queryServiceWrapper.restAssayService
        final Collection<Project> projects = restProjectService.get(projectIds)

        for (Project project : projects) {
            final ServiceIterator<Assay> serviceIterator = restProjectService.iterator(project, Assay)
            Collection<Assay> assays = serviceIterator.collect()
            for (Assay assay : assays) {
                final ServiceIterator<Experiment> experimentIterator = restAssayService.iterator(assay, Experiment)
                Collection<Experiment> experimentList = experimentIterator.collect()
                allExperiments.addAll(experimentList)
            }
        }
        return allExperiments
    }

    /**
     *
     * @param experiment
     * @param compoundETag
     * @return
     */
    List<SpreadSheetActivity> findActivitiesForCompounds(final Experiment experiment, final Object compoundETag) {
        final List<SpreadSheetActivity> spreadSheetActivities = []
        final ServiceIterator<Value> experimentIterator = this.queryServiceWrapper.restExperimentService.activities(experiment, compoundETag);
        while (experimentIterator.hasNext()) {
            Value experimentValue = experimentIterator.next()
            if (experimentValue) {
                SpreadSheetActivity spreadSheetActivity = extractActivitiesFromExperiment(experimentValue, new Long(experiment.id.toString()))
                spreadSheetActivities.add(spreadSheetActivity)
            }
        }
        return spreadSheetActivities
    }


    Map findExperimentDataById(final Long experimentId, final Integer top = 10, final Integer skip = 0) {
        List<SpreadSheetActivity> spreadSheetActivities = []
        final RESTExperimentService restExperimentService = queryServiceWrapper.restExperimentService
        long totalNumberOfRecords = 0
        ExperimentValues.ExperimentRole role = null
        Experiment experiment = restExperimentService.get(experimentId)
        if (experiment) {
            role = experiment.role
            final Map activityValuesMap = extractActivityValues(experiment, top, skip)
            final List<Value> activityValues = activityValuesMap.activityValues
            totalNumberOfRecords = activityValuesMap.totalNumberOfRecords
            spreadSheetActivities = createSpreadSheetActivitiesFromActivityValues(experimentId, activityValues)

        }
        return [total: totalNumberOfRecords, spreadSheetActivities: spreadSheetActivities, role: role, experiment: experiment]
    }

    protected List<SpreadSheetActivity> createSpreadSheetActivitiesFromActivityValues(final Long experimentId, final List<Value> activityValues) {
        List<SpreadSheetActivity> spreadSheetActivities = []
        final Iterator<Value> iterator = activityValues.iterator()
        while (iterator.hasNext()) {
            Value experimentValue = iterator.next()
            SpreadSheetActivity spreadSheetActivity = extractActivitiesFromExperiment(experimentValue, experimentId)
            spreadSheetActivities.add(spreadSheetActivity)
        }
        return spreadSheetActivities
    }

    protected Map extractActivityValues(final Experiment experiment, final Integer top = 10, final Integer skip = 0) {
        final RESTExperimentService restExperimentService = queryServiceWrapper.restExperimentService
        final ServiceIterator<Value> experimentValueIterator = restExperimentService.activities(experiment);
        return extractActivityValuesFromExperimentValueIterator(experimentValueIterator, top, skip)
    }

    protected Map extractActivityValuesFromExperimentValueIterator(final ServiceIterator<Value> experimentValueIterator, final Integer top = 10, final Integer skip = 0) {
        List<Value> activityValues = []
        long totalNumberOfRecords = 0
        if (experimentValueIterator?.hasNext()) {
            if (skip == 0) {
                activityValues = experimentValueIterator.next(top)
            }
            else { //There is no way to pass in skip and top to the iterator so we have to do this hack
                //which is not perfect
                activityValues = experimentValueIterator.next(top + skip)
                activityValues = activityValues.subList(skip, activityValues.size())

            }
            totalNumberOfRecords = experimentValueIterator.count
        }
        return [totalNumberOfRecords: totalNumberOfRecords, activityValues: activityValues]
    }
    /**
     *
     * @param experimentValue
     * @return SpreadSheetActivity
     */
    SpreadSheetActivity extractActivitiesFromExperiment(final Value experimentValue, final Long experimentId) {
        final Iterator<Value> experimentValueIterator = experimentValue.children()
        SpreadSheetActivity spreadSheetActivity = new SpreadSheetActivity()
        spreadSheetActivity.experimentId = experimentId
        while (experimentValueIterator?.hasNext()) {
            Value childValue = experimentValueIterator.next()
            addCurrentActivityToSpreadSheet(spreadSheetActivity, childValue)
        }
        return spreadSheetActivity
    }
    /**
     * The ideais to fill up a spreadsheet activity based on
     *  Note hack -- this method has been short-circuited so that a HillCurveValue will cause the method to
     *  push a value back onto readouts and then return
     * @param spreadSheetActivity
     * @param childValue
     */
    void addCurrentActivityToSpreadSheet(final SpreadSheetActivity spreadSheetActivity, final Value childValue) {
        String identifier = childValue.id

         if (childValue instanceof HillCurveValue) {
            spreadSheetActivity.hillCurveValue = childValue
            spreadSheetActivity.readouts.push(childValue)
            return
        }

        switch (identifier) {
            case "potency":
                spreadSheetActivity.potency = (Double) childValue.value
                break
            case "outcome":
                spreadSheetActivity.activityOutcome = ActivityOutcome.findActivityOutcome((Integer) childValue.value)
                break
            case "eid":
                spreadSheetActivity.eid = (Long) childValue.value
                break
            case "cid":
                spreadSheetActivity.cid = (Long) childValue.value
                break
            case "sid":
                spreadSheetActivity.sid = (Long) childValue.value
                break
            default:
                throw new NotImplementedException("Experiment Identifier: ${identifier} is unknown")
        }
    }

}