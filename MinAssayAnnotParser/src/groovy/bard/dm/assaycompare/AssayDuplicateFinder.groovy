package bard.dm.assaycompare

import bard.db.registration.AssayContextItem
import bard.dm.Log
import bard.db.registration.AssayContext
import bard.db.registration.Assay

/**
 * Created with IntelliJ IDEA.
 * User: dlahr
 * Date: 9/25/12
 * Time: 6:40 PM
 * To change this template use File | Settings | File Templates.
 */
class AssayDuplicateFinder {

    void findDuplicates(List<Assay> assayList) {
        final Date startDate = new Date()
        Log.logger.info("Start assay de-duplication ${startDate}")

        final AssayContextItemCompare itemCompare = new AssayContextItemCompare()
        final AssayContextCompare contextCompare = new AssayContextCompare()
        final AssayCompare assayCompare = new AssayCompare()

        try {
            Set<Assay> exactMatches = new HashSet<Assay>()

            Map<Assay, AssayMatch> assayAssayMatchMap = new HashMap<Assay, AssayMatch>()

            for (int assayIndex = 0; assayIndex < assayList.size(); assayIndex++) {
                Log.logger.debug("assayIndex ${assayIndex}")
                Assay assay1 = assayList.get(assayIndex)
                Log.logger.debug("assay1 ${assay1.id}")

                if (! exactMatches.contains(assay1)) {

                    AssayMatch assay1Match = assayAssayMatchMap.get(assay1)
                    if (null == assay1Match) {
                        assay1Match = new AssayMatch(assay1)
                        assayAssayMatchMap.put(assay1, assay1Match)
                    }

                    checkForDuplicateContexts(new ArrayList<AssayContext>(assay1.assayContexts), contextCompare, assay1.id, itemCompare)

                    for (int j = assayIndex + 1; j < assayList.size(); j++) {
                        Log.logger.debug("\tj ${j}")
                        Assay assay2 = assayList.get(j)
                        Log.logger.debug("\tassay2 ${assay2.id}")

                        if (!exactMatches.contains(assay2)) {
                            ComparisonResult<ComparisonResultEnum> result = assayCompare.compareAssays(assay1, assay2)

                            if (result != null) {
                                String matchString = null
                                if (result.resultEnum.equals(ComparisonResultEnum.ExactMatch)) {
                                    matchString = "exact match"
                                    assay1Match.exactMatches.add(assay2)
                                    exactMatches.add(assay2)
                                } else if (result.resultEnum.equals(ComparisonResultEnum.SubsetMatch)) {
                                    matchString = "subset match"
                                    if (assay1.assayContexts.size() > assay2.assayContexts.size()) {
                                        assay1Match.subsetOfThis.add(assay2)
                                    }
                                } else if (result.resultEnum.equals(ComparisonResultEnum.PartialMatch)) {
                                    matchString = "partial match"
                                }

                                if (matchString != null) {
                                    Log.logger.debug("assays \t ${assay1.id} \t ${assay2.id} \t ${matchString} \t match item count \t ${result.matchedItemCount}")
                                }
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
        } catch (Exception e) {
            e.printStackTrace()
        } finally {
            final Date endDate = new Date()
            final double runTime = (endDate.getTime() - startDate.getTime()) / 60000.0
            Log.logger.info("run time[min]: ${runTime}")
            Log.logger.info("finished ${endDate}")
        }
    }



    private boolean itemExactOrEpsMatch(ContextItemComparisonResultEnum resultEnum) {
        return (resultEnum.equals(ContextItemComparisonResultEnum.ExactMatch) || resultEnum.equals(ContextItemComparisonResultEnum.EpsMatch))
    }


    /**
     * check if there are any duplicate assay context items
     * @param assayContextItemList
     */
    private void checkForDuplicateContextItems(List<AssayContextItem> assayContextItemList, AssayContextItemCompare itemCompare, long assayId, long assayContextId) {
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

    private void checkForDuplicateContexts(List<AssayContext> assayContextList, AssayContextCompare contextCompare,
                                           long assayId, AssayContextItemCompare itemCompare) {

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
}
