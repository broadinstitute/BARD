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

