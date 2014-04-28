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

import bard.db.dictionary.Element
import bard.db.registration.Assay
import bard.dm.assaycompare.AssayMatch
import bard.db.registration.AssayContext
import bard.db.registration.AssayContextItem
import bard.db.registration.AttributeType

/**
 * Created with IntelliJ IDEA.
 * User: dlahr
 * Date: 12/27/12
 * Time: 7:16 PM
 * To change this template use File | Settings | File Templates.
 */
class TestAssayCleanup {
    private static final Element e1 = new Element(id: 1, label: "e1")
    private static final Element e2 = new Element(id: 2, label: "e2")
    private static final Element e3 = new Element(id: 3, label: "e3")

    void test() {
        testBuildLimited()

        Assay assay = new Assay()
        assay.assayContexts.add(buildAssayContext())
        assay.assayContexts.add(buildAssayContext())

        assert assay.assayContexts.size() == 2

        final AssayContextAndItemDuplicateFinder assayCleanup = new AssayContextAndItemDuplicateFinder()
        AssayMatch assayMatch = assayCleanup.removeDuplicateContextsAndItems(assay)
        assert assayMatch.limitedAssayContextList.size() == 1
        assert assayMatch.limitedAssayContextList.get(0).itemSet.size() == 1
        assert assayMatch.duplicateOriginalContextMap.keySet().size() == 1


        AssayContext context = buildAssayContext()
        context.assayContextItems.add(new AssayContextItem(attributeElement: e1, valueElement: e3,
                attributeType: AttributeType.Fixed))
        assay.assayContexts.add(context)

        assayMatch = assayCleanup.removeDuplicateContextsAndItems(assay)
        assert assayMatch.limitedAssayContextList.size() == 2
        assert assayMatch.limitedAssayContextList.get(0).itemSet.size() == 1
        assert assayMatch.limitedAssayContextList.get(1).itemSet.size() == 2
        assert assayMatch.duplicateOriginalContextMap.keySet().size() == 1
    }


    void testBuildLimited() {
        AssayContext context = buildAssayContext()

        Assay assay = new Assay()
        assay.assayContexts.add(context)
        AssayMatch assayMatch = new AssayMatch(assay)

        AssayContextAndItemDuplicateFinder.buildLimitedAssayContexts(assayMatch)

        assert assayMatch.limitedAssayContextList.size() == 1
        LimitedAssayContext lac = assayMatch.limitedAssayContextList.get(0)
        assert lac.assayContext == context
        assert lac.itemSet.size() == 1
        assert lac.itemSet.iterator().next() == context.assayContextItems.get(0)
        assert lac.duplicateOriginalItemMap.keySet().size() == 1

        context.assayContextItems.add(new AssayContextItem(attributeElement: e1, valueElement: e2, attributeType: AttributeType.Fixed))
        context.assayContextItems.add(new AssayContextItem(attributeElement: e1, valueElement: e3, attributeType: AttributeType.Fixed))
        assert context.assayContextItems.size() == 4

        assayMatch = new AssayMatch(assay)

        AssayContextAndItemDuplicateFinder.buildLimitedAssayContexts(assayMatch)

        assert assayMatch.limitedAssayContextList.size() == 1
        lac = assayMatch.limitedAssayContextList.get(0)
        assert lac.assayContext == context

        assert lac.itemSet.size() == 2
        for (AssayContextItem aci : lac.itemSet) {
            assert aci == context.assayContextItems.get(0) || aci == context.assayContextItems.get(3)
        }

        assert lac.duplicateOriginalItemMap.keySet().size() == 2
    }

    private static AssayContext buildAssayContext() {
        AssayContext context = new AssayContext()
        context.assayContextItems.add(new AssayContextItem(attributeElement: e1, valueElement: e2, attributeType: AttributeType.Fixed))
        context.assayContextItems.add(new AssayContextItem(attributeElement: e1, valueElement: e2, attributeType: AttributeType.Fixed))

        return context
    }
}

