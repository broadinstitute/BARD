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

import org.springframework.transaction.support.DefaultTransactionStatus
import org.apache.commons.lang.time.StopWatch
import depositor.neighbor.Relation

/**
 * Parses and persists the parent/child AID relationships that the PubChem depositors have been uploaded.
 * Doesn't guarantee uniqueness.
 */
Integer i = 0
StopWatch globalTime = new StopWatch()
globalTime.start()
Date startTime= new Date()
Date endTime

Relation.withTransaction {DefaultTransactionStatus status ->
    File infile = new File("C:/BARD_DATA_MIGRATION/SwamidassProjectFlow/pcassay_pcassay_depositorneighbor.txt")
    infile.eachLine { String line ->

        String[] values = line.trim().split()
        String parent = values[0]
        Long parentId = new Long(parent - 'AID')
        String child = values[1]
        Long childId = new Long(child - 'AID')

        Relation relation = new Relation(parent: parent, parentId: parentId, child: child, childId: childId)
//        println("${i}: ${relation.dump()}")
        relation.save(flush: false)

        if (i++ % 10000 == 0) {
            endTime = new Date()
            println("${i} of rows persisted so far (${(endTime.time - startTime.time)/1000} sec; total time so far: ${globalTime.time/1000} sec)")
            startTime = endTime
            status.flush()
        }
    }
}
