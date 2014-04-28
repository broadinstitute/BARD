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

import bard.db.registration.Assay

import bard.dm.assaycompare.assaycleanup.LimitedAssayContext

/**
 * Created with IntelliJ IDEA.
 * User: dlahr
 * Date: 9/25/12
 * Time: 3:28 PM
 * To change this template use File | Settings | File Templates.
 */
class AssayMatch {
    Assay assay

//    Set<Assay> exactMatches

//    Set<Assay> subsetOfThis

//    Map<Assay, Integer> partialMatchCountMap

    List<LimitedAssayContext> limitedAssayContextList

    Map<LimitedAssayContext, LimitedAssayContext> duplicateOriginalContextMap

    public AssayMatch(Assay assay) {
        this.assay = assay

//        exactMatches = new HashSet<Assay>()
//        subsetOfThis = new HashSet<Assay>()
//        partialMatchCountMap = new HashMap<Assay, Integer>()

        limitedAssayContextList = new LinkedList<LimitedAssayContext>()

        duplicateOriginalContextMap = new HashMap<LimitedAssayContext, LimitedAssayContext>()
    }
}
