package bardqueryapi

import bard.core.rest.RESTCompoundService
import com.metasieve.shoppingcart.ShoppingCartService
import org.apache.commons.lang3.time.StopWatch
import bard.core.*
import bard.core.adapter.CompoundAdapter

class MolecularSpreadSheetService {

    QueryCartService queryCartService
    QueryServiceWrapper queryServiceWrapper
    ShoppingCartService shoppingCartService
    QueryHelperService queryHelperService
    IQueryService queryService

    final static Integer MAXIMUM_NUMBER_OF_COMPOUNDS = 1000

    /**
     * High-level routine to pull information out of the query cart and store it into a data structure suitable
     * for passing to the molecular spreadsheet.
     *
     * @return  MolSpreadSheetData
     */
    MolSpreadSheetData retrieveExperimentalData() {
        MolSpreadSheetData molSpreadSheetData   = new MolSpreadSheetData()

        if (queryCartService.totalNumberOfUniqueItemsInCart(shoppingCartService) > 0) {

            List<CartCompound> cartCompoundList = retrieveCartCompoundFromShoppingCart()
            List<CartAssay> cartAssayList = retrieveCartAssayFromShoppingCart()
            List<CartProject> cartProjectList = retrieveCartProjectFromShoppingCart()
            Object etag
            List<SpreadSheetActivity> SpreadSheetActivityList

            // need a list of experiments to work with.  Build up that list...
            List<Experiment> experimentList =  new ArrayList<Experiment>()

            // Any projects can be converted to assays, then assays to experiments
            if (cartProjectList.size() > 0)   {
                Collection <Assay> assayCollection = cartProjectsToAssays( cartProjectList)
                experimentList =  assaysToExperiments(assayCollection)
            }

            // Any assays explicitly selected on the cart are added to the  experimentList
            if (cartAssayList.size() > 0)
                experimentList = cartAssaysToExperiments(experimentList,cartAssayList)

            // next deal with the compounds
            if (experimentList.size() > 0) {

                if (cartCompoundList.size() > 0) {
                    // Explicitly specified assays and explicitly specified compounds
                    molSpreadSheetData = populateMolSpreadSheetRowMetadata(molSpreadSheetData, cartCompoundList)
                    molSpreadSheetData = populateMolSpreadSheetColumnMetadata(molSpreadSheetData, experimentList)
                    etag = generateETagFromCartCompounds(cartCompoundList)
                    SpreadSheetActivityList = extractMolSpreadSheetData(molSpreadSheetData, experimentList, etag)
                } else if (cartCompoundList.size() == 0) {
                    // Explicitly specified assay, for which we will retrieve all compounds
                    etag = retrieveImpliedCompoundsEtagFromAssaySpecification(experimentList)
                    molSpreadSheetData = populateMolSpreadSheetColumnMetadata(molSpreadSheetData, experimentList)
                    SpreadSheetActivityList = extractMolSpreadSheetData(molSpreadSheetData, experimentList, etag)
                    Map map = convertSpreadSheetActivityToCompoundInformation(SpreadSheetActivityList)
                    molSpreadSheetData = populateMolSpreadSheetRowMetadata(molSpreadSheetData, map)
                }

                // finally deal with the data
                populateMolSpreadSheetData(molSpreadSheetData, experimentList, SpreadSheetActivityList)
            } else

                molSpreadSheetData = new MolSpreadSheetData()
        }

        molSpreadSheetData
    }

    /**
     * For a given SpreadSheetActivityList, pull back a map specifying all compounds, along with structures, CIDs, etc
     * @param SpreadSheetActivityList
     * @return
     */
    Map convertSpreadSheetActivityToCompoundInformation(List<SpreadSheetActivity> SpreadSheetActivityList) {
        def compoundIds = new ArrayList<Long> ()
        for (SpreadSheetActivity spreadSheetActivity in SpreadSheetActivityList) {
            compoundIds.add(spreadSheetActivity.cid)
        }
        List<SearchFilter> filters = new ArrayList<SearchFilter> ()
        queryService.findCompoundsByCIDs(compoundIds,filters)
    }






    /**
     * When do we have sufficient data to charge often try to build a spreadsheet?
     *
     * @return
     */
    Boolean weHaveEnoughDataToMakeASpreadsheet() {
        boolean returnValue = false
        LinkedHashMap<String,List> itemsInShoppingCart = queryCartService.groupUniqueContentsByType(shoppingCartService)
        if (queryCartService?.totalNumberOfUniqueItemsInCart(itemsInShoppingCart,QueryCartService.cartProject)>0)
            returnValue = true
        else if (queryCartService?.totalNumberOfUniqueItemsInCart(itemsInShoppingCart,QueryCartService.cartAssay)>0)
            returnValue = true
        returnValue
    }


    /**
     * For a set of experiments
     * @param experimentList
     * @param etag
     * @return
     */
      protected List<SpreadSheetActivity> extractMolSpreadSheetData(MolSpreadSheetData molSpreadSheetData,List<Experiment> experimentList, Object etag) {
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
                   molSpreadSheetData.columnPointer.put(translation,columnCount)
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
                    String arrayKey = "${innerRowPointer}_${innerColumnCount+2}"
                    Double activityValue =  spreadSheetActivity.interpretHillCurveValue()
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

    /**
     *
     * @param experimentList
     * @return
     */
    protected Object retrieveImpliedCompoundsEtagFromAssaySpecification(List<Experiment> experimentList) {
        Object etag = null
        def compoundList = new ArrayList<Compound>()
        for (Experiment experiment in experimentList) {
            final ServiceIterator<Compound> compoundServiceIterator = this.queryServiceWrapper.restExperimentService.compounds(experiment)
            List<Compound> singleExperimentCompoundList = compoundServiceIterator.next(MAXIMUM_NUMBER_OF_COMPOUNDS)
            if  ( etag == null )
                etag = this.queryServiceWrapper.restCompoundService.newETag("dsa", singleExperimentCompoundList*.id);
            else
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




    protected  MolSpreadSheetData populateMolSpreadSheetRowMetadata(MolSpreadSheetData molSpreadSheetData,List<CartCompound> cartCompoundList) {

        // add values for the cid column
        int rowCount = 0
        for (CartCompound cartCompound in cartCompoundList){
            updateMolSpreadSheetDataToReferenceCompound(  molSpreadSheetData,rowCount++,cartCompound.compoundId as Long,cartCompound.name,cartCompound.smiles)
         }

        molSpreadSheetData
    }


    protected  MolSpreadSheetData populateMolSpreadSheetRowMetadata(MolSpreadSheetData molSpreadSheetData,Map compoundAdapterMap) {
        List<CompoundAdapter> compoundAdaptersList = compoundAdapterMap.compoundAdapters
        int rowCount = 0
        for(CompoundAdapter compoundAdapter in compoundAdaptersList)  {
            String smiles = compoundAdapter.structureSMILES
            Long cid = compoundAdapter.entity.id
            String name = compoundAdapter.name
            updateMolSpreadSheetDataToReferenceCompound(  molSpreadSheetData,rowCount++,cid,name,smiles)
        }
        molSpreadSheetData
    }



    protected MolSpreadSheetData updateMolSpreadSheetDataToReferenceCompound( MolSpreadSheetData molSpreadSheetData,
                                                                              int rowCount,
                                                                              Long compoundId,
                                                                              String compoundName,
                                                                              String compoundSmiles) {
        // need to be able to map from CID to row location
        molSpreadSheetData.rowPointer.put(compoundId, rowCount)

        // add values for the cid column
        molSpreadSheetData.mssData.put("${rowCount}_0".toString(), new MolSpreadSheetCell(compoundName,compoundSmiles,MolSpreadSheetCellType.image))
        molSpreadSheetData.mssData.put("${rowCount++}_1".toString(), new MolSpreadSheetCell(compoundId.toString(),MolSpreadSheetCellType.identifier))

        molSpreadSheetData

    }


//
//    protected  MolSpreadSheetData populateMolSpreadSheetColumnMetadata(MolSpreadSheetData molSpreadSheetData,List<CartAssay> cartAssayList) {
//        // get the header names from the assays
//        molSpreadSheetData.mssHeaders.add("Struct")
//        molSpreadSheetData.mssHeaders.add("CID")
//        for (CartAssay cartAssay in cartAssayList){
//            molSpreadSheetData.mssHeaders.add(cartAssay.assayTitle)
//        }
//
//        molSpreadSheetData
//    }


    protected  MolSpreadSheetData populateMolSpreadSheetColumnMetadata(MolSpreadSheetData molSpreadSheetData,List<Experiment> experimentList ) {
        // get the header names from the assays
        molSpreadSheetData.mssHeaders.add("Struct")
        molSpreadSheetData.mssHeaders.add("CID")
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
        new ArrayList<Long> ()
    }


    /**
     * Turn projects into assays
     *
     * @param cartProjects
     * @return
     */
     protected List<Assay> cartProjectsToAssays( List<CartProject> cartProjects) {

        List<Long> projectIds = []
        for (CartProject cartProject  : cartProjects) {
            long ProjectId = cartProject.projectId
            projectIds.add(ProjectId)
        }

        final List<Project> projectList = queryServiceWrapper.getRestProjectService().get(projectIds)
         List<Assay> returnAssayList = new ArrayList<Assay>()
        for (Project project in projectList)  {
            def assayList =  project.assays()
            if (assayList.hasNext())  {
                Iterator<Assay> assayIterator = assayList.iterator()
                while (assayIterator.hasNext()) {
                    Assay assay = assayIterator.next()
                    returnAssayList.add(assay )
               }
            }
        }
        returnAssayList
    }

    /**
     * Convert Assay ODs to expt ids
     * @param cartAssays
     * @return
     */
    protected List<Experiment> assaysToExperiments(final Collection<Assay> assays) {
        List<Experiment> allExperiments = []
        if (!assays.isEmpty())  {
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
        if (incomingExperimentList ==null)
            allExperiments = []
        else
            allExperiments =  incomingExperimentList
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
    List<SpreadSheetActivity> findActivitiesForCompounds(MolSpreadSheetData molSpreadSheetData,final Experiment experiment, final Object compoundETag) {
        final List<SpreadSheetActivity> spreadSheetActivities = new ArrayList<SpreadSheetActivity>()
        final ServiceIterator<Value> experimentIterator = this.queryServiceWrapper.restExperimentService.activities(experiment, compoundETag);
        int rowPointer = 0
        while (experimentIterator.hasNext()) {
            Value experimentValue = experimentIterator.next()
            if (experimentValue) {
                SpreadSheetActivity spreadSheetActivity = extractActivitiesFromExperiment(experimentValue)
                updateMolSpreadSheetDataToReferenceCompound(  molSpreadSheetData,rowPointer,spreadSheetActivity.cid as Long,spreadSheetActivity.cid,spreadSheetActivity.cid)
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


    public double interpretHillCurveValue(){
        double retValue = 0d
        if (this.hillCurveValue!=null) {
            if (this.hillCurveValue.getS0()==null)
                retValue = Double.NaN
            else
                retValue = this.hillCurveValue.getS0()
        }
        retValue
    }


}
