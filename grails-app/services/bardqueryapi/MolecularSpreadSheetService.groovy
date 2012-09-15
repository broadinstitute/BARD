package bardqueryapi

import bard.core.rest.RESTCompoundService
import org.apache.commons.lang3.time.StopWatch
import bard.core.*
import bard.core.rest.RESTExperimentService
import com.metasieve.shoppingcart.ShoppingCartService


class MolecularSpreadSheetService {

    static MolSpreadSheetData molSpreadSheetData
    QueryCartService  queryCartService
    QueryServiceWrapper queryServiceWrapper
    ShoppingCartService  shoppingCartService
    RESTExperimentService restExperimentService
    RESTCompoundService restCompoundService
    QueryHelperService queryHelperService



    MolSpreadSheetData retrieveExperimentalData(){
        if (queryCartService.totalNumberOfUniqueItemsInCart(shoppingCartService)> 0){

            // we want to fill this variable
            molSpreadSheetData = new MolSpreadSheetData()

            // start by working through the compounds. First gather CartCompound
            List<Integer>  cartCompoundList  = new  ArrayList<CartCompound>()
            int rowPointer = 0
            for (CartCompound cartCompound in (queryCartService.groupUniqueContentsByType(shoppingCartService)[(QueryCartService.cartCompound)]) ){
                cartCompoundList.add(cartCompound)
                molSpreadSheetData.rowPointer.put(cartCompound.compoundId,rowPointer )
                rowPointer++
            }
            // extract cmpd ids
            List<Integer>  cartCompoundIdList = new ArrayList<Integer>()
            for (CartCompound cartCompound in  cartCompoundList)
                cartCompoundIdList.add (cartCompound.compoundId)
            // build the etag
            Object etag =  queryServiceWrapper.restCompoundService.newETag("My awesome compound collection", cartCompoundIdList);

            // now get a list of expts.  Start with the assays, then conert to experiments with Jacob's method
            List<CartAssay>  cartAssayList  = new  ArrayList<CartAssay>()
            for (CartAssay cartAssay in (queryCartService.groupUniqueContentsByType(shoppingCartService)[(QueryCartService.cartAssay)]) ){
                cartAssayList.add(cartAssay)
                molSpreadSheetData.mssHeaders.add(cartAssay.assayTitle)
            }
            List<Experiment> experimentList = cartAssaysToExperiments(cartAssayList)

            // now step through the data and place into molSpreadSheetData
            List<SpreadSheetActivity> spreadSheetActivityList
            int columnPointer = 0
            for (Experiment experiment in  experimentList) {
                spreadSheetActivityList = findActivitiesForCompounds( experiment,etag)
                // how to aggregate activity values -- for now do something (very) simple
                for ( SpreadSheetActivity spreadSheetActivity in  spreadSheetActivityList ) {
                    if (molSpreadSheetData.rowPointer.containsKey(spreadSheetActivity.cid))  {
                        int innerRowPointer =  molSpreadSheetData.rowPointer[spreadSheetActivity.cid]
                        String arrayKey = "${innerRowPointer}_${columnPointer}"
                        molSpreadSheetData.mssData.put(arrayKey,new MolSpreadSheetCell(spreadSheetActivity.hillCurveValue,MolSpreadSheetCellType.numeric))
                    }
                    else
                        assert false, "did not expect cid = ${spreadSheetActivity.cid}"

                }
                columnPointer++
            }

        }
        molSpreadSheetData
    }






    /**
     *
     * @param cartCompounds
     * @return  list of Experiment's from a list of CartCompound's
     */
    protected List<Long> cartCompoundsToCIDS(final List<CartCompound> cartCompounds) {
        List<Long> cids = []
        for (CartCompound cartCompound : cartCompounds) {
            long cid = cartCompound.compoundId
            cids.add(cid)
        }

        return cids
    }



    protected List<Experiment> cartAssaysToExperiments(final List<CartAssay> cartAssays) {
        List<Long> assayIds = []
        for (CartAssay cartAssay : cartAssays) {
            //TODO: Use the assay Id once it is ready
            long assayId = cartAssay.getId()
            assayIds.add(assayId)
        }
        List<Experiment> allExperiments = []
        Collection<Assay> assays = queryServiceWrapper.getRestAssayService().get(assayIds)
        for (Assay assay : assays) {
            Collection<Experiment> experiments = assay.getExperiments()
            allExperiments.addAll(experiments)
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
            //TODO: Use the project Id once it is ready
            long projectId = cartProject.getId()
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

        if(!cartCompounds){
            throw new RuntimeException("There must be at least one Compound in the cart")
        }
        if(!cartAssays && !cartProjects){
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
        ServiceIterator<Value> experimentIterator = this.queryServiceWrapper.restExperimentService.activities(experiment, compoundETag);
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
    void addCurrentActivityToSpreadSheet(final SpreadSheetActivity spreadSheetActivity,final Value childValue) {
        String identifier = childValue.id
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
}
