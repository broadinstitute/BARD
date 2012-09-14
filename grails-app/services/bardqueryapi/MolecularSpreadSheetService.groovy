package bardqueryapi

import bard.core.rest.RESTCompoundService
import org.apache.commons.lang3.time.StopWatch
import bard.core.*

class MolecularSpreadSheetService {

    QueryServiceWrapper queryServiceWrapper
    QueryHelperService queryHelperService


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

    protected List<Experiment> cartProjectsToExperiments(final List<CartProject> cartProjects) {
        List<Long> projectIds = []
        for (CartProject cartProject : cartProjects) {
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
     * @param experimentId
     * @param compoundETags - Just wish these etags were typed
     * @return List of activities
     */
    public List<SpreadSheetActivity> getMolecularSpreadSheet(List<CartCompound> cartCompounds, List<CartAssay> cartAssays, List<CartProject> cartProjects) {

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
    public List<SpreadSheetActivity> findActivitiesForCompounds(final Experiment experiment, final Object compoundETag) {
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

    SpreadSheetActivity extractActivitiesFromExperiment(Value experimentValue) {
        final Iterator<Value> experimentValueIterator = experimentValue.children()
        SpreadSheetActivity spreadSheetActivity = new SpreadSheetActivity()
        while (experimentValueIterator.hasNext()) {
            Value childValue = experimentValueIterator.next()
            addCurrentActivityToSpreadSheet(spreadSheetActivity, childValue)
        }
        return spreadSheetActivity
    }

    void addCurrentActivityToSpreadSheet(SpreadSheetActivity spreadSheetActivity, Value childValue) {
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
