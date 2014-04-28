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

import bard.db.registration.AssayContextItem

import bard.dm.assaycompare.assaycleanup.LimitedAssayContext
import bard.dm.assaycompare.ContextItemComparisonResultEnum
import bard.dm.assaycompare.CompareUsingMatches
import bard.dm.assaycompare.AssayContextItemMatchTripleBuilder
import bard.dm.assaycompare.ComparisonResult

/**
 * Created with IntelliJ IDEA.
 * User: dlahr
 * Date: 12/27/12
 * Time: 7:00 PM
 * To change this template use File | Settings | File Templates.
 */
class LimitedAssayContextCompare {
    private CompareUsingMatches<AssayContextItem, ContextItemComparisonResultEnum> compareUsingMatches

    public LimitedAssayContextCompare() {
        compareUsingMatches = new CompareUsingMatches<AssayContextItem, ContextItemComparisonResultEnum>(new AssayContextItemMatchTripleBuilder())
    }

    /**
     * compare assay contexts based on their assay context items to see if they match
     * @param ac1
     * @param ac2
     * @return null if one of the assay contexts has no items, otherwise comparison result
     */
    ComparisonResult<ContextItemComparisonResultEnum> compareContext(LimitedAssayContext ac1, LimitedAssayContext ac2) {
        return compareUsingMatches.compare(ac1.itemSet, ac2.itemSet)
    }
}
