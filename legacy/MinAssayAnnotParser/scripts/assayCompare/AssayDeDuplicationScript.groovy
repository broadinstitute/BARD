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

import bard.db.registration.Assay
import bard.dm.Log
import org.apache.log4j.Level
import bard.dm.assaycompare.AssayMatch
import bard.dm.assaycompare.assaycleanup.DuplicateWriter
import bard.dm.assaycompare.assaycleanup.AssayContextAndItemDuplicateFinder
import bard.db.registration.AssayContext
import bard.dm.assaycompare.assaycleanup.AssayCleaner
import bard.dm.assaycompare.AssayDuplicateFinder

Log.initializeLogger("test/exampleData/logsAndOutput/assayDeDuplication.log")
Log.logger.setLevel(Level.DEBUG)

//load assays from database
Log.logger.info("begin loading assays")
final Date startLoadDate = new Date()
List<Assay> assayList = Assay.findAll()

final Date endLoadDate = new Date()
final loadTime = (endLoadDate.time - startLoadDate.time) / 60000.0
Log.logger.info("loaded ${assayList.size()} assays. load time[min]: ${loadTime} finished load: ${endLoadDate}")


Log.logger.info("Clean up duplicate assay context items with assay contexts, and assay contexts within assays")

DuplicateWriter duplicateWriter = new DuplicateWriter("test/exampleData/logsAndOutput")
AssayContextAndItemDuplicateFinder assayContextAndItemDuplicateFinder = new AssayContextAndItemDuplicateFinder()
AssayCleaner assayCleaner = new AssayCleaner()

List<AssayMatch> assayMatchList = new ArrayList<AssayMatch>(assayList.size())

AssayContext.withTransaction {status ->
    int count = 0
    for (Assay assay : assayList) {
        AssayMatch assayMatch = assayContextAndItemDuplicateFinder.removeDuplicateContextsAndItems(assay)
        assayMatchList.add(assayMatch)

        duplicateWriter.write(assayMatch)

        assayCleaner.clean(assayMatch)

        count++
        Log.logger.info("assay " + assay.getId() + " cleanup count: " + count + "  duplicates: " + assayMatch.duplicateOriginalContextMap.keySet().size()
                        + " contexts ")
    }
    duplicateWriter.closeAll()

    //uncomment line below to cause all changes to be rolled back
//    status.setRollbackOnly()
}

//run de-duplication
//AssayDuplicateFinder assayDuplicateFinder = new AssayDuplicateFinder("test/exampleData/assay_duplication_stats.csv")
//assayDuplicateFinder.findDuplicates(assayMatchList)

Log.close()

