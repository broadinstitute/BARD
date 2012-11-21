package molspreadsheet

import bard.core.adapter.CompoundAdapter
import bard.core.interfaces.ExperimentRole
import bard.core.interfaces.SearchResult
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
    final static int START_DYNAMIC_COLUMNS = 4 //Where to start the dynamic columns
    QueryCartService queryCartService
    QueryServiceWrapper queryServiceWrapper
    ShoppingCartService shoppingCartService
    IQueryService queryService



    protected LinkedHashMap<String, Object> prepareForExport ( MolSpreadSheetData molSpreadSheetData ) {
        LinkedHashMap<String, Object> returnValue = []
        returnValue ["labels"]   = ["molstruct": "molecular structure"]
        returnValue ["labels"] << ["cid": "CID"]
        int column = 0
        for (String colHeader in molSpreadSheetData.getColumns()){
            if (column == 2) {
                returnValue ["labels"] << [("c${column}" as String): "$colHeader"]
            }
            if (column > 2) {
                returnValue ["labels"] << [("c${column}" as String): "${molSpreadSheetData.mapColumnsToAssay[column]} ${colHeader}"]
            }
            column++
        }
        returnValue["fields"] = []
        returnValue["labels"].each {key, value ->
            returnValue["fields"] << key
        }
        //leave out promicuity for now
        returnValue["fields"] -= "c2"
        returnValue ["data"]   = []
        for (int rowCnt in 0..(molSpreadSheetData.getRowCount() - 1)){
            LinkedHashMap<String, String> mapForThisRow  = []
            mapForThisRow << ["molstruct": """${molSpreadSheetData.displayValue(rowCnt, 0)?."smiles"}""".toString()]
            mapForThisRow << ["cid": """${molSpreadSheetData.displayValue(rowCnt, 1)?."value"}""".toString()]
            mapForThisRow << ["c3": """${molSpreadSheetData.displayValue(rowCnt, 3)?."value"}""".toString()]
            if (molSpreadSheetData.getColumnCount() > 4) {
                for (int colCnt in (4..molSpreadSheetData.getColumnCount() - 1)) {
                    SpreadSheetActivityStorage spreadSheetActivityStorage = molSpreadSheetData.findSpreadSheetActivity(rowCnt, colCnt)
                    if (spreadSheetActivityStorage != null)  {
                        HillCurveValueHolder hillCurveValueHolder = spreadSheetActivityStorage.getHillCurveValueHolderList()[0]
                        mapForThisRow << [("c${colCnt}" as String): hillCurveValueHolder.toString() ]
                    }  else {
                        mapForThisRow << [("c${colCnt}" as String): "not tested in this experiment"]
                    }

                }
            }
            returnValue["data"] << mapForThisRow
        }
        return returnValue

    }




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



    protected Object retrieveImpliedCompoundsEtagFromAssaySpecification(List<Experiment> experimentList) {
        Object etag = null
        for (Experiment experiment in experimentList) {
            final SearchResult<Compound> compoundSearchResult = this.queryServiceWrapper.restExperimentService.compounds(experiment)
            List<Compound> singleExperimentCompoundList = compoundSearchResult.searchResults
            List<Long> idList = singleExperimentCompoundList*.id as List<Long>
            if (etag == null) {
                etag = this.queryServiceWrapper.restCompoundService.newETag("${new Date().toString()}",
                        idList);
            }
            else if ((singleExperimentCompoundList != null) &&
                    (singleExperimentCompoundList.size() > 0)) {
                this.queryServiceWrapper.restCompoundService.putETag(etag,
                        idList);
            }

        }
        return etag
    }

//
//    protected List<SpreadSheetActivity> extractMolSpreadSheetData(MolSpreadSheetData molSpreadSheetData, List<Experiment> experimentList) {
//        // now step through the data and place into molSpreadSheetData
//        List<SpreadSheetActivity> spreadSheetActivityList = new ArrayList<SpreadSheetActivity>()
//
//        // we need to handle each experiment separately ( until NCGC can do this in the background )
//        // Note that each experiment corresponds to a column in our spreadsheet
//        int columnCount = 0
//        for (Experiment experiment in experimentList) {
//
//            ServiceIterator<Value> experimentIterator = queryServiceWrapper.restExperimentService.activities(experiment)
//
//            // Now step through the result set and pull back  one value for each compound
//            Value experimentValue
//            while (experimentIterator.hasNext()) {
//                experimentValue = experimentIterator.next()
//                Long translation = new Long(experimentValue.id.split("\\.")[0])
//                if (!molSpreadSheetData.columnPointer.containsKey(translation)) {
//                    molSpreadSheetData.columnPointer.put(translation, columnCount)
//                }
//                spreadSheetActivityList.add(extractActivitiesFromExperiment(experimentValue))
//            }
//            columnCount++
//        }
//        spreadSheetActivityList
//    }
//
//
    /**
     * For a set of experiments
     * @param experimentList
     * @param etag
     * @return
     */
    protected List<SpreadSheetActivity> extractMolSpreadSheetData(MolSpreadSheetData molSpreadSheetData, List<Experiment> experimentList, Object etag = null) {
        // now step through the data and place into molSpreadSheetData
        List<SpreadSheetActivity> spreadSheetActivityList = new ArrayList<SpreadSheetActivity>()

        // we need to handle each experiment separately ( until NCGC can do this in the background )
        // Note that each experiment corresponds to a column in our spreadsheet
        int columnCount = 0
        for (Experiment experiment in experimentList) {

            SearchResult<Value> experimentSearchResults
            if (etag == null) {
                experimentSearchResults = queryServiceWrapper.restExperimentService.activities(experiment)
            } else {
                experimentSearchResults = queryServiceWrapper.restExperimentService.activities(experiment, etag)
            }

            // Now step through the result set and pull back  one value for each compound
            //Value experimentValue
            // Integer experimentCount = 0
            for (Value experimentValue : experimentSearchResults.searchResults) {
                Long translation = new Long(experimentValue.id.split("\\.")[0])
                if (!molSpreadSheetData.columnPointer.containsKey(translation)) {
                    molSpreadSheetData.columnPointer.put(translation, columnCount)
                }
                spreadSheetActivityList.add(extractActivitiesFromExperiment(molSpreadSheetData, columnCount, experimentValue))
            }
            columnCount++
        }
        spreadSheetActivityList
    }

    /**
     * Now we have all of the available data in the dataMap, but that map may have holes ( where the compound wasn't tested with a particular assay)
     * and it may also have cells with multiple values. This method is intended to smooth out the rough spots -- make a new map with exactly one value
     * for each cell  in the molecular spreadsheet.  Some of those cells may be null, but that's fine, since we can print out "Not tested in this experiment"
     * on the GSP when we bump into one of them.
     * @param molSpreadSheetData
     * @param dataMap
     */
    protected void fillInTheMissingCellsAndConvertToExpandedMatrix(MolSpreadSheetData molSpreadSheetData, Map<String, MolSpreadSheetCell> dataMap) {
        for (int row in 0..(molSpreadSheetData.rowCount - 1)) {
            int exptNumberColTracker = 0
            for (int col in 0..(molSpreadSheetData.superColumnCount - 1)) {
                String key = "${row}_${col}"
                MolSpreadSheetCell molSpreadSheetCell = null
                SpreadSheetActivityStorage spreadSheetActivityStorage = null
                if (dataMap.containsKey(key)) {
                    molSpreadSheetCell = dataMap[key]
                    spreadSheetActivityStorage = molSpreadSheetCell.spreadSheetActivityStorage
                }
                for (int experimentNum in 0..molSpreadSheetData.mssHeaders[col].size() - 1) {
                    String finalKey = "${row}_${(exptNumberColTracker++)}"
                    if (spreadSheetActivityStorage == null) {
                        if (molSpreadSheetCell != null) {
                            molSpreadSheetData.mssData[finalKey] = new MolSpreadSheetCell(molSpreadSheetCell)
                        } else {
                            molSpreadSheetData.mssData[finalKey] = new MolSpreadSheetCell()
                        }
                    } else {
                        molSpreadSheetData.mssData[finalKey] = new MolSpreadSheetCell(molSpreadSheetCell, experimentNum)
                    }
                }
            }

        }
    }







    protected Object generateETagFromCartCompounds(List<CartCompound> cartCompoundList) {
        List<Long> cartCompoundIdList = new ArrayList<Long>()
        for (CartCompound cartCompound in cartCompoundList)
            cartCompoundIdList.add(cartCompound.externalId)
        Date date = new Date()

        queryServiceWrapper.restCompoundService.newETag(date.toTimestamp().toString(), cartCompoundIdList);
    }
    /**
     * The goal of this method is to take all the information we have  and store it in a map. Note that we are
     * crossing every compound  with every assay, so there may be instances where we have no data
     * for a particular compound/activity combination.  That's fine-- don't fill in the entries
     * for which we don't have data.  Alternatively, sometimes we will have multiple replicates
     * for a single  compound/assay.  In this case those multiple values  can hang off of  the
     * SpreadSheetActivity.
     *
     * @param molSpreadSheetData
     * @param experimentList
     * @param spreadSheetActivityList
     */
    protected void populateMolSpreadSheetData(final MolSpreadSheetData molSpreadSheetData,
                                              final List<Experiment> experimentList,
                                              final List<SpreadSheetActivity> spreadSheetActivityList,
                                              final Map<String, MolSpreadSheetCell> dataMap) {
        // now step through the data and place into molSpreadSheetData
        int columnPointer = 0
        // we need to handle each experiment separately ( until NCGC can do this in the background )
        // Note that each experiment corresponds to a column in our spreadsheet
        for (Experiment experiment in experimentList) {

            for (SpreadSheetActivity spreadSheetActivity in spreadSheetActivityList) {
                if (molSpreadSheetData.rowPointer.containsKey(spreadSheetActivity.cid)) {
                    int innerRowPointer = molSpreadSheetData.rowPointer[spreadSheetActivity.cid]
                    int innerColumnCount = molSpreadSheetData.columnPointer[spreadSheetActivity.eid]
                    String arrayKey = "${innerRowPointer}_${innerColumnCount + START_DYNAMIC_COLUMNS}"
                    MolSpreadSheetCell molSpreadSheetCell = new MolSpreadSheetCell(spreadSheetActivity)
                    dataMap[arrayKey] = molSpreadSheetCell
                }
                else {
                    println "did not expect cid = ${spreadSheetActivity.cid}"
                }

            }
            columnPointer++
        }
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
    protected void populateMolSpreadSheetRowMetadata(final MolSpreadSheetData molSpreadSheetData, final List<CartCompound> cartCompoundList, Map<String, MolSpreadSheetCell> dataMap) {

        // add specific values for the cid column
        int rowCount = 0
        for (CartCompound cartCompound in cartCompoundList) {
            updateMolSpreadSheetDataToReferenceCompound(
                    molSpreadSheetData,
                    rowCount++,
                    cartCompound.externalId as Long,
                    cartCompound.name,
                    cartCompound.smiles,
                    dataMap
            )
        }

    }

    /**
     *
     * @param molSpreadSheetData
     * @param compoundAdapterMap
     * @return
     */
    protected void populateMolSpreadSheetRowMetadata(final MolSpreadSheetData molSpreadSheetData, final Map compoundAdapterMap, Map<String, MolSpreadSheetCell> dataMap) {

        // Add every compound we can find in the compound adapters map
        List<CompoundAdapter> compoundAdaptersList = compoundAdapterMap.compoundAdapters
        int rowCount = 0
        for (CompoundAdapter compoundAdapter in compoundAdaptersList) {
            String smiles = compoundAdapter.structureSMILES
            Long cid = compoundAdapter.pubChemCID
            String name = compoundAdapter.name
            updateMolSpreadSheetDataToReferenceCompound(molSpreadSheetData, rowCount++, cid, name, smiles, dataMap)
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
                                                                             final String compoundSmiles,
                                                                             Map<String, MolSpreadSheetCell> dataMap) {
        // need to be able to map from CID to row location
        molSpreadSheetData.rowPointer.put(compoundId, rowCount)

        //TODO find a generic way to do this. It seems there are too many places to add new constant columns
        // add values for the cid column
        dataMap.put("${rowCount}_0".toString(), new MolSpreadSheetCell(compoundName, compoundSmiles, MolSpreadSheetCellType.image))
        dataMap.put("${rowCount}_1".toString(), new MolSpreadSheetCell(compoundId.toString(), MolSpreadSheetCellType.identifier))
        //we will use this to get the promiscuity score
        dataMap.put("${rowCount}_2".toString(), new MolSpreadSheetCell(compoundId.toString(), MolSpreadSheetCellType.identifier))
        //we will use this to get the 'active vrs tested' column
        int activeAssays = this.queryService.getNumberTestedAssays(compoundId,true)
        int testedAssays  = this.queryService.getNumberTestedAssays(compoundId,false)
        dataMap.put("${rowCount}_3".toString(),  new MolSpreadSheetCell("${activeAssays} / ${testedAssays}", MolSpreadSheetCellType.string))

        return molSpreadSheetData

    }

    /**
     *
     * @param molSpreadSheetData
     * @param experimentList
     */
    protected void populateMolSpreadSheetColumnMetadata(MolSpreadSheetData molSpreadSheetData, List<Experiment> experimentList) {

        // now retrieve the header names from the assays
        molSpreadSheetData.mssHeaders << ["Struct"]
        molSpreadSheetData.mssHeaders << ["CID"]
        molSpreadSheetData.mssHeaders << ["UNM Promiscuity Analysis"]
        molSpreadSheetData.mssHeaders << ["Active vs Tested across all Assay Definitions"]
        for (Experiment experiment in experimentList) {
            molSpreadSheetData.experimentNameList << "${experiment.id.toString()}"
            molSpreadSheetData.experimentFullNameList << "${experiment.name.toString()}"
            molSpreadSheetData.mssHeaders << []
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

        for (Assay assay : assays) {
            final SearchResult<Experiment> searchResult = restAssayService.searchResult(assay, Experiment)
            if (searchResult) {
                allExperiments.addAll(searchResult.searchResults)
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

        for (Long individualAssayIds : assayIds) {
            final Assay assay = restAssayService.get(individualAssayIds)
            final SearchResult<Experiment> experimentSearchResult = restAssayService.searchResult(assay, Experiment)
            for (Experiment experiment : experimentSearchResult.searchResults) {
                allExperiments << experiment
            }
        }

        return allExperiments
    }

    /**
     * Convert Cart assays to Experiments, starting this time with cart Assays
     * @param cartAssays
     * @return
     */
    protected List<Experiment> cartAssaysToExperiments(List<Experiment> incomingExperimentList, final List<CartAssay> cartAssays) {
        List<Long> assayIds = cartAssays*.externalId

        assaysToExperiments(incomingExperimentList, assayIds)
    }

    /**
     *
     * @param incomingExperimentList
     * @param cartCompounds
     * @return
     */
    protected List<Experiment> cartCompoundsToExperiments(final List<CartCompound> cartCompounds) {
        List<Long> compoundIds = cartCompounds*.externalId

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
        List<Long> projectIds = cartProjects*.externalId
        List<Experiment> allExperiments = []
        final RESTProjectService restProjectService = queryServiceWrapper.restProjectService
        final RESTAssayService restAssayService = queryServiceWrapper.restAssayService
        final Collection<Project> projects = restProjectService.get(projectIds)

        for (Project project : projects) {
            final SearchResult<Assay> assaySearchResult = restProjectService.searchResult(project, Assay)
            for (Assay assay : assaySearchResult.searchResults) {
                final SearchResult<Experiment> searchResults = restAssayService.searchResult(assay, Experiment)
                allExperiments.addAll(searchResults.searchResults)
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
        final SearchResult<Value> experimentalResults = this.queryServiceWrapper.restExperimentService.activities(experiment, compoundETag);

        for (Value experimentValue : experimentalResults.searchResults) {
            if (experimentValue) {
                SpreadSheetActivity spreadSheetActivity = extractActivitiesFromExperiment(experimentValue)
                spreadSheetActivities.add(spreadSheetActivity)
            }
        }
        return spreadSheetActivities
    }


    Map findExperimentDataById(final Long experimentId, final Integer top = 10, final Integer skip = 0) {
        List<SpreadSheetActivity> spreadSheetActivities = []
        final RESTExperimentService restExperimentService = queryServiceWrapper.restExperimentService
        long totalNumberOfRecords = 0
        ExperimentRole role = null
        Experiment experiment = restExperimentService.get(experimentId)
        if (experiment) {
            role = experiment.role
            final Map activityValuesMap = extractActivityValues(experiment, top, skip)
            final List<Value> activityValues = activityValuesMap.activityValues
            totalNumberOfRecords = activityValuesMap.totalNumberOfRecords
            spreadSheetActivities = createSpreadSheetActivitiesFromActivityValues(activityValues)

        }
        return [total: totalNumberOfRecords, spreadSheetActivities: spreadSheetActivities, role: role, experiment: experiment]
    }

    protected List<SpreadSheetActivity> createSpreadSheetActivitiesFromActivityValues(final List<Value> activityValues) {
        List<SpreadSheetActivity> spreadSheetActivities = []
        for (Value experimentValue : activityValues) {
            SpreadSheetActivity spreadSheetActivity = extractActivitiesFromExperiment(experimentValue)
            spreadSheetActivities.add(spreadSheetActivity)
        }
        return spreadSheetActivities
    }

    protected Map extractActivityValues(final Experiment experiment, final Integer top = 10, final Integer skip = 0) {
        final RESTExperimentService restExperimentService = queryServiceWrapper.restExperimentService
        final SearchResult<Value> experimentSearchResults = restExperimentService.activities(experiment);
        return extractActivityValuesFromExperimentValueIterator(experimentSearchResults, top, skip)
    }

    protected Map extractActivityValuesFromExperimentValueIterator(final SearchResult<Value> experimentResults, final Integer top = 10, final Integer skip = 0) {
        List<Value> activityValues = []
        long totalNumberOfRecords = 0
        if (experimentResults) {
            activityValues = experimentResults.next(top, skip)
            totalNumberOfRecords = experimentResults.count
        }

        return [totalNumberOfRecords: totalNumberOfRecords, activityValues: activityValues]
    }




    protected void prepareMapOfColumnsToAssay(final MolSpreadSheetData molSpreadSheetData) {
        molSpreadSheetData.mapColumnsToAssay = [:]
        int columnIndex = 0
        int assayIndex = 0
        for (List<String> listOfColumnSubheadings in molSpreadSheetData.mssHeaders) {
            if (columnIndex < START_DYNAMIC_COLUMNS) {
                molSpreadSheetData.mapColumnsToAssay[columnIndex++] = ""
            } else {
                for (String columnSubheadings in listOfColumnSubheadings) {
                    molSpreadSheetData.mapColumnsToAssayName[columnIndex] = molSpreadSheetData.experimentFullNameList[assayIndex].toString()
                    molSpreadSheetData.mapColumnsToAssay[columnIndex++] = molSpreadSheetData.experimentNameList[assayIndex].toString()
                }
                assayIndex++
            }
        }
    }

/**
 *
 * @param experimentValue
 * @return SpreadSheetActivity
 */
    SpreadSheetActivity extractActivitiesFromExperiment(MolSpreadSheetData molSpreadSheetData, final Integer experimentCount, final Value experimentValue) {
        final Collection<Value> experimentValueIterator = experimentValue.getChildren()
        SpreadSheetActivity spreadSheetActivity = new SpreadSheetActivity()
        for (Value childValue : experimentValueIterator) {
            addCurrentActivityToSpreadSheet(molSpreadSheetData.mssHeaders[START_DYNAMIC_COLUMNS + experimentCount], spreadSheetActivity, childValue)
        }
        return spreadSheetActivity
    }


    SpreadSheetActivity extractActivitiesFromExperiment(final Value experimentValue) {
        final Collection<Value> experimentValueIterator = experimentValue.getChildren()
        SpreadSheetActivity spreadSheetActivity = new SpreadSheetActivity()
        List<String> dummyHeadersList = []
        for (Value childValue : experimentValueIterator) {
            addCurrentActivityToSpreadSheet(dummyHeadersList, spreadSheetActivity, childValue)
        }
        return spreadSheetActivity
    }



    SpreadSheetActivity extractActivitiesFromExperiment(final Value experimentValue, final Long experimentId) {
        final Collection<Value> experimentValueIterator = experimentValue.getChildren()
        SpreadSheetActivity spreadSheetActivity = new SpreadSheetActivity()
        spreadSheetActivity.experimentId = experimentId
        List<String> dummyHeadersList = []
        for (Value childValue : experimentValueIterator) {
            addCurrentActivityToSpreadSheet(dummyHeadersList, spreadSheetActivity, childValue)
        }
        return spreadSheetActivity
    }

/**
 * The idea is to fill up a spreadsheet activity based on
 *  Note hack -- this method has been short-circuited so that a HillCurveValue will cause the method to
 *  push a value back onto readouts and then return
 * @param spreadSheetActivity
 * @param childValue
 */
    void addCurrentActivityToSpreadSheet(final List<String> resultTypeNames, final SpreadSheetActivity spreadSheetActivity, final Value childValue) {

        if (childValue instanceof HillCurveValue) {
            spreadSheetActivity.hillCurveValueList << childValue
            if (!resultTypeNames.contains(childValue.id)) {
                resultTypeNames << childValue.id
            }
        }
        else {
            String identifier = childValue.id
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

}
