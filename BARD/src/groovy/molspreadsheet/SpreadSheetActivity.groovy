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
    static String DEFAULT_DATATYPE = "AC50"

    public void activityToSpreadSheetActivity(final Activity activity, final List <MolSpreadSheetColSubHeader> resultTypeNames) {    //List <MolSpreadSheetColSubHeader>
        this.cid = activity.cid
        this.eid = activity.bardExptId
        this.sid = activity.sid
        this.addPotency(activity)
        this.addOutCome(activity)
        ResultData resultData = activity.resultData
        readOutsToHillCurveValues(resultData, resultTypeNames)
    }



    public void nonPriorityElementToSpreadSheetActivity(final Activity activity, final List <MolSpreadSheetColSubHeader> resultTypeNames){
        this.cid = activity.cid
        this.eid = activity.bardExptId
        this.sid = activity.sid
        this.addPotency(activity)
        this.addOutCome(activity)
        ResultData resultData = activity.resultData
        if (!resultTypeNames*.columnTitle.contains(DEFAULT_DATATYPE)) {
            resultTypeNames << new MolSpreadSheetColSubHeader( columnTitle:  DEFAULT_DATATYPE)
        }
        ArrayList<ActivityConcentration>  activityConcentrationArrayList = []
        activityConcentrationArrayList.add(new ActivityConcentration(value:this.potency, pubChemDisplayName:  DEFAULT_DATATYPE, qualifier: "", dictElemId: 959))
//        this.priorityElementList << new PriorityElement(primaryElements: activityConcentrationArrayList)
    }

    void addPotency(final Activity activity) {
        // convert a "null" potency to a "NaN"
        if (activity.potency==null) {
            this.potency = Double.NaN
        }  else {
            this.potency = new Double(activity.potency)
        }
    }

    void addOutCome(final Activity activity) {
        this.activityOutcome =  ActivityOutcome.UNSPECIFIED // provide a default
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
//        String columnHeaderName = priorityElement.displayName ?: priorityElement.concentrationResponseSeries?.responseUnit ?: ""
        String columnHeaderName = priorityElement.getDictionaryLabel() ?: priorityElement.concentrationResponseSeries?.getDictionaryLabel() ?: ""
        if (!resultTypeNames*.columnTitle.contains(columnHeaderName)) {
            resultTypeNames << new MolSpreadSheetColSubHeader( columnTitle:  columnHeaderName)
        }
        //TODO: Read from CAP
//        String columnHeaderName = priorityElement.getDictionaryLabel() ?: priorityElement.concentrationResponseSeries?.getDictionaryLabel() ?: ""
//        if (!resultTypeNames.contains(columnHeaderName)) {
//            resultTypeNames.add(columnHeaderName)
//        }
    }

}
