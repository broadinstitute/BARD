package bard.dm.assaycompare

import grails.test.mixin.TestFor
import bard.dm.assaycompare.AssayContextCompare
import bard.db.registration.AssayContextItem
import bard.db.dictionary.Element
import bard.db.registration.AssayContext

import bard.dm.assaycompare.ContextItemComparisonResultEnum
import bard.dm.assaycompare.ComparisonResult
import bard.dm.assaycompare.ComparisonResultEnum
import bard.db.registration.Assay
/**
 * Created with IntelliJ IDEA.
 * User: dlahr
 * Date: 9/23/12
 * Time: 10:02 PM
 * To change this template use File | Settings | File Templates.
 */
@TestFor(AssayContextCompare)
class AssayContextCompareTest {
    private static final Element e1 = new Element(id: 1, label: "e1")
    private static final Element e2 = new Element(id: 2, label: "e2")
    private static final Element e3 = new Element(id: 3, label: "e3")
    private static final Element e4 = new Element(id: 4, label: "e4")

    private static final double value = 11.01

    private AssayContextCompare assayContextCompare
    void setUp() {
        assayContextCompare = new AssayContextCompare()
    }

    void testNoContextItems() {
        AssayContext ac1 = new AssayContext()
        AssayContext ac2 = new AssayContext()

        assert null == assayContextCompare.compareContext(ac1, ac2), 'neither has assay context items'

        AssayContextItem item = new AssayContextItem(attributeElement: e1, valueElement: e2)
        ac1.assayContextItems.add(item)
        assert null == assayContextCompare.compareContext(ac1, ac2), 'ac1 only has an assay context item'

        ac1.assayContextItems.remove(item)
        ac2.assayContextItems.add(item)
        assert null == assayContextCompare.compareContext(ac1, ac2), 'ac2 only has an assay context item'
    }

    void testNoMatchingContextItems() {
        AssayContext ac1 = new AssayContext()
        ac1.assayContextItems.add(new AssayContextItem(attributeElement: e1))
        AssayContext ac2 = new AssayContext()
        ac2.assayContextItems.add(new AssayContextItem(attributeElement: e3))

        assert assayContextCompare.compareContext(ac1, ac2).resultEnum.equals(ComparisonResultEnum.DoesNotMatch), 'both have one assayContextItem that do not match'

        AssayContextItem item = new AssayContextItem(attributeElement: e2)
        ac1.assayContextItems.add(item)
        assert assayContextCompare.compareContext(ac1, ac2).resultEnum.equals(ComparisonResultEnum.DoesNotMatch), 'ac1 has 2 assayContextItems, ac2 has 1 assayContextItem, none match'

        ac1.assayContextItems.remove(item)
        ac2.assayContextItems.add(item)
        assert assayContextCompare.compareContext(ac1, ac2).resultEnum.equals(ComparisonResultEnum.DoesNotMatch), 'ac1 has 1 assayContextItem, ac2 has 2 assayContextItems, none match '

        ac1.assayContextItems.add(new AssayContextItem(attributeElement: e4))
        assert assayContextCompare.compareContext(ac1, ac2).resultEnum.equals(ComparisonResultEnum.DoesNotMatch), 'both have 2 assayContextItems, none match'

        ac2.assayContextItems.add(item)
        assert assayContextCompare.compareContext(ac1, ac2).resultEnum.equals(ComparisonResultEnum.DoesNotMatch), 'ac1 has 2 assayContextItems, ac2 has 3 assayContextItems, 2 are the same within ac2 but none are the same between ac1 and ac2'
    }

    void testExactAndExactMatch() {
        AssayContext ac1 = new AssayContext()
        ac1.assayContextItems.add(new AssayContextItem(attributeElement: e1, valueElement: e2))
        AssayContext ac2 = new AssayContext()
        ac2.assayContextItems.add(new AssayContextItem(attributeElement: e1, valueElement: e2))

        ComparisonResult<ContextItemComparisonResultEnum> result = assayContextCompare.compareContext(ac1, ac2)
        assert result.resultEnum.equals(ComparisonResultEnum.ExactMatch), '1 exact matching item only in each context, check exact match at context level'
        assert result.matchCondition.equals(ContextItemComparisonResultEnum.ExactMatch), '1 exact matching item only in each context, check exact match at context item level'

        ac1.assayContextItems.add(new AssayContextItem(attributeElement: e2, valueElement: e3))
        ac2.assayContextItems.add(new AssayContextItem(attributeElement: e2, valueElement: e3))
        result = assayContextCompare.compareContext(ac1, ac2)
        assert result.resultEnum.equals(ComparisonResultEnum.ExactMatch), '2 exact matching items in each context, check exact match at context level'
        assert result.matchCondition.equals(ContextItemComparisonResultEnum.ExactMatch), '2 exact matching items in each context, check exact match at context item level'

        Collections.reverse(ac1.assayContextItems)
        result = assayContextCompare.compareContext(ac1, ac2)
        assert result.resultEnum.equals(ComparisonResultEnum.ExactMatch), '2 exact matching items in each context, order does not match, check exact match at context level'
        assert result.matchCondition.equals(ContextItemComparisonResultEnum.ExactMatch), '2 exact matching items in each context, order does not match, check exact match at context item level'
    }

    void testExactAndEpsMatch() {
        AssayContext ac1 = new AssayContext()
        ac1.assayContextItems.add(new AssayContextItem(attributeElement: e1, valueNum: value))
        AssayContext ac2 = new AssayContext()
        ac2.assayContextItems.add(new AssayContextItem(attributeElement: e1, valueNum: value))

        ComparisonResult<ContextItemComparisonResultEnum> result = assayContextCompare.compareContext(ac1, ac2)
        assert result.resultEnum.equals(ComparisonResultEnum.ExactMatch), '1 eps matching item only in each context, check exact match at context level'
        assert result.matchCondition.equals(ContextItemComparisonResultEnum.EpsMatch), '1 eps matching item only in each context, check for EPS match at context item level'

        ac1.assayContextItems.add(new AssayContextItem(attributeElement: e2, valueElement: e3))
        ac2.assayContextItems.add(new AssayContextItem(attributeElement: e2, valueElement: e3))
        result = assayContextCompare.compareContext(ac1, ac2)
        assert result.resultEnum.equals(ComparisonResultEnum.ExactMatch), '1 eps, 1 exact match items in each context, check exact match at context level'
        assert result.matchCondition.equals(ContextItemComparisonResultEnum.EpsMatch), '1 eps, 1 exact match items in each context, check EPS match at context item level'

        //reverse the order of the items in the sets
        Collections.reverse(ac1.assayContextItems)
        result = assayContextCompare.compareContext(ac1, ac2)
        assert result.resultEnum.equals(ComparisonResultEnum.ExactMatch), '1 eps, 1 exact match items in each context, ordering does not match, check exact match at context level'
        assert result.matchCondition.equals(ContextItemComparisonResultEnum.EpsMatch), '1 eps, 1 exact match items in each context, ordering does not match, check EPS match at context item level'

        Collections.reverse(ac2.assayContextItems)
        result = assayContextCompare.compareContext(ac1, ac2)
        assert result.resultEnum.equals(ComparisonResultEnum.ExactMatch), '1 eps, 1 exact match items in each context, reversed initial order, check exact match at context level'
        assert result.matchCondition.equals(ContextItemComparisonResultEnum.EpsMatch), '1 eps, 1 exact match items in each context, reversed initial order, ordering does not match, check EPS match at context item level'
    }

    void testSubsetMatch() {
        AssayContext ac1 = new AssayContext()
        ac1.assayContextItems.add(new AssayContextItem(attributeElement: e1, valueElement: e2))
        ac1.assayContextItems.add(new AssayContextItem(attributeElement: e3, valueElement: e4))
        AssayContext ac2 = new AssayContext()
        ac2.assayContextItems.add(new AssayContextItem(attributeElement: e1, valueElement: e2))

        assert assayContextCompare.compareContext(ac1, ac2).resultEnum.equals(ComparisonResultEnum.SubsetMatch), 'ac1 has 2, ac2 has 1, check for subset match'

        Collections.reverse(ac1.assayContextItems)
        assert assayContextCompare.compareContext(ac1, ac2).resultEnum.equals(ComparisonResultEnum.SubsetMatch), ' ac1 has 2, ac2 has 1, reverse order, check for subset match'

        ac1.assayContextItems.add(new AssayContextItem(attributeElement: e2, valueNum: value))
        assert assayContextCompare.compareContext(ac1, ac2).resultEnum.equals(ComparisonResultEnum.SubsetMatch), ' ac1 has 3, ac2 has 1, check for subset match'

        ac2.assayContextItems.add(new AssayContextItem(attributeElement: e2, valueNum: value))
        assert assayContextCompare.compareContext(ac1, ac2).resultEnum.equals(ComparisonResultEnum.SubsetMatch), ' ac1 has 3, ac2 has 2, check for subset match'
    }

    void testPartialMatch() {
        AssayContext ac1 = new AssayContext()
        ac1.assayContextItems.add(new AssayContextItem(attributeElement: e1, valueElement: e2))
        ac1.assayContextItems.add(new AssayContextItem(attributeElement: e3, valueElement: e4))
        AssayContext ac2 = new AssayContext()
        ac2.assayContextItems.add(new AssayContextItem(attributeElement: e1, valueElement: e2))
        ac2.assayContextItems.add(new AssayContextItem(attributeElement: e2, valueNum: value))

        assert assayContextCompare.compareContext(ac1, ac2).resultEnum.equals(ComparisonResultEnum.PartialMatch), 'ac1 has 2, ac2 has 2, check for partial match'

        Collections.reverse(ac1.assayContextItems)
        assert assayContextCompare.compareContext(ac1, ac2).resultEnum.equals(ComparisonResultEnum.PartialMatch), 'ac1 has 2, ac2 has 2, reverse order, check for partial match'

        ac1.assayContextItems.add(new AssayContextItem(attributeElement: e4, valueNum: (value*4.0)))
        assert assayContextCompare.compareContext(ac1, ac2).resultEnum.equals(ComparisonResultEnum.PartialMatch), 'ac1 has 3, ac2 has 2, check for partial match'

        ac2.assayContextItems.add(new AssayContextItem(attributeElement: e4, valueNum: (value*4.0)))
        assert assayContextCompare.compareContext(ac1, ac2).resultEnum.equals(ComparisonResultEnum.PartialMatch), 'ac1 has 3, ac2 has 3, two matching items, check for partial match'

        ac1.assayContextItems.add(new AssayContextItem(attributeElement: e2, valueNum: (value*2.0)))
        ac1.assayContextItems.add(new AssayContextItem(attributeElement: e1, valueNum: (value*10.0)))
        assert assayContextCompare.compareContext(ac1, ac2).resultEnum.equals(ComparisonResultEnum.PartialMatch), 'ac1 has 5, ac2 has 3, two matching items, check for partial match'
    }
}
