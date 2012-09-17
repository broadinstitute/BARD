package bardqueryapi

import bard.core.rest.RESTCompoundService
import com.metasieve.shoppingcart.ShoppingCartService
import org.apache.commons.lang3.time.StopWatch
import bard.core.*

class MolecularSpreadSheetService {

    QueryCartService queryCartService
    QueryServiceWrapper queryServiceWrapper
    ShoppingCartService shoppingCartService
    QueryHelperService queryHelperService



    MolSpreadSheetData retrieveExperimentalData() {
        MolSpreadSheetData molSpreadSheetData

        if (queryCartService.totalNumberOfUniqueItemsInCart(shoppingCartService) > 0) {

            List<CartCompound> cartCompoundList = retrieveCartCompoundFromShoppingCart()
            List<CartAssay> cartAssayList = retrieveCartAssayFromShoppingCart()
            List<CartProject> cartProjectList = retrieveCartProjectFromShoppingCart()

            molSpreadSheetData =  populateMolSpreadSheetMetadata(cartCompoundList,cartAssayList,cartProjectList)
            Object etag =  generateETagFromCartCompounds( cartCompoundList )
            List<Experiment> experimentList = cartAssaysToExperiments(cartAssayList)

            populateMolSpreadSheetData( molSpreadSheetData,  experimentList,  etag)

        }  else
            molSpreadSheetData  = new MolSpreadSheetData()

        molSpreadSheetData
    }




    protected void populateMolSpreadSheetData(MolSpreadSheetData molSpreadSheetData, List<Experiment> experimentList, Object etag) {
        // now step through the data and place into molSpreadSheetData
        List<SpreadSheetActivity> spreadSheetActivityList = new ArrayList<SpreadSheetActivity>()
        int columnPointer = 0
        // we need to handle each experiment separately ( until NCGC can do this in the background )
        // Note that each experiment corresponds to a column in our spreadsheet
        for (Experiment experiment in experimentList) {

            ServiceIterator<Value> experimentIterator = queryServiceWrapper.restExperimentService.activities(experiment, etag)

            // Now step through the result set and pull back  one value for each compound
            Value experimentValue
            while (experimentIterator.hasNext()) {
                experimentValue = experimentIterator.next()
                spreadSheetActivityList.add(extractActivitiesFromExperiment(experimentValue))
            }
            //SpreadSheetActivity spreadSheetActivity2 = extractActivitiesFromExperiment(experimentValue)

            for (SpreadSheetActivity spreadSheetActivity in spreadSheetActivityList) {
                if (molSpreadSheetData.rowPointer.containsKey(spreadSheetActivity.cid)) {
                    int innerRowPointer = molSpreadSheetData.rowPointer[spreadSheetActivity.cid]
                    String arrayKey = "${innerRowPointer}_${columnPointer+2}"
                    MolSpreadSheetCell molSpreadSheetCell = new MolSpreadSheetCell(spreadSheetActivity.interpretHillCurveValue().toString(), MolSpreadSheetCellType.numeric)
                    molSpreadSheetData.mssData.put(arrayKey, molSpreadSheetCell)
                }
                else
                    assert false, "did not expect cid = ${spreadSheetActivity.cid}"

            }
            columnPointer++
        }
        molSpreadSheetData
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


    protected  MolSpreadSheetData populateMolSpreadSheetMetadata(List<CartCompound> cartCompoundList, List<CartAssay> cartAssayList,List<CartProject> cartProjectList) {
        // we want to fill this variable
        MolSpreadSheetData molSpreadSheetData = new MolSpreadSheetData()

        // need to be able to map from CID to row location
        int rowPointer = 0
        for (CartCompound cartCompound in cartCompoundList){
            molSpreadSheetData.rowPointer.put(cartCompound.compoundId as Long, rowPointer++)
        }

        // get the header names from the assays
        molSpreadSheetData.mssHeaders.add("Struct")
        molSpreadSheetData.mssHeaders.add("CID")
        for (CartAssay cartAssay in cartAssayList){
            molSpreadSheetData.mssHeaders.add(cartAssay.assayTitle)
        }

        // add values for the cid column
        int rowCount = 0
        for (CartCompound cartCompound in cartCompoundList){
            molSpreadSheetData.mssData.put("${rowCount}_0".toString(), new MolSpreadSheetCell(cartCompound.name,cartCompound.smiles,MolSpreadSheetCellType.image))
            molSpreadSheetData.mssData.put("${rowCount++}_1".toString(), new MolSpreadSheetCell("${cartCompound.compoundId}".toString(),MolSpreadSheetCellType.identifier))
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

    // Will do this the right way eventually, but for now we are forced to assume that assayids == experimentids
    protected List<Experiment> cartAssaysToExperiments(final List<CartAssay> cartAssayList) {
        List<Experiment> experimentList = new ArrayList<Experiment>()
        for (CartAssay cartAssay in cartAssayList) {
            Long experimentID = cartAssay.assayId // for now make this assumption, that assayid=exptid
            experimentList.add(queryServiceWrapper.restExperimentService.get(experimentID))
        }
        experimentList
    }

//    protected List<Experiment> cartAssaysToExperiments(final List<CartAssay> cartAssays) {
//        List<Long> assayIds = []
//        for (CartAssay cartAssay : cartAssays) {
//            long assayId = cartAssay.assayId
//            assayIds.add(assayId)
//        }
//        List<Experiment> allExperiments = []
//        Collection<Assay> assays = queryServiceWrapper.getRestAssayService().get(assayIds)
//        for (Assay assay : assays) {
//            Collection<Experiment> experiments = assay.getExperiments()
//            allExperiments.addAll(experiments)
//        }
//        return allExperiments
//    }
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
    public double interpretHillCurveValue(){
        double retValue = 0d
        if (this.hillCurveValue!=null) {
           if (this.hillCurveValue.getValue()!=null)
               retValue = this.hillCurveValue.getValue()
            else if (this.hillCurveValue.getConc()!=null){
               if (this.hillCurveValue.getConc().size()>1) {
                   int midpoint =  this.hillCurveValue.getConc().size()/2
                   retValue = this.hillCurveValue.getConc()[midpoint]
               }
           }
        }
        retValue
    }
}
