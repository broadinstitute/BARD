package molspreadsheet

import bard.core.HillCurveValue
import bard.core.rest.spring.experiment.Activity
import bard.core.rest.spring.experiment.ActivityConcentration
import bard.core.rest.spring.experiment.PriorityElement
import bard.core.rest.spring.experiment.ResultData
import bardqueryapi.ActivityOutcome

/**
 *
 */
class SpreadSheetActivity {
    Long experimentId
    Long eid = 0L
    Long cid = 0L
    Long sid = 0L
    Double potency
    ActivityOutcome activityOutcome = ActivityOutcome.UNSPECIFIED
    List<HillCurveValue> hillCurveValueList = []
    List<ActivityConcentration>  activityConcentrationList = []
    List<PriorityElement>  priorityElementList = []

    public void activityToSpreadSheetActivity(final Activity activity, final List <MolSpreadSheetColSubHeader> resultTypeNames) {    //List <MolSpreadSheetColSubHeader>
        this.cid = activity.cid
        this.eid = activity.eid
        this.sid = activity.sid
        this.addPotency(activity)
        this.addOutCome(activity)
        ResultData resultData = activity.resultData
        readOutsToHillCurveValues(resultData, resultTypeNames)
    }

    void addPotency(final Activity activity) {
        if (activity.potency) {
            this.potency = new Double(activity.potency)
        }
    }

    void addOutCome(final Activity activity) {
        if (activity.outcome != null) {
            this.activityOutcome = ActivityOutcome.findActivityOutcome(activity.outcome.intValue())
        }
    }

    void readOutsToHillCurveValues(final ResultData resultData, final List <MolSpreadSheetColSubHeader> resultTypeNames) {
        if (resultData){
            for (PriorityElement priorityElements in resultData.priorityElements){
                extractExperimentalValuesFromAPriorityElement(resultTypeNames, priorityElements)
            }
        }
    }

    /***
     * This is where we retrieve the curve
     */
    void extractExperimentalValuesFromAPriorityElement(final List <MolSpreadSheetColSubHeader> resultTypeNames, final PriorityElement priorityElement ) {
        this.priorityElementList << priorityElement
        String columnHeaderName = priorityElement.displayName ?: priorityElement.concentrationResponseSeries?.responseUnit ?: ""
        if (!resultTypeNames*.columnTitle.contains(columnHeaderName)) {
            resultTypeNames << new MolSpreadSheetColSubHeader( columnTitle:  columnHeaderName)
        }
    }




//    void readOutToHillCurveValue(final List<String> resultTypeNames, final PriorityElement priorityElement ) {
//        final HillCurveValue hillCurveValue = resultData.toHillCurveValue()
//        if (!resultTypeNames.contains(hillCurveValue.id)) {
//            resultTypeNames.add(hillCurveValue.id)
//        }
//        this.hillCurveValueList << hillCurveValue
//    }

}
