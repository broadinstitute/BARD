package molspreadsheet

import bard.core.SearchParams
import bard.core.adapter.CompoundAdapter
import bard.core.rest.spring.AssayRestService
import bard.core.rest.spring.CompoundRestService
import bard.core.rest.spring.ExperimentRestService
import bard.core.rest.spring.ProjectRestService
import bard.core.rest.spring.assays.AbstractAssay
import bard.core.rest.spring.assays.Assay
import bard.core.rest.spring.assays.ExpandedAssay
import bard.core.rest.spring.assays.ExpandedAssayResult
import bard.core.rest.spring.experiment.Activity
import bard.core.rest.spring.experiment.ExperimentData
import bard.core.rest.spring.experiment.ExperimentSearch
import bard.core.rest.spring.experiment.ExperimentSearchResult
import bard.core.rest.spring.project.Project
import bard.core.rest.spring.project.ProjectResult
import bardqueryapi.IQueryService
import bardqueryapi.SearchFilter
import bard.core.rest.spring.project.ProjectExpanded

class MolecularSpreadSheetService {
    final static int START_DYNAMIC_COLUMNS = 4 //Where to start the dynamic columns

    IQueryService queryService
    ExperimentRestService experimentRestService
    AssayRestService assayRestService
    CompoundRestService compoundRestService
    ProjectRestService projectRestService



    public ExperimentData activitiesByEIDs(final List<Long> eids, final SearchParams searchParams) {
        return experimentRestService.activitiesByEIDs(eids, searchParams)

    }

    public ExperimentData activitiesByADIDs(final List<Long> adids, final SearchParams searchParams) {
        return experimentRestService.activitiesByADIDs(adids, searchParams)
    }

    public ExperimentData activitiesBySIDs(final List<Long> sids, final SearchParams searchParams) {
        return experimentRestService.activitiesBySIDs(sids, searchParams)
    }

    public ExperimentData activitiesByCIDs(final List<Long> cids, final SearchParams searchParams) {
        return experimentRestService.activitiesByCIDs(cids, searchParams)
    }

    protected void addTableHeader(final MolSpreadSheetData molSpreadSheetData, final LinkedHashMap<String, Object> spreadSheetTable) {

        spreadSheetTable["labels"] = ["molstruct": "molecular structure"]
        spreadSheetTable["labels"] << ["cid": "CID"]
        int column = 0
        for (String colHeader in molSpreadSheetData.getColumns()) {
            if (column == 2) {
                spreadSheetTable["labels"] << [("c${column}" as String): "$colHeader"]
            }
            if (column > 2) {
                spreadSheetTable["labels"] << [("c${column}" as String): "${molSpreadSheetData.mapColumnsToAssay[column]} ${colHeader}"]
            }
            column++
        }
    }
    //adds the label and fields under the main header
    protected void addTableFields(final LinkedHashMap<String, Object> spreadSheetTable) {
        spreadSheetTable["fields"] = []
        spreadSheetTable["labels"].each {key, value ->
            spreadSheetTable["fields"] << key
        }
        //leave out promiscuity for now
        spreadSheetTable["fields"] -= "c2"
    }

    protected void addTableData(final MolSpreadSheetData molSpreadSheetData, final LinkedHashMap<String, Object> spreadSheetTable) {
        spreadSheetTable["data"] = []
        for (int rowCnt in 0..(molSpreadSheetData.getRowCount() - 1)) {
            LinkedHashMap<String, String> mapForThisRow = []
            addFixedColumnData(mapForThisRow, molSpreadSheetData, rowCnt)
            if (molSpreadSheetData.getColumnCount() > START_DYNAMIC_COLUMNS) {
                for (int colCnt in (START_DYNAMIC_COLUMNS..molSpreadSheetData.getColumnCount() - 1)) {
                    final SpreadSheetActivityStorage spreadSheetActivityStorage = molSpreadSheetData.findSpreadSheetActivity(rowCnt, colCnt)
                    addActivitiesForCurrentRow(mapForThisRow, spreadSheetActivityStorage, colCnt)
                }
            }
            spreadSheetTable["data"] << mapForThisRow
        }
    }

    protected void addActivitiesForCurrentRow(final LinkedHashMap<String, String> mapForThisRow,final SpreadSheetActivityStorage spreadSheetActivityStorage,final int colCnt) {
        if (spreadSheetActivityStorage != null) {
            HillCurveValueHolder hillCurveValueHolder = spreadSheetActivityStorage.getHillCurveValueHolderList()[0]
            mapForThisRow << [("c${colCnt}" as String): hillCurveValueHolder.toString()]
        } else {
            mapForThisRow << [("c${colCnt}" as String): "not tested in this experiment"]
        }

    }

    protected void addFixedColumnData(final LinkedHashMap<String, String> mapForThisRow, final MolSpreadSheetData molSpreadSheetData, final int rowCnt) {
        mapForThisRow << ["molstruct": """${molSpreadSheetData.displayValue(rowCnt, 0)?."smiles"}""".toString()]
        mapForThisRow << ["cid": """${molSpreadSheetData.displayValue(rowCnt, 1)?."value"}""".toString()]
        mapForThisRow << ["c3": """${molSpreadSheetData.displayValue(rowCnt, 3)?."value"}""".toString()]
    }

    protected LinkedHashMap<String, Object> prepareForExport(final MolSpreadSheetData molSpreadSheetData) {
        LinkedHashMap<String, Object> spreadSheetTable = []
        addTableHeader(molSpreadSheetData, spreadSheetTable)
        addTableFields(spreadSheetTable)
        spreadSheetTable["data"] = []
        addTableData(molSpreadSheetData, spreadSheetTable)

        return spreadSheetTable

    }
    /**
     * High-level routine to pull information out of the query cart and store it into a data structure suitable
     * for passing to the molecular spreadsheet.
     *
     * @return MolSpreadSheetData
     */
    MolSpreadSheetData retrieveExperimentalDataFromIds(List<Long> cids, List<Long> adids, List<Long> pids, Boolean showActiveCompoundsOnly) {

        MolSpreadSheetDataBuilderDirector molSpreadSheetDataBuilderDirector = new MolSpreadSheetDataBuilderDirector()

        MolSpreadSheetDataBuilder molSpreadSheetDataBuilder = new MolSpreadSheetDataBuilder(this)

        molSpreadSheetDataBuilderDirector.setMolSpreadSheetDataBuilder(molSpreadSheetDataBuilder)

        molSpreadSheetDataBuilderDirector.constructMolSpreadSheetDataFromIds(cids, adids, pids,showActiveCompoundsOnly)

        return molSpreadSheetDataBuilderDirector.molSpreadSheetData
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
        return queryService.findCompoundsByCIDs(compoundIds, filters)
    }




    protected String retrieveImpliedCompoundsEtagFromAssaySpecification(List<ExperimentSearch> experimentList) {
        String etag = null
        for (ExperimentSearch experiment : experimentList) {
            List<Long> idList = []
            if (experiment.bardExptId) {
                idList = this.experimentRestService.compoundsForExperiment(experiment.bardExptId)
            }
            if (etag == null) {
                etag = this.compoundRestService.newETag("${new Date().toString()}", idList);
            }
            else if (idList) {
                this.compoundRestService.putETag(etag, idList);
            }
        }
        return etag
    }
    /**
     * For a set of experiments
     * @param experimentList
     * @param etag
     * @return
     */
    protected List<SpreadSheetActivity> extractMolSpreadSheetData(final MolSpreadSheetData molSpreadSheetData, final List<ExperimentSearch> experimentList, String etag = null) {
        // now step through the data and place into molSpreadSheetData
        final List<SpreadSheetActivity> spreadSheetActivityList = new ArrayList<SpreadSheetActivity>()

        // we need to handle each experiment separately ( until NCGC can do this in the background )
        // Note that each experiment corresponds to a column in our spreadsheet
        int columnCount = 0
        for (ExperimentSearch experiment in experimentList) {
            ExperimentData experimentSearchResults
            if (etag) {
                experimentSearchResults = experimentRestService.activities(experiment.bardExptId, etag)
            } else {
                experimentSearchResults = experimentRestService.activities(experiment.bardExptId)
            }

            // Now step through the result set and pull back  one value for each compound
            //Value experimentValue
            // Integer experimentCount = 0
            for (Activity activity : experimentSearchResults.activities) {
                Long translation = new Long(activity.exptDataId.split("\\.")[0])
                if (!molSpreadSheetData.columnPointer.containsKey(translation)) {
                    molSpreadSheetData.columnPointer.put(translation, columnCount)
                }

                if (activity.resultData.priorityElements?.size()>0) {  // documends with  priority elements we know how to handle
                    spreadSheetActivityList.add(extractActivitiesFromExperiment(molSpreadSheetData, columnCount, activity))
                } else {     // need to handle documents w/o priority elements too
                    spreadSheetActivityList.add(constructActivityForNonpriorityElement(molSpreadSheetData, columnCount, activity))
                }


            }
            columnCount++
        }
        return spreadSheetActivityList
    }

    /**
     * Now we have all of the available data in the dataMap, but that map may have holes ( where the compound wasn't tested with a particular assay)
     * and it may also have cells with multiple values. This method is intended to smooth out the rough spots -- make a new map with at least one value
     * for each cell  in the molecular spreadsheet.  Some of those cells may be null, but that's fine, since we can print out "Not tested in this experiment"
     * on the GSP when we bump into one of them.
     * @param molSpreadSheetData
     * @param dataMap
     */
    protected void fillInTheMissingCellsAndConvertToExpandedMatrix(MolSpreadSheetData molSpreadSheetData, Map<String, List<MolSpreadSheetCell>> dataMap) {
        for (int row in 0..(molSpreadSheetData.rowCount - 1)) {
            int exptNumberColTracker = START_DYNAMIC_COLUMNS
            for (int col in 0..(molSpreadSheetData.superColumnCount - 1)) {
                String key = "${row}_${col}"
                List<MolSpreadSheetCell> molSpreadSheetCellList = null
                SpreadSheetActivityStorage spreadSheetActivityStorage = null
                if (dataMap.containsKey(key)) {
                    molSpreadSheetCellList = dataMap[key]
                }
                if (molSpreadSheetData.mssHeaders[col].molSpreadSheetColSubHeaderList.size() > 0) {
                    if (molSpreadSheetCellList != null) {
                        if (col < START_DYNAMIC_COLUMNS) {
                            molSpreadSheetData.mssData["${row}_${col}"] = molSpreadSheetCellList[0]
                        } else {
                            int countingColumns = 0
                            int columnsToFill = molSpreadSheetData.mssHeaders[col].molSpreadSheetColSubHeaderList.size()
                            for (int experimentNum in 0..columnsToFill - 1) {   // outer loop -- each run advances counter
                                MolSpreadSheetCell bestMolSpreadSheetCell = null
                                if (molSpreadSheetCellList.size() > 1) { // more than one result type. Pick the best one(s)
                                    List<MolSpreadSheetCell> molSpreadSheetCells = molSpreadSheetCellList.findAll { MolSpreadSheetCell molSpreadSheetCell ->
                                        ((molSpreadSheetCell.spreadSheetActivityStorage != null) &&
                                                (molSpreadSheetCell.spreadSheetActivityStorage.dictionaryLabel == molSpreadSheetData.mssHeaders[col].molSpreadSheetColSubHeaderList[experimentNum].columnTitle))
                                    }
                                    int numberOfValuesGoingIntoArithmeticMean = 0
                                    countingColumns++
                                    if (molSpreadSheetCellList.size()==0) {
                                        bestMolSpreadSheetCell = new MolSpreadSheetCell()
                                    } else if ((molSpreadSheetCells == null) || (molSpreadSheetCells.size() == 0)) {
                                        bestMolSpreadSheetCell = new MolSpreadSheetCell()  // this shouldn't happen(?). We have no data
                                    } else if (molSpreadSheetCells.size() == 1) {   // we have exactly one match.
                                        bestMolSpreadSheetCell = molSpreadSheetCells[0]
                                    } else { // we have more than one option.  Send all of the spreadsheet activities forward. Combine them together.
                                        double valueToDisplay = Double.NaN
                                        String stringValue = ""
                                        MolSpreadSheetCell firstNonNullMolSpreadSheetCell = null


                                        for (MolSpreadSheetCell molSpreadSheetCell in molSpreadSheetCells) {              // this is the inner loop.  Collect data, don't advance counter
                                            if (molSpreadSheetCell.molSpreadSheetCellType==MolSpreadSheetCellType.numeric) {
                                                for (HillCurveValueHolder hillCurveValueHolder in molSpreadSheetCell.spreadSheetActivityStorage?.hillCurveValueHolderList) {
                                                    if ((hillCurveValueHolder.slope) && (hillCurveValueHolder.slope != Double.NaN)) {
                                                        numberOfValuesGoingIntoArithmeticMean++
                                                        if (valueToDisplay == Double.NaN) {
                                                            firstNonNullMolSpreadSheetCell = molSpreadSheetCell
                                                            valueToDisplay = hillCurveValueHolder.slope
                                                        } else {
                                                            valueToDisplay += hillCurveValueHolder.slope
                                                            firstNonNullMolSpreadSheetCell.spreadSheetActivityStorage.hillCurveValueHolderList << hillCurveValueHolder
                                                        }
                                                    }

                                                }

                                            } else if (molSpreadSheetCell.molSpreadSheetCellType==MolSpreadSheetCellType.string)   {
                                                if (molSpreadSheetCell.strInternalValue!="null"){
                                                    for (HillCurveValueHolder hillCurveValueHolder in molSpreadSheetCell.spreadSheetActivityStorage?.hillCurveValueHolderList) {
                                                        if (stringValue == "") {
                                                            firstNonNullMolSpreadSheetCell = molSpreadSheetCell
                                                            stringValue = hillCurveValueHolder.stringValue
                                                        } else {
                                                            stringValue += hillCurveValueHolder.stringValue
                                                            firstNonNullMolSpreadSheetCell.spreadSheetActivityStorage.hillCurveValueHolderList << hillCurveValueHolder
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                        if ((firstNonNullMolSpreadSheetCell != null) &&      // we had at least one value that wasn't null   AND  EITHER
                                                (
                                                (stringValue != "")

                                                        ||
                                                 ((valueToDisplay != Double.NaN) &&              // we had a non-null numeric value
                                                   (numberOfValuesGoingIntoArithmeticMean > 0)))){  //  we had a non-null string value
                                            // if we wanted to assign the mean value to the first element then we could do it here.
                                            //firstNonNullMolSpreadSheetCell.spreadSheetActivityStorage.hillCurveValueHolderList[0].slope = (valueToDisplay / numberOfValuesGoingIntoArithmeticMean)
                                            bestMolSpreadSheetCell = firstNonNullMolSpreadSheetCell // we like this one.  Remember it.
                                        } else {
                                            bestMolSpreadSheetCell = new MolSpreadSheetCell()// we had no values that were not null. Send this null marker as an indication that we have nothing to report
                                        }
                                        // finally, make sure that the first one is real
                                        if (bestMolSpreadSheetCell != molSpreadSheetCells[0])
                                            molSpreadSheetCells[0] = bestMolSpreadSheetCell
                                    }

                                } else {
                                    bestMolSpreadSheetCell = molSpreadSheetCellList[0]
                                }
                                molSpreadSheetData.mssData["${row}_${exptNumberColTracker++}"] = bestMolSpreadSheetCell
                            }

                        }

                    } else {
                        //  This condition typically arises ONLY when we have multiple compounds crossing multiple assays
                        // and some compounds are not tested in some of those assays
                        molSpreadSheetData.mssData["${row}_${(exptNumberColTracker++)}"] = new MolSpreadSheetCell()
                    }
                }
            }
        }
    }




    protected String generateETagFromCids(List<Long> cids) {
        Date date = new Date()
        return compoundRestService.newETag(date.toTimestamp().toString(), cids);
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
                                              final List<ExperimentSearch> experimentList,
                                              final List<SpreadSheetActivity> spreadSheetActivityList,
                                              final Map<String, List <MolSpreadSheetCell>> dataMap) {
        // now step through the data and place into molSpreadSheetData
        int columnPointer = 0
        // we need to handle each experiment separately ( until NCGC can do this in the background )
        // Note that each experiment corresponds to a column in our spreadsheet
        //       for (ExperimentSearch experiment in experimentList) {

        for (SpreadSheetActivity spreadSheetActivity in spreadSheetActivityList) {
            if (molSpreadSheetData.rowPointer.containsKey(spreadSheetActivity.cid)) {
                int innerRowPointer = molSpreadSheetData.rowPointer[spreadSheetActivity.cid]
                int innerColumnCount = molSpreadSheetData.columnPointer[spreadSheetActivity.eid]
                String arrayKey = "${innerRowPointer}_${innerColumnCount + START_DYNAMIC_COLUMNS}"
                List <MolSpreadSheetCell> molSpreadSheetCellList = MolSpreadSheetCell.molSpreadSheetCellListFactory (spreadSheetActivity)
                if (dataMap.containsKey(arrayKey)) {
                    // we have multiple values for cell = ${arrayKey}.
                    // as long as the list is not null we add every element into the data map
                    if ((molSpreadSheetCellList != null) && (molSpreadSheetCellList.size () > 0))  {
                        for (MolSpreadSheetCell molSpreadSheetCell in molSpreadSheetCellList) {
                            dataMap[arrayKey].add(molSpreadSheetCell)
                        }
                    }
                } else {
                    dataMap[arrayKey] = molSpreadSheetCellList
                }
            } else {
                println "did not expect cid = ${spreadSheetActivity.cid}"
            }
            columnPointer++
        }
    }

    /**
     *
     * @param molSpreadSheetData
     * @param compoundAdapters
     * @param dataMap
     */
    protected void populateMolSpreadSheetRowMetadataFromCompoundAdapters(final MolSpreadSheetData molSpreadSheetData, final List<CompoundAdapter> compoundAdapters, Map<String, List<MolSpreadSheetCell>> dataMap) {

        // add specific values for the cid column
        int rowCount = 0
        for (CompoundAdapter compoundAdapter in compoundAdapters) {
            updateMolSpreadSheetDataToReferenceCompound(
                    molSpreadSheetData,
                    rowCount++,
                    compoundAdapter.id,
                    compoundAdapter.name,
                    compoundAdapter.smiles,
                    dataMap, compoundAdapter.numberOfActiveAssays, compoundAdapter.numberOfAssays
            )
        }

    }
    /**
     *
     * @param molSpreadSheetData
     * @param compoundAdapterMap
     * @return
     */
    protected void populateMolSpreadSheetRowMetadata(final MolSpreadSheetData molSpreadSheetData, final Map compoundAdapterMap, Map<String, List<MolSpreadSheetCell>> dataMap) {

        // Add every compound we can find in the compound adapters map
        List<CompoundAdapter> compoundAdaptersList = compoundAdapterMap.compoundAdapters
        int rowCount = 0
        for (CompoundAdapter compoundAdapter : compoundAdaptersList) {
            String smiles = compoundAdapter.smiles
            Long cid = compoundAdapter.id
            String name = compoundAdapter.name
            final int numberOfActiveAssays = compoundAdapter.numberOfActiveAssays
            final int numberOfAssays = compoundAdapter.numberOfAssays
            updateMolSpreadSheetDataToReferenceCompound(molSpreadSheetData, rowCount++, cid, name, smiles, dataMap, numberOfActiveAssays, numberOfAssays)
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
    protected MolSpreadSheetData updateMolSpreadSheetDataToReferenceCompound(MolSpreadSheetData molSpreadSheetData,
                                                                             int rowCount, Long compoundId,
                                                                             String compoundName,
                                                                             String compoundSmiles, Map<String, List<MolSpreadSheetCell>> dataMap,
                                                                             int numAssayActive,
                                                                             int numAssayTested) {
        // need to be able to map from CID to row location
        molSpreadSheetData.rowPointer.put(compoundId, rowCount)

        //TODO find a generic way to do this. It seems there are too many places to add new constant columns
        // add values for the cid column
        dataMap.put("${rowCount}_0".toString(), [new MolSpreadSheetCell(compoundName, compoundSmiles, MolSpreadSheetCellType.image)])
        dataMap.put("${rowCount}_1".toString(), [new MolSpreadSheetCell(compoundId.toString(), MolSpreadSheetCellType.identifier)])
        //we will use this to get the promiscuity score
        dataMap.put("${rowCount}_2".toString(), [new MolSpreadSheetCell(compoundId.toString(), MolSpreadSheetCellType.identifier)])
        //we will use this to get the 'active vrs tested' column
        dataMap.put("${rowCount}_3".toString(), [new MolSpreadSheetCell("${numAssayActive} / ${numAssayTested}", MolSpreadSheetCellType.string)])

        return molSpreadSheetData

    }

    /**
     *
     * @param molSpreadSheetData
     * @param experimentList
     */
    protected void populateMolSpreadSheetColumnMetadata(MolSpreadSheetData molSpreadSheetData, final List<ExperimentSearch> experimentList) {

        // now retrieve the header names from the assays
        molSpreadSheetData.mssHeaders << new  MolSpreadSheetColumnHeader(molSpreadSheetColSubHeaderList:[new MolSpreadSheetColSubHeader(columnTitle:'Struct')])
        molSpreadSheetData.mssHeaders << new  MolSpreadSheetColumnHeader(molSpreadSheetColSubHeaderList:[new MolSpreadSheetColSubHeader(columnTitle:'CID')])
        molSpreadSheetData.mssHeaders << new  MolSpreadSheetColumnHeader(molSpreadSheetColSubHeaderList:[new MolSpreadSheetColSubHeader(columnTitle:'UNM Promiscuity Analysis')])
        molSpreadSheetData.mssHeaders << new  MolSpreadSheetColumnHeader(molSpreadSheetColSubHeaderList:[new MolSpreadSheetColSubHeader(columnTitle:'Active vs Tested across all Assay Definitions')])

        for (ExperimentSearch experiment : experimentList) {
            molSpreadSheetData.experimentNameList << "${experiment.bardAssayId.toString()}".toString()
            molSpreadSheetData.experimentFullNameList << "${experiment.name.toString()}".toString()
            molSpreadSheetData.mssHeaders << new MolSpreadSheetColumnHeader(molSpreadSheetColSubHeaderList:[])
            molSpreadSheetData.mapColumnsNormalization["${experiment.bardAssayId.toString()}"]  = true
        }
    }

    /**
     * Convert Assay iDs to expt ids
     * @param assays
     * @return list
     */
    protected List<ExperimentSearch> assaysToExperiments(final List<? extends AbstractAssay> assays) {
        if (assays) {
            List<Long> assayIds = assays*.id
            return assayIdsToExperiments(assayIds)
        }
        return []
    }
    /**
     * Convert Assay ids to expt ids
     * @param assayIds
     * @return list of experiments
     */
    protected List<ExperimentSearch> assayIdsToExperiments(final List<Long> assayIds) {
        final List<ExperimentSearch> allExperiments = []

        for (Long assayId : assayIds) {
            //TODO: We probably could post all the ids to this url. We need to investigate
            final List<ExperimentSearch> experiments = assayRestService.findExperimentsByAssayId(assayId)
            for (ExperimentSearch experimentSearch in experiments)  {
                if (!allExperiments*.bardExptId.contains(experimentSearch.bardExptId) )
                    allExperiments <<  experimentSearch
            }
         }

        return allExperiments
    }
    /**
     * Convert assayIds to Experiments starting with a list of Assay IDs
     * @param incomingExperimentList
     * @param assayIds
     * @return list
     */
    protected List<ExperimentSearch> assaysToExperiments(List<ExperimentSearch> incomingExperimentList,
                                                         final List<Long> assayIds,
                                                         Map<Long, Long> mapExperimentIdsToCapAssayIds,
                                                         Map<Long, String> mapCapAssayIdsToAssayNames) {

        List<ExperimentSearch> allExperiments
        if (incomingExperimentList) {
            allExperiments = incomingExperimentList
        }
        else {
            allExperiments = []
        }
        if (!assayIds) {
            return allExperiments
        }
        final ExpandedAssayResult expandedAssayResult = assayRestService.searchAssaysByIds(assayIds)
        final List<ExpandedAssay> assays = expandedAssayResult.assays
        for (ExpandedAssay assay : assays) {
            if (!mapCapAssayIdsToAssayNames.containsKey(assay.bardAssayId)){  // store the name of the assay for later display
                mapCapAssayIdsToAssayNames [assay.bardAssayId]    = assay.name
            }
            final List<ExperimentSearch> experiments = assay.experiments
            if (experiments) {
                for (ExperimentSearch experimentSearch in experiments) {
                    if (!allExperiments*.bardExptId.contains(experimentSearch.bardExptId)){
                        allExperiments <<  experimentSearch
                        mapExperimentIdsToCapAssayIds[experimentSearch.bardAssayId] =  assay.capAssayId
                    }

                }
            }
        }
        return allExperiments
    }
    /**
     * Convert assay ids to Experiments, starting this time with assay ids
     * @param incomingExperimentList
     * @param adids
     * @return list of experiments
     */
    protected List<ExperimentSearch> assayIdsToExperiments(final List<ExperimentSearch> incomingExperimentList,
                                                           final List<Long> adids,
                                                           Map<Long, Long> mapExperimentIdsToCapAssayIds,
                                                           Map<Long, String> mapCapAssayIdsToAssayNames ) {
        return assaysToExperiments(incomingExperimentList, adids, mapExperimentIdsToCapAssayIds,mapCapAssayIdsToAssayNames)
    }
    /**
     *
     * @param cids
     * @return list of experiments
     */
    protected List<ExperimentSearch> compoundIdsToExperiments(final List<Long> cids,
                                                              Map<Long, Long> mapExperimentIdsToCapAssayIds,
                                                              Map<Long, String> mapCapAssayIdsToAssayNames,
                                                              Boolean showActiveCompoundsOnly) {
        if (!cids) {
            return []
        }
        List<Assay> allAssays = []
        for (Long individualCompoundId in cids) {
            List<Assay> assays = compoundRestService.getTestedAssays(individualCompoundId, showActiveCompoundsOnly)
            for (Assay assay in assays) {
                if (!mapCapAssayIdsToAssayNames.containsKey(assay.bardAssayId)){  // store the name of the assay for later display
                    mapCapAssayIdsToAssayNames [assay.bardAssayId]    = assay.name
                }
                if (!allAssays*.assayId.contains(assay.bardAssayId)) {
                    allAssays.add(assay)
                    mapExperimentIdsToCapAssayIds[assay.bardAssayId] = assay.capAssayId
                }
            }
        }

        return assaysToExperiments(allAssays)
    }

/**
     *
     * @param projectIds
     * @return list of Experiment's from a list of project Ids
     */
    protected List<ExperimentSearch> projectIdsToExperiments(final List<Long> projectIds, Map<Long, Long> mapExperimentIdsToCapAssayIds,Map<Long, String> mapCapAssayIdsToAssayNames) {
        final ProjectResult projectResult = projectRestService.searchProjectsByIds(projectIds)
        return projectsToExperiments(projectResult, mapExperimentIdsToCapAssayIds,mapCapAssayIdsToAssayNames)
    }

    protected List<ExperimentSearch> projectsToExperiments(final ProjectResult projectResult, Map<Long, Long> mapExperimentIdsToCapAssayIds,Map<Long, String> mapCapAssayIdsToAssayNames) {
        final List<ExperimentSearch> allExperiments = []
        if (projectResult) {
            for (Project project : projectResult.projects) {
                ProjectExpanded projectExpanded = projectRestService.getProjectById(project.bardProjectId)
                for (Assay assay in projectExpanded?.assays){
                    if (!mapCapAssayIdsToAssayNames.containsKey(assay.bardAssayId)){  // store the name of the assay for later display
                        mapCapAssayIdsToAssayNames [assay.bardAssayId]    = assay.name
                    }
                    if (!mapExperimentIdsToCapAssayIds.containsKey(assay.bardAssayId)) {
                        mapExperimentIdsToCapAssayIds[assay.bardAssayId] = assay.capAssayId
                    }

                }
                projectToExperiment(project.eids, allExperiments)
            }
        }
        return allExperiments
    }

    protected void projectToExperiment(final List<Long> eids, final List<ExperimentSearch> allExperiments) {
        final ExperimentSearchResult experimentResult = experimentRestService.searchExperimentsByIds(eids)
        if (experimentResult) {
            allExperiments.addAll(experimentResult.experiments)
        }
    }


    /**
     *
     * @param molSpreadSheetData
     */
    protected void prepareMapOfColumnsToAssay(final MolSpreadSheetData molSpreadSheetData, List<ExperimentSearch> experimentList) {
        molSpreadSheetData.mapColumnsToAssay = [:]
        int columnIndex = 0
        int assayIndex = 0
        for (MolSpreadSheetColumnHeader molSpreadSheetColumnHeader  in molSpreadSheetData.mssHeaders) {
            List<String> listOfColumnSubheadings  = molSpreadSheetColumnHeader.molSpreadSheetColSubHeaderList*.columnTitle
            if (columnIndex < START_DYNAMIC_COLUMNS) {
                molSpreadSheetData.mapColumnsToAssay[columnIndex++] = ""
            } else {
                int subColumnStepper = 0
                for (MolSpreadSheetColSubHeader molSpreadSheetColSubHeader in molSpreadSheetColumnHeader.molSpreadSheetColSubHeaderList){
                    for (int rowCnt in 0..molSpreadSheetData.rowCount)  {
                        SpreadSheetActivityStorage spreadSheetActivityStorage =  molSpreadSheetData.findSpreadSheetActivity(rowCnt, columnIndex+subColumnStepper)
                        if (spreadSheetActivityStorage)  {
                            // figure out if the column units are consistent
                            if (molSpreadSheetColSubHeader.unitsInColumn == null) {
                                molSpreadSheetColSubHeader.unitsInColumn =  spreadSheetActivityStorage.dictionaryId.toString()
                                molSpreadSheetColSubHeader.unitsInColumnAreUniform = true
                            }  else {
                                if (molSpreadSheetColSubHeader.unitsInColumn != spreadSheetActivityStorage.dictionaryId.toString()) {
                                    molSpreadSheetColSubHeader.unitsInColumnAreUniform = false
                                }
                            }
                            // extract a minimum and maximum value for any responses
                            if (spreadSheetActivityStorage.hillCurveValueHolderList.size()>0) {
                                for (HillCurveValueHolder hillCurveValueHolder in spreadSheetActivityStorage.hillCurveValueHolderList) {
                                    if ((hillCurveValueHolder.response!=null)&&
                                        (hillCurveValueHolder.response.size()>0)){
                                        Double maxResponse =  hillCurveValueHolder.response[0]
                                        for (Double instDouble in hillCurveValueHolder.response){
                                            if (instDouble>maxResponse) {
                                                maxResponse =  instDouble
                                            }
                                        }
                                        if (maxResponse != Double.NaN){
                                            if ((molSpreadSheetColSubHeader.maximumResponse == Double.NaN)  ||
                                                    (maxResponse>molSpreadSheetColSubHeader.maximumResponse)) {
                                                molSpreadSheetColSubHeader.maximumResponse =  maxResponse
                                            }
                                        }
                                        Double minResponse =  hillCurveValueHolder.response[0]
                                        for (Double instDouble in hillCurveValueHolder.response){
                                            if (instDouble<minResponse) {
                                                minResponse =  instDouble
                                            }
                                        }
                                        if (minResponse != Double.NaN){
                                            if ((molSpreadSheetColSubHeader.minimumResponse == Double.NaN)  ||
                                                    (minResponse<molSpreadSheetColSubHeader.minimumResponse)) {
                                                molSpreadSheetColSubHeader.minimumResponse =  minResponse
                                            }
                                        }

                                    }
                                }
                            }
                        }
                    }
                    subColumnStepper++
                }
                for (String columnSubheadings in listOfColumnSubheadings) {
                    molSpreadSheetData.mapColumnsToAssayName[columnIndex] = molSpreadSheetData.experimentFullNameList[assayIndex].toString()
                    molSpreadSheetData.mapColumnsToExperimentId[columnIndex] = experimentList[assayIndex].capExptId.toString()
                    molSpreadSheetData.mapColumnsToAssay[columnIndex++] = molSpreadSheetData.experimentNameList[assayIndex].toString()
                }
                 assayIndex++
            }
        }
    }

    /**
     *
     * @param molSpreadSheetData
     * @param experimentCount
     * @param experimentValue
     * @return SpreadSheetActivity
     */
    SpreadSheetActivity extractActivitiesFromExperiment(MolSpreadSheetData molSpreadSheetData, final Integer experimentCount, final Activity experimentValue) {
        SpreadSheetActivity spreadSheetActivity = new SpreadSheetActivity()
        spreadSheetActivity.activityToSpreadSheetActivity(experimentValue, molSpreadSheetData.getSubColumnList(START_DYNAMIC_COLUMNS + experimentCount))
        return spreadSheetActivity
    }



    SpreadSheetActivity constructActivityForNonpriorityElement(MolSpreadSheetData molSpreadSheetData, final Integer experimentCount, final Activity experimentValue) {
        SpreadSheetActivity spreadSheetActivity = new SpreadSheetActivity()
        spreadSheetActivity.nonPriorityElementToSpreadSheetActivity(experimentValue, molSpreadSheetData.getSubColumnList(START_DYNAMIC_COLUMNS + experimentCount))
        return spreadSheetActivity
    }





    /**
     *
     * @param columnNames
     * @param spreadSheetActivity
     * @param activity
     */
//    void addCurrentActivityToSpreadSheet(List <MolSpreadSheetColSubHeader> dummyColumnNames, SpreadSheetActivity spreadSheetActivity, final Activity activity) {
//        spreadSheetActivity.activityToSpreadSheetActivity(activity, dummyColumnNames)
//    }
    /**
     *
     * @param activity
     * @return SpreadSheetActivity
     */
//    SpreadSheetActivity extractActivitiesFromExperiment(final Activity activity) {
//        List <MolSpreadSheetColSubHeader> dummyHeadersList = []
//        final SpreadSheetActivity spreadSheetActivity = new SpreadSheetActivity()
//        addCurrentActivityToSpreadSheet(dummyHeadersList, spreadSheetActivity, activity)
//        return spreadSheetActivity
//    }
}
