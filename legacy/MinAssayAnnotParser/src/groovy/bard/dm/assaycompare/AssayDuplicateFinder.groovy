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
    private String outputFile

    public AssayDuplicateFinder(String outputFile) {
        this.outputFile = outputFile
    }


    void findDuplicates(List<AssayMatch> assayMatchList) {
        BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))
        writer.writeLine("orig_assay_id,orig_context_count,match_type,match_assay_id,match_context_count,partial_match_count")

        final Date startDate = new Date()
        Log.logger.info("Start assay de-duplication statistics calculation ${startDate}")

        final AssayCompare assayCompare = new AssayCompare()

        try {
            Set<AssayMatch> exactMatches = new HashSet<AssayMatch>()

            for (int assayIndex = 0; assayIndex < assayMatchList.size(); assayIndex++) {
                Log.logger.trace("assayIndex ${assayIndex}")
                AssayMatch assayMatch1 = assayMatchList.get(assayIndex)
                Log.logger.trace("assay1 ${assayMatch1.assay.id}")

                if (! exactMatches.contains(assayMatch1)) {
                    final String prefix = assayMatch1.assay.id + "," + assayMatch1.assay.assayContexts.size() + ","

                    for (int j = assayIndex + 1; j < assayMatchList.size(); j++) {
                        Log.logger.trace("\tj ${j}")
                        AssayMatch assayMatch2 = assayMatchList.get(j)
                        Log.logger.trace("\tassay2 ${assayMatch2.assay.id}")

                        if (!exactMatches.contains(assayMatch2)) {
                            ComparisonResult<ComparisonResultEnum> result =
                                assayCompare.compareAssays(assayMatch1.assay, assayMatch2.assay)

                            if (result != null) {
                                String matchString = null
                                if (result.resultEnum.equals(ComparisonResultEnum.ExactMatch)) {
                                    matchString = "exact match"
//                                    assayMatch1.exactMatches.add(assayMatch2.assay)
                                    exactMatches.add(assayMatch2)
                                    writer.writeLine(prefix + "exact," + assayMatch2.assay.id + "," + assayMatch2.assay.assayContexts.size())
                                } else if (result.resultEnum.equals(ComparisonResultEnum.SubsetMatch)) {
                                    matchString = "subset match"
                                    if (assayMatch1.assay.assayContexts.size() > assayMatch2.assay.assayContexts.size()) {
//                                        assayMatch1.subsetOfThis.add(assayMatch2.assay)
                                        writer.writeLine(prefix + "subset_of," + assayMatch2.assay.id + "," + assayMatch2.assay.assayContexts.size())
                                    }
                                } else if (result.resultEnum.equals(ComparisonResultEnum.PartialMatch)) {
                                    matchString = "partial match"

                                    final int numContexts1 = assayMatch1.assay.assayContexts.size()
                                    final int numContexts2 = assayMatch2.assay.assayContexts.size()
                                    final int smallerSize =  numContexts1 < numContexts2 ? numContexts1 : numContexts2

                                    boolean savePartialMatch = false
                                    if (smallerSize >= 10) {
                                        if (result.matchedItemCount >= 0.7*smallerSize) {
                                            savePartialMatch = true
                                        }
                                    } else if (smallerSize >= 5) {
                                        if (result.matchedItemCount >= smallerSize-2) {
                                            savePartialMatch = true
                                        }
                                    } else {
                                        if (result.matchedItemCount >= smallerSize-1) {
                                            savePartialMatch = true
                                        }
                                    }
//                                    assayMatch1.partialMatchCountMap.put(assayMatch2.assay, result.matchedItemCount)
                                    if (savePartialMatch) {
                                        writer.writeLine(prefix + "partial," + assayMatch2.assay.id + ","
                                                                                    + assayMatch2.assay.assayContexts.size() + "," + result.matchedItemCount)
                                    }
                                }

                                if (matchString != null) {
                                    Log.logger.trace("assays \t ${assayMatch1.assay.id} \t ${assayMatch2.assay.id} "
                                            + "\t ${matchString} \t match item count \t ${result.matchedItemCount}")
                                }
                            }

                        }

                        if (j%200 == 0) {
                            Log.logger.info("finished ${j}th inner assay at ${new Date()}")
                        }
                    }

                    if ((assayIndex % 100) == 0) {
                        Log.logger.info("finished ${assayIndex} assays")
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace()
        } finally {
            writer.close()

            final Date endDate = new Date()
            final double runTime = (endDate.getTime() - startDate.getTime()) / 60000.0
            Log.logger.info("run time[min]: ${runTime}")
            Log.logger.info("finished ${endDate}")
        }
    }
}
