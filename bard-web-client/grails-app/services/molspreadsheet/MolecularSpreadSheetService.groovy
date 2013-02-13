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
            if (molSpreadSheetData.getColumnCount() > 4) {
                for (int colCnt in (4..molSpreadSheetData.getColumnCount() - 1)) {
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
    MolSpreadSheetData retrieveExperimentalDataFromIds(List<Long> cids, List<Long> adids, List<Long> pids) {

        MolSpreadSheetDataBuilderDirector molSpreadSheetDataBuilderDirector = new MolSpreadSheetDataBuilderDirector()

        MolSpreadSheetDataBuilder molSpreadSheetDataBuilder = new MolSpreadSheetDataBuilder(this)

        molSpreadSheetDataBuilderDirector.setMolSpreadSheetDataBuilder(molSpreadSheetDataBuilder)

        molSpreadSheetDataBuilderDirector.constructMolSpreadSheetDataFromIds(cids, adids, pids)

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
            if (experiment.id) {
                idList = this.experimentRestService.compoundsForExperiment(experiment.id)
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
                experimentSearchResults = experimentRestService.activities(experiment.id, etag)
            } else {
                experimentSearchResults = experimentRestService.activities(experiment.id)
            }

            // Now step through the result set and pull back  one value for each compound
            //Value experimentValue
            // Integer experimentCount = 0
            for (Activity activity : experimentSearchResults.activities) {
                Long translation = new Long(activity.exptDataId.split("\\.")[0])
                if (!molSpreadSheetData.columnPointer.containsKey(translation)) {
                    molSpreadSheetData.columnPointer.put(translation, columnCount)
                }
                spreadSheetActivityList.add(extractActivitiesFromExperiment(molSpreadSheetData, columnCount, activity))
            }
            columnCount++
        }
        return spreadSheetActivityList
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
                if (molSpreadSheetData.mssHeaders[col].molSpreadSheetColSubHeaderList.size()>0){//} molSpreadSheetData.getSubColumns().size()>0) {
//                    if (molSpreadSheetData.mssHeaders[col].columnTitle.size()>0) {
                        for (int experimentNum in 0..molSpreadSheetData.mssHeaders[col].molSpreadSheetColSubHeaderList.size() - 1) {
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
                                              final Map<String, MolSpreadSheetCell> dataMap) {
        // now step through the data and place into molSpreadSheetData
        int columnPointer = 0
        // we need to handle each experiment separately ( until NCGC can do this in the background )
        // Note that each experiment corresponds to a column in our spreadsheet
        for (ExperimentSearch experiment in experimentList) {

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

    /**
     *
     * @param molSpreadSheetData
     * @param compoundAdapters
     * @param dataMap
     */
    protected void populateMolSpreadSheetRowMetadataFromCompoundAdapters(final MolSpreadSheetData molSpreadSheetData, final List<CompoundAdapter> compoundAdapters, Map<String, MolSpreadSheetCell> dataMap) {

        // add specific values for the cid column
        int rowCount = 0
        for (CompoundAdapter compoundAdapter in compoundAdapters) {
            updateMolSpreadSheetDataToReferenceCompound(
                    molSpreadSheetData,
                    rowCount++,
                    compoundAdapter.id,
                    compoundAdapter.name,
                    compoundAdapter.structureSMILES,
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
    protected void populateMolSpreadSheetRowMetadata(final MolSpreadSheetData molSpreadSheetData, final Map compoundAdapterMap, Map<String, MolSpreadSheetCell> dataMap) {

        // Add every compound we can find in the compound adapters map
        List<CompoundAdapter> compoundAdaptersList = compoundAdapterMap.compoundAdapters
        int rowCount = 0
        for (CompoundAdapter compoundAdapter : compoundAdaptersList) {
            String smiles = compoundAdapter.structureSMILES
            Long cid = compoundAdapter.pubChemCID
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
                                                                             String compoundSmiles, Map<String, MolSpreadSheetCell> dataMap,
                                                                             int numAssayActive,
                                                                             int numAssayTested) {
        // need to be able to map from CID to row location
        molSpreadSheetData.rowPointer.put(compoundId, rowCount)

        //TODO find a generic way to do this. It seems there are too many places to add new constant columns
        // add values for the cid column
        dataMap.put("${rowCount}_0".toString(), new MolSpreadSheetCell(compoundName, compoundSmiles, MolSpreadSheetCellType.image))
        dataMap.put("${rowCount}_1".toString(), new MolSpreadSheetCell(compoundId.toString(), MolSpreadSheetCellType.identifier))
        //we will use this to get the promiscuity score
        dataMap.put("${rowCount}_2".toString(), new MolSpreadSheetCell(compoundId.toString(), MolSpreadSheetCellType.identifier))
        //we will use this to get the 'active vrs tested' column
        dataMap.put("${rowCount}_3".toString(), new MolSpreadSheetCell("${numAssayActive} / ${numAssayTested}", MolSpreadSheetCellType.string))

        return molSpreadSheetData

    }

    /**
     *
     * @param molSpreadSheetData
     * @param experimentList
     */
    protected void populateMolSpreadSheetColumnMetadata(MolSpreadSheetData molSpreadSheetData, List<ExperimentSearch> experimentList) {

        // now retrieve the header names from the assays
        molSpreadSheetData.mssHeaders << new  MolSpreadSheetColumnHeader(molSpreadSheetColSubHeaderList:[new MolSpreadSheetColSubHeader(columnTitle:'Struct')])
        molSpreadSheetData.mssHeaders << new  MolSpreadSheetColumnHeader(molSpreadSheetColSubHeaderList:[new MolSpreadSheetColSubHeader(columnTitle:'CID')])
        molSpreadSheetData.mssHeaders << new  MolSpreadSheetColumnHeader(molSpreadSheetColSubHeaderList:[new MolSpreadSheetColSubHeader(columnTitle:'UNM Promiscuity Analysis')])
        molSpreadSheetData.mssHeaders << new  MolSpreadSheetColumnHeader(molSpreadSheetColSubHeaderList:[new MolSpreadSheetColSubHeader(columnTitle:'Active vs Tested across all Assay Definitions')])
        for (ExperimentSearch experiment : experimentList) {
            molSpreadSheetData.experimentNameList << "${experiment.assayId.toString()}".toString()
            molSpreadSheetData.experimentFullNameList << "${experiment.name.toString()}".toString()
            molSpreadSheetData.mssHeaders << new MolSpreadSheetColumnHeader(molSpreadSheetColSubHeaderList:[])
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
            if (experiments) {
                allExperiments.addAll(experiments)
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
    protected List<ExperimentSearch> assaysToExperiments(List<ExperimentSearch> incomingExperimentList, final List<Long> assayIds, Map<Long, Long> mapExperimentIdsToCapAssayIds) {

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
            final List<ExperimentSearch> experiments = assay.experiments
            if (experiments) {
                allExperiments.addAll(experiments)
                for (ExperimentSearch experimentSearch in experiments) {
                    mapExperimentIdsToCapAssayIds[experimentSearch.assayId] =  assay.capAssayId
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
    protected List<ExperimentSearch> assayIdsToExperiments(final List<ExperimentSearch> incomingExperimentList, final List<Long> adids, Map<Long, Long> mapExperimentIdsToCapAssayIds) {
        return assaysToExperiments(incomingExperimentList, adids, mapExperimentIdsToCapAssayIds)
    }
    /**
     *
     * @param cids
     * @return list of experiments
     */
    protected List<ExperimentSearch> compoundIdsToExperiments(final List<Long> cids, Map<Long, Long> mapExperimentIdsToCapAssayIds) {
        if (!cids) {
            return []
        }
        List<Assay> allAssays = []
        for (Long individualCompoundId in cids) {
            List<Assay> assays = compoundRestService.getTestedAssays(individualCompoundId, true)  // true = active only
            for (Assay assay in assays) {
                mapExperimentIdsToCapAssayIds[assay.bardAssayId] =  assay.capAssayId
            }
            allAssays.addAll(assays)
        }
        return assaysToExperiments(allAssays)
    }

    /**
     *
     * @param projectIds
     * @return list of Experiment's from a list of project Ids
     */
    protected List<ExperimentSearch> projectIdsToExperiments(final List<Long> projectIds, Map<Long, Long> mapExperimentIdsToCapAssayIds) {
        final ProjectResult projectResult = projectRestService.searchProjectsByIds(projectIds)
        return projectsToExperiments(projectResult, mapExperimentIdsToCapAssayIds)
    }

    protected List<ExperimentSearch> projectsToExperiments(final ProjectResult projectResult, Map<Long, Long> mapExperimentIdsToCapAssayIds) {
        final List<ExperimentSearch> allExperiments = []
        if (projectResult) {
            for (Project project : projectResult.projects) {
                ProjectExpanded projectExpanded = projectRestService.getProjectById(project.projectId)
                for (Assay assay in projectExpanded?.assays){
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
    protected void prepareMapOfColumnsToAssay(final MolSpreadSheetData molSpreadSheetData) {
        molSpreadSheetData.mapColumnsToAssay = [:]
        int columnIndex = 0
        int assayIndex = 0
        for (MolSpreadSheetColumnHeader molSpreadSheetColumnHeader  in molSpreadSheetData.mssHeaders) {
            List<String> listOfColumnSubheadings  = molSpreadSheetColumnHeader.molSpreadSheetColSubHeaderList*.columnTitle
            if (columnIndex < START_DYNAMIC_COLUMNS) {
                molSpreadSheetData.mapColumnsToAssay[columnIndex++] = ""
            } else {
                for (MolSpreadSheetColSubHeader molSpreadSheetColSubHeader in molSpreadSheetColumnHeader.molSpreadSheetColSubHeaderList){
                    for (int rowCnt in 0..molSpreadSheetData.rowCount)  {
                        SpreadSheetActivityStorage spreadSheetActivityStorage =  molSpreadSheetData?.findSpreadSheetActivity(rowCnt, columnIndex)
                        if (spreadSheetActivityStorage)  {
                            if (molSpreadSheetColSubHeader.unitsInColumn == null) {
                                molSpreadSheetColSubHeader.unitsInColumn =  spreadSheetActivityStorage.dictionaryDescription
                                molSpreadSheetColSubHeader.unitsInColumnAreUniform = true
                            }  else {
                                if (molSpreadSheetColSubHeader.unitsInColumn != spreadSheetActivityStorage.dictionaryDescription) {
                                    molSpreadSheetColSubHeader.unitsInColumnAreUniform = false
                                }
                            }
                        }
                    }
                }
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
    /**
     *
     * @param columnNames
     * @param spreadSheetActivity
     * @param activity
     */
    void addCurrentActivityToSpreadSheet(List <MolSpreadSheetColSubHeader> dummyColumnNames, SpreadSheetActivity spreadSheetActivity, final Activity activity) {
        spreadSheetActivity.activityToSpreadSheetActivity(activity, dummyColumnNames)
    }
    /**
     *
     * @param activity
     * @return SpreadSheetActivity
     */
    SpreadSheetActivity extractActivitiesFromExperiment(final Activity activity) {
        List <MolSpreadSheetColSubHeader> dummyHeadersList = []
        final SpreadSheetActivity spreadSheetActivity = new SpreadSheetActivity()
        addCurrentActivityToSpreadSheet(dummyHeadersList, spreadSheetActivity, activity)
        return spreadSheetActivity
    }
}
