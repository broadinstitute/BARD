package bardqueryapi

import bard.core.adapter.CompoundAdapter
import bard.core.rest.RESTCompoundService
import bard.core.rest.RESTExperimentService
import com.metasieve.shoppingcart.ShoppingCartService
import org.apache.commons.lang3.time.StopWatch
import bard.core.*

class MolecularSpreadSheetService {

    QueryCartService queryCartService
    QueryServiceWrapper queryServiceWrapper
    ShoppingCartService shoppingCartService
    QueryHelperService queryHelperService
    IQueryService queryService

    /**
     * High-level routine to pull information out of the query cart and store it into a data structure suitable
     * for passing to the molecular spreadsheet.
     *
     * @return MolSpreadSheetData
     */
    MolSpreadSheetData retrieveExperimentalData() {
        MolSpreadSheetData molSpreadSheetData = new MolSpreadSheetData()

        if (queryCartService.totalNumberOfUniqueItemsInCart(shoppingCartService) > 0) {

            List<CartCompound> cartCompoundList = retrieveCartCompoundFromShoppingCart()
            List<CartAssay> cartAssayList = retrieveCartAssayFromShoppingCart()
            List<CartProject> cartProjectList = retrieveCartProjectFromShoppingCart()


            if ((cartAssayList.size() > 0) &&
                    (cartCompoundList.size() > 0)) {
                // Explicitly specified assays and explicitly specified compounds
                List<Experiment> experimentList = cartAssaysToExperiments(cartAssayList)
                molSpreadSheetData = populateMolSpreadSheetRowMetadata(molSpreadSheetData, cartCompoundList)
                molSpreadSheetData = populateMolSpreadSheetColumnMetadata(molSpreadSheetData, cartAssayList)
                Object etag = generateETagFromCartCompounds(cartCompoundList)
                List<SpreadSheetActivity> SpreadSheetActivityList = extractMolSpreadSheetData(molSpreadSheetData, experimentList, etag)
                populateMolSpreadSheetData(molSpreadSheetData, experimentList, SpreadSheetActivityList)

            } else if ((cartAssayList.size() > 0) &&
                    (cartCompoundList.size() == 0)) {
                // Explicitly specified assay, for which we will retrieve all compounds
                List<Experiment> experimentList = cartAssaysToExperiments(cartAssayList)
                Object etag = retrieveImpliedCompoundsEtagFromAssaySpecification(experimentList)
                molSpreadSheetData = populateMolSpreadSheetColumnMetadata(molSpreadSheetData, cartAssayList)
                List<SpreadSheetActivity> SpreadSheetActivityList = extractMolSpreadSheetData(molSpreadSheetData, experimentList, etag)
                Map map = convertSpreadSheetActivityToCompoundInformation(SpreadSheetActivityList)
                molSpreadSheetData = populateMolSpreadSheetRowMetadata(molSpreadSheetData, map)
                populateMolSpreadSheetData(molSpreadSheetData, experimentList, SpreadSheetActivityList)
            }


        } else
            molSpreadSheetData = new MolSpreadSheetData()

        molSpreadSheetData
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
        boolean returnValue = true
        LinkedHashMap<String, List> itemsInShoppingCart = queryCartService.groupUniqueContentsByType(shoppingCartService)
        if (queryCartService?.totalNumberOfUniqueItemsInCart(itemsInShoppingCart, QueryCartService.cartAssay) < 1)
            returnValue = false
        if (queryCartService?.totalNumberOfUniqueItemsInCart(itemsInShoppingCart, QueryCartService.cartCompound) < 0)
            returnValue = false
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
                    String arrayKey = "${innerRowPointer}_${innerColumnCount + 2}"
                    Double activityValue = spreadSheetActivity.interpretHillCurveValue()
                    MolSpreadSheetCell molSpreadSheetCell = new MolSpreadSheetCell(activityValue.toString(), MolSpreadSheetCellType.numeric)
                    if (activityValue == Double.NaN)
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








    protected Object retrieveImpliedCompoundsEtagFromAssaySpecification(List<Experiment> experimentList) {
        int numVals = 1000
        Object etag = null
        def compoundList = new ArrayList<Compound>()
        for (Experiment experiment in experimentList) {
            final ServiceIterator<Compound> compoundServiceIterator = this.queryServiceWrapper.restExperimentService.compounds(experiment)
            numVals = 1000
            List<Compound> singleExperimentCompoundList = compoundServiceIterator.next(numVals - 1)
            if (etag == null)
                etag = this.queryServiceWrapper.restCompoundService.newETag("dsa", singleExperimentCompoundList*.id);
            else
                this.queryServiceWrapper.restCompoundService.putETag(etag, singleExperimentCompoundList*.id);
        }
        etag
    }

//    protected List<Compound> retrieveCartCompoundFromAssaySpecification(List<Experiment> experimentList) {
//        int  numVals = 1
//        def compoundList = new ArrayList<Compound>()
//        for (Experiment experiment in experimentList) {
//            final ServiceIterator<Compound> compoundServiceIterator = this.queryServiceWrapper.restExperimentService.compounds(experiment)
//            List<Compound> singleExperimentCompoundList = compoundServiceIterator.next(numVals)
//            Object etag = this.queryServiceWrapper.restCompoundService.newETag("dsa", singleExperimentCompoundList*.id);
//            this.queryServiceWrapper.restExperimentService.activities(experiment, etag);
//            ServiceIterator<Value> eiter = this.queryServiceWrapper.restExperimentService.activities(experiment, etag);
//            while (eiter.hasNext()) {
//                Value value = eiter.next()
//                println  value
//            }
//        }
//        compoundList
//    }

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
            Long cid = compoundAdapter.entity.id
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

        molSpreadSheetData

    }



    protected MolSpreadSheetData populateMolSpreadSheetColumnMetadata(MolSpreadSheetData molSpreadSheetData, List<CartAssay> cartAssayList) {
        // get the header names from the assays
        molSpreadSheetData.mssHeaders.add("Struct")
        molSpreadSheetData.mssHeaders.add("CID")
        for (CartAssay cartAssay in cartAssayList) {
            molSpreadSheetData.mssHeaders.add(cartAssay.assayTitle)
        }

        molSpreadSheetData
    }


    protected Object generateETagFromCartCompounds(List<CartCompound> cartCompoundList) {
        List<Long> cartCompoundIdList = new ArrayList<Long>()
        for (CartCompound cartCompound in cartCompoundList)
            cartCompoundIdList.add(new Long(cartCompound.compoundId))
        Date date = new Date()

        queryServiceWrapper.restCompoundService.newETag(date.toString(), cartCompoundIdList);
    }

    // start by working through the compounds. First gather CartCompound

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
    protected List<Experiment> cartAssaysToExperiments(final List<CartAssay> cartAssays) {
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

        return allExperiments
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
        Collection<Project> projects = queryServiceWrapper.getRestProjectService().get(projectIds)
        for (Project project : projects) {
            Collection<Experiment> experiments = project.getExperiments()
            allExperiments.addAll(experiments)
        }
        return allExperiments
    }
    /**
     *
     * @param cartCompounds
     * @param cartAssays
     * @param cartProjects
     * @return list of SpreadSheetActivities
     */
    public List<SpreadSheetActivity> getMolecularSpreadSheet(final List<CartCompound> cartCompounds,
                                                             final List<CartAssay> cartAssays,
                                                             final List<CartProject> cartProjects) {

        if (!cartCompounds) {
            throw new RuntimeException("There must be at least one Compound in the cart")
        }
        if (!cartAssays && !cartProjects) {
            throw new RuntimeException("At least one Project or Assay must be in the Cart")
        }

        //TODO: add assertions here
        List<Experiment> experiments = []

        experiments.addAll(cartAssaysToExperiments(cartAssays))
        experiments.addAll(cartProjectsToExperiments(cartProjects))
        List<Long> cids = cartCompoundsToCIDS(cartCompounds)

        StopWatch stopWatch = queryHelperService.startStopWatch()
        //TODO: create the ETAG, we should randomize this to support multithreading
        final String eTagName = "ETAG_" + stopWatch.getStartTime().toString()
        final RESTCompoundService restCompoundService = queryServiceWrapper.getRestCompoundService()
        Object etag = restCompoundService.newETag(eTagName, cids)

        List<SpreadSheetActivity> spreadSheetActivities = []
        for (Experiment experiment : experiments) {
            spreadSheetActivities.addAll(findActivitiesForCompounds(experiment, etag))
        }

        return spreadSheetActivities

    }
    /**
     *
     * @param experimentId
     * @param compoundETags - Just wish these etags were typed
     * @return List of activities
     */
    List<SpreadSheetActivity> findActivitiesForCompounds(MolSpreadSheetData molSpreadSheetData, final Experiment experiment, final Object compoundETag) {
        final List<SpreadSheetActivity> spreadSheetActivities = new ArrayList<SpreadSheetActivity>()
        final ServiceIterator<Value> experimentIterator = this.queryServiceWrapper.restExperimentService.activities(experiment, compoundETag);
        int rowPointer = 0
        while (experimentIterator.hasNext()) {
            Value experimentValue = experimentIterator.next()
            if (experimentValue) {
                SpreadSheetActivity spreadSheetActivity = extractActivitiesFromExperiment(experimentValue)
                updateMolSpreadSheetDataToReferenceCompound(molSpreadSheetData, rowPointer, spreadSheetActivity.cid as Long, spreadSheetActivity.cid, spreadSheetActivity.cid)
                spreadSheetActivities.add(spreadSheetActivity)
            }
        }
        return spreadSheetActivities
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

    public Map<Long,List<SpreadSheetActivity>> findExperimentDataById(final Long experimentId, final int top = 10, final int skip = 0) {
        List<SpreadSheetActivity> spreadSheetActivities = []
        final RESTExperimentService restExperimentService = queryServiceWrapper.getRestExperimentService()
        Experiment experiment = restExperimentService.get(experimentId)
        final AssayValues.AssayRole role = experiment?.getAssay()?.getRole()
        final ServiceIterator<Value> experimentIterator = restExperimentService.activities(experiment);

        List<Value> activityValues = []
        if (experimentIterator.hasNext()) {
            if (skip == 0) {
                activityValues = experimentIterator.next(top)
            }
            else { //There is no wau to pass in skip and top to the iterator so we get have to do this hack
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
        final long totalNumberOfRecords = experimentIterator.getCount()
        return [total : totalNumberOfRecords,  spreadSheetActivities:spreadSheetActivities, role: role]
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

    // we need a real valued iterpretation of the hillCurveValue.  Someday this must be meaningful, but
    // for now we need a placeholder
//    public double interpretHillCurveValue(){
//        double retValue = 0d
//        if (this.hillCurveValue!=null) {
//           if (this.hillCurveValue.getValue()!=null)
//               retValue = this.hillCurveValue.getValue()
//            else if (this.hillCurveValue.getConc()!=null){
//               if (this.hillCurveValue.getConc().size()>1) {
//                   int midpoint =  this.hillCurveValue.getConc().size()/2
//                   retValue = this.hillCurveValue.getConc()[midpoint]
//               }
//           }
//        }
//        retValue
//    }


    public double interpretHillCurveValue() {
        double retValue = 0d
        if (this.hillCurveValue != null) {
            if (this.hillCurveValue.getS0() == null)
                retValue = Double.NaN
            else
                retValue = this.hillCurveValue.getS0()
        }
        retValue
    }


}
