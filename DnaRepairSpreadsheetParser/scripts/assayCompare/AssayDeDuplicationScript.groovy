import bard.db.registration.Assay
import org.springframework.transaction.support.DefaultTransactionStatus
import bard.db.registration.AssayContext
import bard.dm.assaycompare.AssayContextItemCompare
import bard.dm.assaycompare.AssayContextCompare
import bard.db.registration.AssayContextItem
import bard.dm.assaycompare.ContextItemComparisonResultEnum
import bard.dm.Log
import bard.dm.assaycompare.ComparisonResult
import bard.dm.assaycompare.ComparisonResultEnum
import bard.dm.assaycompare.AssayCompare

/**
 * Created with IntelliJ IDEA.
 * User: dlahr
 * Date: 9/24/12
 * Time: 12:12 AM
 * To change this template use File | Settings | File Templates.
 */

final Date startDate = new Date()
Log.logger.info("Start assay de-duplication ${startDate}")

final AssayContextItemCompare itemCompare = new AssayContextItemCompare()
final AssayContextCompare contextCompare = new AssayContextCompare()
final AssayCompare assayCompare = new AssayCompare()

try {
//Assay.withTransaction { DefaultTransactionStatus status ->
    List<Assay> assayList = Assay.findAll()
    Date loadDate = new Date()
    final loadTime = (loadDate.time - startDate.time) / 60000.0
    Log.logger.info("load time[min]: ${loadTime} finished load: ${loadDate}")

    Set<Assay> exactMatches = new HashSet<Assay>()

    Map<Assay, AssayMatch> assayAssayMatchMap = new HashMap<Assay, AssayMatch>()

    for (int assayIndex = 0; assayIndex < assayList.size(); assayIndex++) {
        Assay assay1 = assayList.get(assayIndex)

        if (! exactMatches.contains(assay1)) {

            AssayMatch assay1Match = assayAssayMatchMap.get(assay1)
            if (null == assay1Match) {
                assay1Match = new AssayMatch(assay1)
                assayAssayMatchMap.put(assay1, assay1Match)
            }

            checkForDuplicateContexts(new ArrayList<AssayContext>(assay1.assayContexts), contextCompare, assay1.id, itemCompare)

            for (int j = assayIndex + 1; j < assayList.size(); j++) {
                Assay assay2 = assayList.get(j)


                if (!exactMatches.contains(assay2)) {
                    ComparisonResult<ComparisonResultEnum> result = assayCompare.compareAssays(assay1, assay2)

                    if (result != null) {
//                        String matchString = null
                        if (result.resultEnum.equals(ComparisonResultEnum.ExactMatch)) {
//                            matchString = "exact match"
                            assay1Match.exactMatches.add(assay2)
                            exactMatches.add(assay2)
                        } else if (result.resultEnum.equals(ComparisonResultEnum.SubsetMatch)) {
//                            matchString = "subset match"
                            if (assay1.assayContexts.size() > assay2.assayContexts.size()) {
                                assay1Match.subsetOfThis.add(assay2)
                            }
                        } else if (result.resultEnum.equals(ComparisonResultEnum.PartialMatch)) {
//                            matchString = "partial match"
                        }

//                        if (matchString != null) {
//                            Log.logger.info("assays \t ${assay1.id} \t ${assay2.id} \t ${matchString} \t match item count \t ${result.matchedItemCount}")
//                        }
                    }

                }

                if (j%200 == 0) {
                    println("finished ${j}th inner assay")
                }
            }

            if ((assayIndex % 100) == 0) {
                println("finished ${assayIndex} assays")
            }

            Log.logger.info("\tassay ${assay1.id}")

            Log.logger.info("\t\texact matches:")
            for (Assay exactMatch : assay1Match.exactMatches) {
                Log.logger.info("\t\t\t${exactMatch.id}")
            }

            Log.logger.info("\t\tsubset of this assay:")
            for (Assay subsetOfThis : assay1Match.subsetOfThis) {
                Log.logger.info("\t\t\t${subsetOfThis.id}")
            }
        }
    }


//    status.setRollbackOnly()
//}
} catch (Exception e) {
    e.printStackTrace()
} finally {
    final Date endDate = new Date()
    final double runTime = (endDate.getTime() - startDate.getTime()) / 60000.0
    Log.logger.info("run time[min]: ${runTime}")
    Log.logger.info("finished ${endDate}")
}

return


boolean itemExactOrEpsMatch(ContextItemComparisonResultEnum resultEnum) {
    return (resultEnum.equals(ContextItemComparisonResultEnum.ExactMatch) || resultEnum.equals(ContextItemComparisonResultEnum.EpsMatch))
}


/**
 * check if there are any duplicate assay context items
 * @param assayContextItemList
 */
void checkForDuplicateContextItems(List<AssayContextItem> assayContextItemList, AssayContextItemCompare itemCompare, long assayId, long assayContextId) {
    for (int i = 0; i < assayContextItemList.size(); i++) {
        AssayContextItem aci1 = assayContextItemList.get(i)

        for (int j = i + 1; j < assayContextItemList.size(); j++) {
            AssayContextItem aci2 = assayContextItemList.get(j)

            if (itemExactOrEpsMatch(itemCompare.compareContextItems(aci1, aci2))) {
                Log.logger.info("assay ${assayId} assay context ${assayContextId} has duplicate context items: ${aci1.id} ${aci2.id}")
            }
        }
    }

}

void checkForDuplicateContexts(List<AssayContext> assayContextList, AssayContextCompare contextCompare, long assayId, AssayContextItemCompare itemCompare) {
    for (int i = 0; i < assayContextList.size(); i++) {
        AssayContext ac1 = assayContextList.get(i)
        checkForDuplicateContextItems(ac1.assayContextItems, itemCompare, assayId, ac1.id)

        for (int j = i+1; j < assayContextList.size(); j++) {
            AssayContext ac2 = assayContextList.get(j)
            ComparisonResult<ContextItemComparisonResultEnum> result = contextCompare.compareContext(ac1, ac2)
            if (result) {
                if (result.resultEnum.equals(ComparisonResultEnum.ExactMatch)) {
                    Log.logger.info("assay ${assayId} has exactly matching assay contexts: ${ac1.id} ${ac2.id}")
                } else if (result.resultEnum.equals(ComparisonResultEnum.SubsetMatch)) {
                    Log.logger.info("assay ${assayId} has subset match assay context: ${ac1.id} ${ac2.id}")
                } else if (result.resultEnum.equals(ComparisonResultEnum.PartialMatch)) {
//                    Log.logger.info("assay ${assayId} has partial match assay context: ${ac1.id} ${ac2.id}")
                }
            }
        }
    }
}

class AssayMatch {
    Assay assay

    Set<Assay> exactMatches

    Set<Assay> subsetOfThis


    public AssayMatch(Assay assay) {
        this.assay = assay

        exactMatches = new HashSet<Assay>()
        subsetOfThis = new HashSet<Assay>()
    }
}

