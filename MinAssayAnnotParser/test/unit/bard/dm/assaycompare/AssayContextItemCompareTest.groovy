package bard.dm.assaycompare

import grails.test.mixin.TestFor
import bard.dm.assaycompare.AssayContextItemCompare

import bard.db.registration.AssayContextItem
import bard.db.dictionary.Element

import bard.dm.assaycompare.ContextItemComparisonResultEnum
import bard.db.registration.AttributeType

/**
 * Created with IntelliJ IDEA.
 * User: dlahr
 * Date: 9/23/12
 * Time: 7:19 PM
 * To change this template use File | Settings | File Templates.
 */
@TestFor(AssayContextItemCompare)
class AssayContextItemCompareTest {
    private static final Element e1 = new Element(id: 1, label: "e1")
    private static final Element e2 = new Element(id: 2, label: "e2")
    private static final Element e3 = new Element(id: 3, label: "e3")

    private final static double value = 11.01

    private final static String extValueId = "external value id"

    private final static String valueDisplay = "value for display"

    private AssayContextItemCompare assayContextItemCompare
    void setUp() {
        assayContextItemCompare = new AssayContextItemCompare()
    }

    void testUnmatchedAttributes() {
        AssayContextItem aci1 = new AssayContextItem(attributeElement: e1)
        AssayContextItem aci2 = new AssayContextItem(attributeElement: e2)

        assert assayContextItemCompare.compareContextItems(aci1, aci2).equals(ContextItemComparisonResultEnum.DoesNotMatch)
    }

    void testWithAttributeType() {
        AssayContextItem aci1 = new AssayContextItem(attributeElement: e1, attributeType: AttributeType.Free)
        AssayContextItem aci2 = new AssayContextItem(attributeElement: e1, attributeType: AttributeType.Free)

        assert assayContextItemCompare.compareContextItems(aci1, aci2).equals(ContextItemComparisonResultEnum.ExactMatch)

        aci2.attributeType = AttributeType.Range
        assert assayContextItemCompare.compareContextItems(aci1, aci2).equals(ContextItemComparisonResultEnum.DoesNotMatch)
    }

    void testWithElementValues() {
        AssayContextItem aci1 = new AssayContextItem(attributeElement: e1, valueElement: e2, attributeType: AttributeType.Fixed)
        AssayContextItem aci2 = new AssayContextItem(attributeElement: e1, valueElement: e2, attributeType: AttributeType.Fixed)

        assert assayContextItemCompare.compareContextItems(aci1, aci2).equals(ContextItemComparisonResultEnum.ExactMatch)

        aci2.valueElement = e3
        assert assayContextItemCompare.compareContextItems(aci1, aci2).equals(ContextItemComparisonResultEnum.DoesNotMatch)

        aci2.valueElement = null
        assert assayContextItemCompare.compareContextItems(aci1, aci2).equals(ContextItemComparisonResultEnum.DoesNotMatch)
        aci2.valueElement = e3

        aci1.valueElement = null
        assert assayContextItemCompare.compareContextItems(aci1, aci2).equals(ContextItemComparisonResultEnum.DoesNotMatch)
    }


    void testWithExternalValueId() {
        AssayContextItem aci1 = new AssayContextItem(attributeElement: e1, extValueId: extValueId, attributeType: AttributeType.Fixed)
        AssayContextItem aci2 = new AssayContextItem(attributeElement: e1, extValueId: extValueId, attributeType: AttributeType.Fixed)

        assert assayContextItemCompare.compareContextItems(aci1, aci2).equals(ContextItemComparisonResultEnum.ExactMatch)

        aci2.extValueId = extValueId + " and some more stuff"
        assert assayContextItemCompare.compareContextItems(aci1, aci2).equals(ContextItemComparisonResultEnum.DoesNotMatch)

        aci2.extValueId = null
        assert assayContextItemCompare.compareContextItems(aci1, aci2).equals(ContextItemComparisonResultEnum.DoesNotMatch)
        aci2.extValueId = extValueId

        aci1.extValueId = null
        assert assayContextItemCompare.compareContextItems(aci1, aci2).equals(ContextItemComparisonResultEnum.DoesNotMatch)
    }

    void testWithNumericalValues() {
        AssayContextItem aci1 = new AssayContextItem(attributeElement: e1, valueNum: value, attributeType: AttributeType.Fixed)
        AssayContextItem aci2 = new AssayContextItem(attributeElement: e1, valueNum: value, attributeType: AttributeType.Fixed)

        assert assayContextItemCompare.compareContextItems(aci1, aci2).equals(ContextItemComparisonResultEnum.EpsMatch), 'valueNum is equal and qualifiers are both null'

        aci1.qualifier = ">"
        aci2.qualifier = ">"
        assert assayContextItemCompare.compareContextItems(aci1, aci2).equals(ContextItemComparisonResultEnum.EpsMatch), 'valueNum is equal and qualifiers are both >'

        aci1.qualifier = "<"
        assert assayContextItemCompare.compareContextItems(aci1,aci2).equals(ContextItemComparisonResultEnum.DoesNotMatch), 'valueNum is equal but qualifiers are different'

        aci1.qualifier = null
        assert assayContextItemCompare.compareContextItems(aci1,aci2).equals(ContextItemComparisonResultEnum.DoesNotMatch), ' valueNum is equal but one qualifier is null'

        aci1.qualifier = "<"
        aci2.qualifier = null
        assert assayContextItemCompare.compareContextItems(aci1,aci2).equals(ContextItemComparisonResultEnum.DoesNotMatch), 'valueNum is equal but switched which qualifier is null'

        aci1.qualifier = null
        aci2.valueNum = value*(1.0 + assayContextItemCompare.eps)
        assert assayContextItemCompare.compareContextItems(aci1,aci2).equals(ContextItemComparisonResultEnum.EpsMatch), 'valueNum is equal within EPS limit, qualifiers are null'

        aci2.valueNum = value*(1.0 + (2.0*assayContextItemCompare.eps))
        assert assayContextItemCompare.compareContextItems(aci1,aci2).equals(ContextItemComparisonResultEnum.DoesNotMatch), 'valueNum is different, qualifiers are null'
    }

    void testNumericalValueMax() {
        AssayContextItem aci1 = new AssayContextItem(attributeElement: e1, valueMax: value, attributeType: AttributeType.Fixed)
        AssayContextItem aci2 = new AssayContextItem(attributeElement: e1, valueMax: value, attributeType: AttributeType.Fixed)

        assert assayContextItemCompare.compareContextItems(aci1,aci2).equals(ContextItemComparisonResultEnum.EpsMatch)

        aci1.valueMax = value*(1.0 + (assayContextItemCompare.eps*2.0))
        assert assayContextItemCompare.compareContextItems(aci1,aci2).equals(ContextItemComparisonResultEnum.DoesNotMatch)
    }

    void testNumericalValueMin() {
        AssayContextItem aci1 = new AssayContextItem(attributeElement: e1, valueMin: value, attributeType: AttributeType.Fixed)
        AssayContextItem aci2 = new AssayContextItem(attributeElement: e1, valueMin: value, attributeType: AttributeType.Fixed)

        assert assayContextItemCompare.compareContextItems(aci1,aci2).equals(ContextItemComparisonResultEnum.EpsMatch)

        aci1.valueMin = value*(1.0 + (assayContextItemCompare.eps*2.0))
        assert assayContextItemCompare.compareContextItems(aci1,aci2).equals(ContextItemComparisonResultEnum.DoesNotMatch)
    }

    void testNumericalRangeValues() {
        AssayContextItem aci1 = new AssayContextItem(attributeElement: e1, valueMax: value, valueMin: value, attributeType: AttributeType.Fixed)
        AssayContextItem aci2 = new AssayContextItem(attributeElement: e1, valueMax: value, valueMin: value, attributeType: AttributeType.Fixed)

        assert assayContextItemCompare.compareContextItems(aci1,aci2).equals(ContextItemComparisonResultEnum.EpsMatch)

        aci1.valueMax = value*(1.0 + (assayContextItemCompare.eps*2.0))
        assert assayContextItemCompare.compareContextItems(aci1,aci2).equals(ContextItemComparisonResultEnum.DoesNotMatch)
        aci1.valueMax = value

        aci2.valueMax = value*(1.0 + (assayContextItemCompare.eps*2.0))
        assert assayContextItemCompare.compareContextItems(aci1,aci2).equals(ContextItemComparisonResultEnum.DoesNotMatch)
        aci2.valueMax = value

        aci1.valueMin = value*(1.0 + (assayContextItemCompare.eps*2.0))
        assert assayContextItemCompare.compareContextItems(aci1,aci2).equals(ContextItemComparisonResultEnum.DoesNotMatch)
        aci1.valueMin = value

        aci2.valueMin = value*(1.0 + (assayContextItemCompare.eps*2.0))
        assert assayContextItemCompare.compareContextItems(aci1,aci2).equals(ContextItemComparisonResultEnum.DoesNotMatch)
        aci2.valueMin = value

        aci1.valueMax = value*(1.0 + (assayContextItemCompare.eps*2.0))
        aci1.valueMin = value*(1.0 + (assayContextItemCompare.eps*2.0))
        assert assayContextItemCompare.compareContextItems(aci1,aci2).equals(ContextItemComparisonResultEnum.DoesNotMatch)
        aci1.valueMax = value
        aci1.valueMin = value

        aci2.valueMax = value*(1.0 + (assayContextItemCompare.eps*2.0))
        aci2.valueMin = value*(1.0 + (assayContextItemCompare.eps*2.0))
        assert assayContextItemCompare.compareContextItems(aci1,aci2).equals(ContextItemComparisonResultEnum.DoesNotMatch)
        aci2.valueMax = value
        aci2.valueMin = value

        aci1.valueMax = value*(1.0 + (assayContextItemCompare.eps*2.0))
        aci2.valueMin = value*(1.0 + (assayContextItemCompare.eps*2.0))
        assert assayContextItemCompare.compareContextItems(aci1,aci2).equals(ContextItemComparisonResultEnum.DoesNotMatch)
        aci1.valueMax = value
        aci2.valueMin = value

        aci2.valueMax = value*(1.0 + (assayContextItemCompare.eps*2.0))
        aci1.valueMin = value*(1.0 + (assayContextItemCompare.eps*2.0))
        assert assayContextItemCompare.compareContextItems(aci1,aci2).equals(ContextItemComparisonResultEnum.DoesNotMatch)
        aci2.valueMax = value
        aci1.valueMin = value
    }

    void testValueDisplay() {
        AssayContextItem aci1 = new AssayContextItem(attributeElement: e1, valueDisplay: valueDisplay, attributeType: AttributeType.Fixed)
        AssayContextItem aci2 = new AssayContextItem(attributeElement: e1, valueDisplay: valueDisplay, attributeType: AttributeType.Fixed)

        assert assayContextItemCompare.compareContextItems(aci1, aci2).equals(ContextItemComparisonResultEnum.ExactMatch), 'same value display'

        aci1.valueElement = e2
        aci2.valueElement = e3
        assert assayContextItemCompare.compareContextItems(aci1, aci2).equals(ContextItemComparisonResultEnum.DoesNotMatch), 'same value display but different value elements - value elements have priority'

        aci2.valueElement = e2
        aci1.valueDisplay = "other junk"
        assert assayContextItemCompare.compareContextItems(aci1, aci2).equals(ContextItemComparisonResultEnum.ExactMatch), 'different value display but same value elements, value elements have priority'
    }

    void testNullNumericalValues() {
        AssayContextItem aci1 = new AssayContextItem(attributeElement: e1, valueNum: value, attributeType: AttributeType.Fixed)
        AssayContextItem aci2 = new AssayContextItem(attributeElement: e1, attributeType: AttributeType.Fixed)

        assert assayContextItemCompare.compareContextItems(aci1,aci2).equals(ContextItemComparisonResultEnum.DoesNotMatch)

        aci1.valueNum = null
        aci2.valueNum = value
        assert assayContextItemCompare.compareContextItems(aci1, aci2).equals(ContextItemComparisonResultEnum.DoesNotMatch)
    }

    void testNullNumericalMin() {
        AssayContextItem aci1 = new AssayContextItem(attributeElement: e1, valueMin: value, attributeType: AttributeType.Fixed)
        AssayContextItem aci2 = new AssayContextItem(attributeElement: e1, attributeType: AttributeType.Fixed)

        assert assayContextItemCompare.compareContextItems(aci1, aci2).equals(ContextItemComparisonResultEnum.DoesNotMatch)

        aci1.valueMin = null
        aci2.valueMin = value
        assert assayContextItemCompare.compareContextItems(aci1, aci2).equals(ContextItemComparisonResultEnum.DoesNotMatch)
    }

    void testNullNumericalMax() {
        AssayContextItem aci1 = new AssayContextItem(attributeElement: e1, valueMax: value, attributeType: AttributeType.Fixed)
        AssayContextItem aci2 = new AssayContextItem(attributeElement: e1, attributeType: AttributeType.Fixed)

        assert assayContextItemCompare.compareContextItems(aci1, aci2).equals(ContextItemComparisonResultEnum.DoesNotMatch)

        aci1.valueMax = null
        aci2.valueMax = value
        assert assayContextItemCompare.compareContextItems(aci1, aci2).equals(ContextItemComparisonResultEnum.DoesNotMatch)
    }

    void testNullValueDisplay() {
        AssayContextItem aci1 = new AssayContextItem(attributeElement: e1, valueDisplay: valueDisplay, attributeType: AttributeType.Fixed)
        AssayContextItem aci2 = new AssayContextItem(attributeElement: e1, valueDisplay: null, attributeType: AttributeType.Fixed)

        assert assayContextItemCompare.compareContextItems(aci1, aci2).equals(ContextItemComparisonResultEnum.DoesNotMatch), 'one value display null, the other not'

        aci1.valueDisplay = null
        aci2.valueDisplay = valueDisplay
        assert assayContextItemCompare.compareContextItems(aci1, aci2).equals(ContextItemComparisonResultEnum.DoesNotMatch), 'reversed which value display is null and which is not'
    }
}
