/* Copyright (c) 2014, The Broad Institute
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
    * Redistributions of source code must retain the above copyright
      notice, this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright
      notice, this list of conditions and the following disclaimer in the
      documentation and/or other materials provided with the distribution.
    * Neither the name of The Broad Institute nor the
      names of its contributors may be used to endorse or promote products
      derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL The Broad Institute BE LIABLE FOR ANY
DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

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
        activityConcentrationArrayList.add(new ActivityConcentration(value:this.potency, displayName:  DEFAULT_DATATYPE, qualifier: "", dictElemId: 959))
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
