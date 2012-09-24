package bardqueryapi

import bard.core.adapter.CompoundAdapter
import bard.core.rest.RESTExperimentService
import com.metasieve.shoppingcart.ShoppingCartService
import molspreadsheet.MolSpreadSheetCell
import molspreadsheet.MolSpreadSheetData
import bard.core.*
import molspreadsheet.SpreadSheetActivityStorage

class MolecularSpreadSheetService {

    QueryCartService queryCartService
    QueryServiceWrapper queryServiceWrapper
    ShoppingCartService shoppingCartService
    IQueryService queryService

    final static Integer MAXIMUM_NUMBER_OF_COMPOUNDS = 1000

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

        molSpreadSheetDataBuilderDirector.getMolSpreadSheetData()
    }

    /**
     * For a given SpreadSheetActivityList, pull back a map specifying all compounds, along with structures, CIDs, etc
     * @param SpreadSheetActivityList
     * @return
     */
    Map convertSpreadSheetActivityToCompoundInformation(List<SpreadSheetActivity> SpreadSheetActivityList) {
        def compoundIds = new ArrayList<Long>()
        for (SpreadSheetActivity spreadSheetActivity in SpreadSheetActivityList) {
            compoundIds.add(spreadSheetActivity.cid)
        }
        List<SearchFilter> filters = new ArrayList<SearchFilter>()
        queryService.findCompoundsByCIDs(compoundIds, filters)
    }
    /**
     * When do we have sufficient data to charge often try to build a spreadsheet?
     *
     * @return
     */
    Boolean weHaveEnoughDataToMakeASpreadsheet() {
        boolean returnValue = false
        LinkedHashMap<String, List> itemsInShoppingCart = queryCartService.groupUniqueContentsByType(shoppingCartService)
        if (queryCartService?.totalNumberOfUniqueItemsInCart(itemsInShoppingCart, QueryCartService.cartProject) > 0)
            returnValue = true
        else if (queryCartService?.totalNumberOfUniqueItemsInCart(itemsInShoppingCart, QueryCartService.cartAssay) > 0)
            returnValue = true
        returnValue
    }

    /**
     * For a set of experiments
     * @param experimentList
     * @param etag
     * @return
     */
    protected List<SpreadSheetActivity> extractMolSpreadSheetData(MolSpreadSheetData molSpreadSheetData, List<Experiment> experimentList, Object etag) {
        // now step through the data and place into molSpreadSheetData
        List<SpreadSheetActivity> spreadSheetActivityList = new ArrayList<SpreadSheetActivity>()

        // we need to handle each experiment separately ( until NCGC can do this in the background )
        // Note that each experiment corresponds to a column in our spreadsheet
        int columnCount = 0
        for (Experiment experiment in experimentList) {

            ServiceIterator<Value> experimentIterator = queryServiceWrapper.restExperimentService.activities(experiment, etag)

            // Now step through the result set and pull back  one value for each compound
            Value experimentValue
            while (experimentIterator.hasNext()) {
                experimentValue = experimentIterator.next()
                Long translation = new Long(experimentValue.id.split("\\.")[0])
                if (!molSpreadSheetData.columnPointer.containsKey(translation)) {
                    molSpreadSheetData.columnPointer.put(translation, columnCount)
                }
                spreadSheetActivityList.add(extractActivitiesFromExperiment(experimentValue))
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
        // we need to handle each experiment separately ( until NCGC can do this in the background )
        // Note that each experiment corresponds to a column in our spreadsheet
        for (Experiment experiment in experimentList) {

            for (SpreadSheetActivity spreadSheetActivity in spreadSheetActivityList) {
                if (molSpreadSheetData.rowPointer.containsKey(spreadSheetActivity.cid)) {
                    int innerRowPointer = molSpreadSheetData.rowPointer[spreadSheetActivity.cid]
                    int innerColumnCount = molSpreadSheetData.columnPointer[spreadSheetActivity.eid]
                    String arrayKey = "${innerRowPointer}_${innerColumnCount + 3}"
                    SpreadSheetActivityStorage spreadSheetActivityStorage = spreadSheetActivity.toSpreadSheetActivityStorage()
                    MolSpreadSheetCell molSpreadSheetCell = new MolSpreadSheetCell( spreadSheetActivity.interpretHillCurveValue().toString(),
                                                                                    MolSpreadSheetCellType.numeric,
                                                                                    MolSpreadSheetCellUnit.Micromolar,
                                                                                    spreadSheetActivityStorage)
                    if (spreadSheetActivityStorage == null)
                        molSpreadSheetCell.activity = false
                    molSpreadSheetData.mssData.put(arrayKey, molSpreadSheetCell)
                }
                else
                    assert false, "did not expect cid = ${spreadSheetActivity.cid}"

            }
            columnPointer++
        }
        molSpreadSheetData
    }

    /**
     *
     * @param experimentList
     * @return
     */
    protected Object retrieveImpliedCompoundsEtagFromAssaySpecification(List<Experiment> experimentList) {
        Object etag = null
        for (Experiment experiment in experimentList) {
            final ServiceIterator<Compound> compoundServiceIterator = this.queryServiceWrapper.restExperimentService.compounds(experiment)
            List<Compound> singleExperimentCompoundList = compoundServiceIterator.next(MAXIMUM_NUMBER_OF_COMPOUNDS)
            if (etag == null)
                etag = this.queryServiceWrapper.restCompoundService.newETag("dsa", singleExperimentCompoundList*.id);
            else if ( (singleExperimentCompoundList != null) &&
                      (singleExperimentCompoundList.size() > 0) )
                this.queryServiceWrapper.restCompoundService.putETag(etag, singleExperimentCompoundList*.id);
        }
        etag
    }

    protected List<CartCompound> retrieveCartCompoundFromShoppingCart() {
        List<CartCompound> cartCompoundList = new ArrayList<CartCompound>()
        for (CartCompound cartCompound in (queryCartService.groupUniqueContentsByType(shoppingCartService)[(QueryCartService.cartCompound)])) {
            cartCompoundList.add(cartCompound)
        }
        cartCompoundList
    }

    protected List<CartAssay> retrieveCartAssayFromShoppingCart() {
        List<CartAssay> cartAssayList = new ArrayList<CartAssay>()
        for (CartAssay cartAssay in (queryCartService.groupUniqueContentsByType(shoppingCartService)[(QueryCartService.cartAssay)])) {
            cartAssayList.add(cartAssay)
        }
        cartAssayList
    }

    protected List<CartProject> retrieveCartProjectFromShoppingCart() {
        List<CartProject> cartProjectList = new ArrayList<CartProject>()
        for (CartProject cartProject in (queryCartService.groupUniqueContentsByType(shoppingCartService)[(QueryCartService.cartProject)])) {
            cartProjectList.add(cartProject)
        }
        cartProjectList
    }




    protected MolSpreadSheetData populateMolSpreadSheetRowMetadata(MolSpreadSheetData molSpreadSheetData, List<CartCompound> cartCompoundList) {

        // add values for the cid column
        int rowCount = 0
        for (CartCompound cartCompound in cartCompoundList) {
            updateMolSpreadSheetDataToReferenceCompound(molSpreadSheetData, rowCount++, cartCompound.compoundId as Long, cartCompound.name, cartCompound.smiles)
        }

        molSpreadSheetData
    }


    protected MolSpreadSheetData populateMolSpreadSheetRowMetadata(MolSpreadSheetData molSpreadSheetData, Map compoundAdapterMap) {
        List<CompoundAdapter> compoundAdaptersList = compoundAdapterMap.compoundAdapters
        int rowCount = 0
        for (CompoundAdapter compoundAdapter in compoundAdaptersList) {
            String smiles = compoundAdapter.structureSMILES
            Long cid = compoundAdapter.pubChemCID
            String name = compoundAdapter.name
            updateMolSpreadSheetDataToReferenceCompound(molSpreadSheetData, rowCount++, cid, name, smiles)
        }
        molSpreadSheetData
    }



    protected MolSpreadSheetData updateMolSpreadSheetDataToReferenceCompound(MolSpreadSheetData molSpreadSheetData,
                                                                             int rowCount,
                                                                             Long compoundId,
                                                                             String compoundName,
                                                                             String compoundSmiles) {
        // need to be able to map from CID to row location
        molSpreadSheetData.rowPointer.put(compoundId, rowCount)

        // add values for the cid column
        molSpreadSheetData.mssData.put("${rowCount}_0".toString(), new MolSpreadSheetCell(compoundName, compoundSmiles, MolSpreadSheetCellType.image))
        molSpreadSheetData.mssData.put("${rowCount++}_1".toString(), new MolSpreadSheetCell(compoundId.toString(), MolSpreadSheetCellType.identifier))
        //we will use this to get the promiscuity score
        molSpreadSheetData.mssData.put("${rowCount++}_1".toString(), new MolSpreadSheetCell(compoundId.toString(), MolSpreadSheetCellType.identifier))

        molSpreadSheetData

    }



    protected MolSpreadSheetData populateMolSpreadSheetColumnMetadata(MolSpreadSheetData molSpreadSheetData, List<Experiment> experimentList) {
        // get the header names from the assays
        molSpreadSheetData.mssHeaders.add("Struct")
        molSpreadSheetData.mssHeaders.add("CID")
        molSpreadSheetData.mssHeaders.add("UNM Promiscuity Analysis")
        for (Experiment experiment in experimentList)
            molSpreadSheetData.mssHeaders.add(experiment.name)

        molSpreadSheetData
    }



    protected Object generateETagFromCartCompounds(List<CartCompound> cartCompoundList) {
        List<Long> cartCompoundIdList = new ArrayList<Long>()
        for (CartCompound cartCompound in cartCompoundList)
            cartCompoundIdList.add(new Long(cartCompound.compoundId))
        Date date = new Date()

        queryServiceWrapper.restCompoundService.newETag(date.toString(), cartCompoundIdList);
    }

    /**
     *
     * @param cartCompounds
     * @return list of Experiment's from a list of CartCompound's
     */
    protected List<Long> cartCompoundsToCIDS(final List<CartCompound> cartCompounds) {
        List<Long> cids = []
        for (CartCompound cartCompound : cartCompounds) {
            long cid = cartCompound.compoundId
            cids.add(cid)
        }

        return cids
    }

    /**
     * Convert Assay ODs to expt ids
     * @param cartAssays
     * @return
     */
    protected List<Long> generateCompoundListFromAssays(final List<CartAssay> cartAssays) {
        List<Long> assayIds = []
        for (CartAssay cartAssay : cartAssays) {
            long assayId = cartAssay.assayId
            assayIds.add(assayId)
        }

        List<Experiment> allExperiments = []
        for (Long individualAssayIds in assayIds) {
            Assay assay = queryServiceWrapper.getRestAssayService().get(individualAssayIds)
            Collection<Experiment> experimentList = assay.getExperiments()
            for (Experiment experiment in experimentList) {
                allExperiments.add(experiment)
            }
        }
        new ArrayList<Long>()
    }

    /**
     * Convert Assay ODs to expt ids
     * @param cartAssays
     * @return
     */
    protected List<Experiment> assaysToExperiments(final Collection<Assay> assays) {
        List<Experiment> allExperiments = []
        if (!assays.isEmpty()) {
            Iterator<Assay> assayIterator = assays.iterator()
            while (assayIterator.hasNext()) {
                Assay assay = assayIterator.next()
                Collection<Experiment> experimentList = assay.getExperiments()
                for (Experiment experiment in experimentList) {
                    allExperiments.add(experiment)
                }
            }
        }

        return allExperiments
    }

    /**
     * Convert Cart assays to Experiments
     * @param cartAssays
     * @return
     */
    protected List<Experiment> cartAssaysToExperiments(List<Experiment> incomingExperimentList, final List<CartAssay> cartAssays) {
        List<Long> assayIds = []
        for (CartAssay cartAssay : cartAssays) {
            long assayId = cartAssay.assayId
            assayIds.add(assayId)
        }

        List<Experiment> allExperiments
        if (incomingExperimentList == null)
            allExperiments = []
        else
            allExperiments = incomingExperimentList
        for (Long individualAssayIds in assayIds) {
            Assay assay = queryServiceWrapper.getRestAssayService().get(individualAssayIds)
            Collection<Experiment> experimentList = assay.getExperiments()
            for (Experiment experiment in experimentList) {
                allExperiments.add(experiment)
            }
        }

        return allExperiments
    }
    /**
     *
     * @param cartProjects
     * @return list of Experiment's from a list of CartProject's
     */
    protected List<Assay> cartProjectsToExperiments(final List<CartProject> cartProjects) {
        List<Long> projectIds = []
        for (CartProject cartProject : cartProjects) {
            long projectId = cartProject.projectId
            projectIds.add(projectId)
        }
        List<Experiment> allExperiments = []
        Collection<Project> projects = queryServiceWrapper.getRestProjectService().get(projectIds)
        for (Project project : projects) {
            Collection<Assay> assays = project.getAssays()
            if (!assays.isEmpty()) {
                Iterator<Assay> assayIterator = assays.iterator()
                while (assayIterator.hasNext()) {
                    Assay assay = assayIterator.next()
                    Collection<Experiment> experimentList = assay.getExperiments()
                    for (Experiment experiment in experimentList) {
                        allExperiments.add(experiment)
                    }
                }
            }
        }
        return allExperiments
    }



    List<SpreadSheetActivity> findActivitiesForCompounds(final Experiment experiment, final Object compoundETag) {
        final List<SpreadSheetActivity> spreadSheetActivities = new ArrayList<SpreadSheetActivity>()
        final ServiceIterator<Value> experimentIterator = this.queryServiceWrapper.restExperimentService.activities(experiment, compoundETag);
        while (experimentIterator.hasNext()) {
            Value experimentValue = experimentIterator.next()
            if (experimentValue) {
                SpreadSheetActivity spreadSheetActivity = extractActivitiesFromExperiment(experimentValue)
                spreadSheetActivities.add(spreadSheetActivity)
            }
        }
        return spreadSheetActivities
    }


    public Map findExperimentDataById(final Long experimentId, final int top = 10, final int skip = 0) {
        List<SpreadSheetActivity> spreadSheetActivities = []
        final RESTExperimentService restExperimentService = queryServiceWrapper.getRestExperimentService()
        long totalNumberOfRecords = 0
        AssayValues.AssayRole role = null
        Experiment experiment = restExperimentService.get(experimentId)
        if (experiment) {
            role = experiment?.getAssay()?.getRole()
            final ServiceIterator<Value> experimentIterator = restExperimentService.activities(experiment);

            List<Value> activityValues = []
            if (experimentIterator.hasNext()) {
                if (skip == 0) {
                    activityValues = experimentIterator.next(top)
                }
                else { //There is no way to pass in skip and top to the iterator so we have to do this hack
                    //which is not perfect
                    activityValues = experimentIterator.next(top + skip)
                    activityValues = activityValues.subList(skip, activityValues.size())
                }
            }
            final Iterator<Value> iterator = activityValues.iterator()
            while (iterator.hasNext()) {
                Value experimentValue = iterator.next()
                SpreadSheetActivity spreadSheetActivity = extractActivitiesFromExperiment(experimentValue)
                spreadSheetActivities.add(spreadSheetActivity)
            }

            totalNumberOfRecords = experimentIterator.getCount()
        }
        return [total: totalNumberOfRecords, spreadSheetActivities: spreadSheetActivities, role: role]
    }

    /**
     *
     * @param experimentValue
     * @return SpreadSheetActivity
     */
    SpreadSheetActivity extractActivitiesFromExperiment(final Value experimentValue) {
        final Iterator<Value> experimentValueIterator = experimentValue.children()
        SpreadSheetActivity spreadSheetActivity = new SpreadSheetActivity()
        while (experimentValueIterator.hasNext()) {
            Value childValue = experimentValueIterator.next()
            addCurrentActivityToSpreadSheet(spreadSheetActivity, childValue)
        }
        return spreadSheetActivity
    }
    /**
     *
     * @param spreadSheetActivity
     * @param childValue
     */
    void addCurrentActivityToSpreadSheet(final SpreadSheetActivity spreadSheetActivity, final Value childValue) {
        String identifier = childValue.id
        if (childValue instanceof HillCurveValue) {
            spreadSheetActivity.hillCurveValue = childValue
            return
        }
        switch (identifier) {
            case "eid":
                spreadSheetActivity.eid = childValue.value
                break
            case "cid":

                spreadSheetActivity.cid = childValue.value
                break
            case "sid":
                spreadSheetActivity.sid = childValue.value
                break
            case "Activity":
                spreadSheetActivity.hillCurveValue = childValue.value
                break
            default:
                println "unknown value"
        }
    }

}

/**
 * Since there is no experimentAdapter I had to make a method to open up the experiment
 */

public class SpreadSheetActivity {
    Long eid
    Long cid
    Long sid
    HillCurveValue hillCurveValue

    public SpreadSheetActivity() {

    }



    SpreadSheetActivityStorage toSpreadSheetActivityStorage () {
        SpreadSheetActivityStorage spreadSheetActivityStorage  = new SpreadSheetActivityStorage ()
        spreadSheetActivityStorage.sid =  sid
        spreadSheetActivityStorage.eid =  eid
        spreadSheetActivityStorage.cid =  cid
        if (hillCurveValue != null)     {
            spreadSheetActivityStorage.hillCurveValueId =  hillCurveValue.id
            spreadSheetActivityStorage.hillCurveValueSInf =  hillCurveValue.setSinf()
            spreadSheetActivityStorage.hillCurveValueS0 =  hillCurveValue.s0
            spreadSheetActivityStorage.hillCurveValueSlope =  hillCurveValue.slope
            spreadSheetActivityStorage.hillCurveValueCoef =  hillCurveValue.coef
            spreadSheetActivityStorage.hillCurveValueConc =  hillCurveValue.conc
            spreadSheetActivityStorage.hillCurveValueResponse =  hillCurveValue.response
        }
        spreadSheetActivityStorage
    }

    /**
     *
     * @param eid
     * @param cid
     * @param sid
     * @param hillCurveValue
     */
    public SpreadSheetActivity(Long eid,
                               Long cid,
                               Long sid,
                               HillCurveValue hillCurveValue) {
        this.eid = eid
        this.cid = cid
        this.sid = sid
        this.hillCurveValue = hillCurveValue
    }

    public String toString() {
        final List<String> stringBuilder = []
        stringBuilder.add("CID: ${this.cid}")
        stringBuilder.add("EID: ${this.eid}")
        stringBuilder.add("SID: ${this.sid}")
        stringBuilder.add("Hill Curve Value:\n ${this.hillCurveValue}")
        return stringBuilder.join("\n").toString()
    }



    public Double interpretHillCurveValue() {
        Double retValue = Double.NaN

        if (this.hillCurveValue != null)
            retValue = (this.hillCurveValue.getSlope() == null) ? Double.NaN : this.hillCurveValue.getSlope()

        retValue
    }


}
