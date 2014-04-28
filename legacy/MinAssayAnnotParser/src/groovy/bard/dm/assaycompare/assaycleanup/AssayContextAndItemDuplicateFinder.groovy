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

package bard.dm.assaycompare.assaycleanup

import bard.db.registration.Assay
import bard.db.registration.AssayContext
import bard.db.registration.AssayContextItem
import bard.dm.assaycompare.AssayMatch

import bard.dm.assaycompare.ContextItemComparisonResultEnum
import bard.dm.assaycompare.ComparisonResult
import bard.dm.assaycompare.ComparisonResultEnum
import bard.dm.assaycompare.AssayContextItemCompare
import bard.dm.Log

/**
 * Created with IntelliJ IDEA.
 * User: dlahr
 * Date: 12/27/12
 * Time: 6:09 PM
 * To change this template use File | Settings | File Templates.
 */
class AssayContextAndItemDuplicateFinder {
    public AssayMatch removeDuplicateContextsAndItems(Assay assay) {

        LimitedAssayContextCompare limitedAssayContextCompare = new LimitedAssayContextCompare()

        AssayMatch assayMatch = new AssayMatch(assay)

        //build a LimitedAssayContext for each AssayContext.  Each LimitedAssayContext has no
        // duplicate AssayContextItems
        AssayContextItemCompare itemCompare = new AssayContextItemCompare()
        buildLimitedAssayContexts(assayMatch, itemCompare)

        //Compare every LimitedAssayContext to every other LimitedAssayContext to identify duplicates
        for (int i = 0; i < assayMatch.limitedAssayContextList.size(); i++) {
            LimitedAssayContext context1 = assayMatch.limitedAssayContextList.get(i)

            //if it has not already been identified as duplicate, compare it to the remaining LimitedAssayContexts
            if (! assayMatch.duplicateOriginalContextMap.containsKey(context1)) {
                for (int j = i+1; j < assayMatch.limitedAssayContextList.size(); j++) {
                    LimitedAssayContext context2 = assayMatch.limitedAssayContextList.get(j)

                    //If it has not already been identified as a duplicate, compare it
                    if (! assayMatch.duplicateOriginalContextMap.containsKey(context2)) {
                        ComparisonResult<ContextItemComparisonResultEnum> comparisonResult =
                            limitedAssayContextCompare.compareContext(context1, context2)

                        if (comparisonResult && comparisonResult.resultEnum.equals(ComparisonResultEnum.ExactMatch)) {
                            assayMatch.duplicateOriginalContextMap.put(context2, context1)
                        }
                    }
                }
            }
        }

        assayMatch.limitedAssayContextList.removeAll(assayMatch.duplicateOriginalContextMap.keySet())

        return assayMatch
    }


    private static void buildLimitedAssayContexts(AssayMatch assayMatch, AssayContextItemCompare itemCompare) {
        //create a "limited" assay context for each "full" assay context
        //the limited assay context will not contain duplicate assay context items
        for (AssayContext context : assayMatch.assay.assayContexts) {
            if (null == context) {
                Log.logger.error("context is null - should be initialized no nulls within list - number must be missing in database: adid: {$assayMatch.assay.id}")
            } else {
                LimitedAssayContext limitedAssayContext = new LimitedAssayContext(assayContext: context)
                assayMatch.limitedAssayContextList.add(limitedAssayContext)

                for (int i = 0; i < context.assayContextItems.size(); i++) {
                    AssayContextItem item1 = context.assayContextItems.get(i)

                    //if item1 has not already been identified as a duplicate of another item, add it to the
                    // limited assasy context and compare it to the remaining items
                    if (! limitedAssayContext.duplicateOriginalItemMap.containsKey(item1)) {
                        limitedAssayContext.itemSet.add(item1)

                        for (int j = i+1; j < context.assayContextItems.size(); j++) {
                            AssayContextItem item2 = context.assayContextItems.get(j)

                            if (! limitedAssayContext.duplicateOriginalItemMap.containsKey(item2)) {
                                if (itemExactOrEpsMatch(itemCompare.compareContextItems(item1, item2))) {
                                    limitedAssayContext.duplicateOriginalItemMap.put(item2, item1)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private static boolean itemExactOrEpsMatch(ContextItemComparisonResultEnum resultEnum) {
        return (resultEnum.equals(ContextItemComparisonResultEnum.ExactMatch) || resultEnum.equals(ContextItemComparisonResultEnum.EpsMatch))
    }
}
