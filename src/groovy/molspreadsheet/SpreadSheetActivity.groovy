package molspreadsheet

import bard.core.HillCurveValue
import bardqueryapi.ActivityOutcome

/**
 *
 */
class SpreadSheetActivity {
    Long experimentId
    Long eid  = 0L
    Long cid  = 0L
    Long sid  = 0L
    Double potency
    ActivityOutcome activityOutcome  = ActivityOutcome.UNSPECIFIED
    List<HillCurveValue> hillCurveValueList = []


}
