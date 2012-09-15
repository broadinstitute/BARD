package bardqueryapi

import bard.core.Experiment
import bard.core.HillCurveValue
import bard.core.ServiceIterator
import bard.core.Value
import bard.core.Compound
import com.metasieve.shoppingcart.ShoppingCartService
import bard.core.rest.RESTExperimentService
import bard.core.rest.RESTCompoundService

class MolecularSpreadSheetService {

    static MolSpreadSheetData molSpreadSheetData
    QueryCartService  queryCartService
    QueryServiceWrapper queryServiceWrapper
    ShoppingCartService  shoppingCartService
    RESTExperimentService restExperimentService
    RESTCompoundService restCompoundService



    static {
        molSpreadSheetData = new MolSpreadSheetData()
        molSpreadSheetData.mssHeaders = ["Chemical Structure",
                "CID",
                "DNA polymerase (Q9Y253) ADID : 1 IC50",
                "Serine-protein kinase (Q13315) ADID : 1 IC50",
                "Tyrosine-DNA phosphodiesterase 1 (Q9NUW8) ADID: 514789"]
        molSpreadSheetData.mssData.put("0_0",new MolSpreadSheetCell("1",MolSpreadSheetCellType.string))
        molSpreadSheetData.mssData.put("0_1",new MolSpreadSheetCell("3888711",MolSpreadSheetCellType.identifier))
        molSpreadSheetData.mssData.put("0_2",new MolSpreadSheetCell("3888711",MolSpreadSheetCellType.greaterThanNumeric))
        molSpreadSheetData.mssData.put("0_3",new MolSpreadSheetCell("3888711",MolSpreadSheetCellType.percentageNumeric))
        molSpreadSheetData.mssData.put("0_4",new MolSpreadSheetCell("3888711",MolSpreadSheetCellType.lessThanNumeric))
        molSpreadSheetData.mssData.put("1_0",new MolSpreadSheetCell("1",MolSpreadSheetCellType.string))
        molSpreadSheetData.mssData.put("1_1",new MolSpreadSheetCell("3888711",MolSpreadSheetCellType.identifier))
        molSpreadSheetData.mssData.put("1_2",new MolSpreadSheetCell("3888711",MolSpreadSheetCellType.greaterThanNumeric))
        molSpreadSheetData.mssData.put("1_3",new MolSpreadSheetCell("3888711",MolSpreadSheetCellType.percentageNumeric))
        molSpreadSheetData.mssData.put("1_4",new MolSpreadSheetCell("3888711",MolSpreadSheetCellType.lessThanNumeric))
    }



    MolSpreadSheetData fakeMe(){
        molSpreadSheetData
    }


    MolSpreadSheetData retrieveExperimentalData(){
        if (queryCartService.totalNumberOfUniqueItemsInCart(shoppingCartService)> 0){
            // start by getting a list of cmpds
            List<Integer>  cartCompoundIdList  = new  ArrayList<Integer>()
            int rowPointer = 0
            for (CartCompound cartCompound in (queryCartService.groupUniqueContentsByType(shoppingCartService)[(QueryCartService.cartCompound)]) ){
                cartCompoundIdList.add(cartCompound.compoundId)
                molSpreadSheetData.rowPointer.put(cartCompound.compoundId,rowPointer )
                rowPointer++
            }
            Object etag =  queryServiceWrapper.restCompoundService.newETag("My awesome compound collection", cartCompoundIdList);
            // now get a list of expts
            List<CartAssay>  cartAssayList  = new  ArrayList<CartAssay>()

            for (CartAssay cartAssay in (queryCartService.groupUniqueContentsByType(shoppingCartService)[(QueryCartService.cartAssay)]) ){
                cartAssayList.add(cartAssay)
            }
            // get the assays
            molSpreadSheetData = new MolSpreadSheetData()
            List<SpreadSheetActivity> spreadSheetActivityList
            int columnPointer = 0
            for (CartAssay cartAssay in cartAssayList) {
                Experiment experiment = queryServiceWrapper.restExperimentService.get(cartAssay.assayId)
                spreadSheetActivityList = findActivitiesForCompounds( experiment,etag)
                molSpreadSheetData.mssHeaders.add(cartAssay.assayTitle)
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
