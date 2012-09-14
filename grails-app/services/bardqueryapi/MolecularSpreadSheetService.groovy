package bardqueryapi

import bard.core.Experiment
import bard.core.HillCurveValue
import bard.core.ServiceIterator
import bard.core.Value

class MolecularSpreadSheetService {

    QueryServiceWrapper queryServiceWrapper
    /**
     *
     * @param experimentId
     * @param compoundETags - Just wish these etags were typed
     * @return List of activities
     */
    public List<SpreadSheetActivity> getMolecularSpreadSheet(List<Long> cids) {

//        final List<SpreadSheetActivity> spreadSheetActivities = new ArrayList<SpreadSheetActivity>()
//        ServiceIterator<Value> experimentIterator = this.queryServiceWrapper.restExperimentService.activities(experiment, compoundETag);
//        while (experimentIterator.hasNext()) {
//            Value experimentValue = experimentIterator.next()
//            if (experimentValue) {
//                SpreadSheetActivity spreadSheetActivity = extractActivitiesFromExperiment(experimentValue)
//                spreadSheetActivities.add(spreadSheetActivity)
//            }
//        }
        return []
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
