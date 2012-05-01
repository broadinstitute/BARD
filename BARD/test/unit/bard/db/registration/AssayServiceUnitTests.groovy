package bard.db.registration

import grails.test.mixin.TestFor
import grails.test.mixin.Mock
import bard.db.dictionary.ResultType

/**
 * See the API for {@link grails.test.mixin.services.ServiceUnitTestMixin} for usage instructions
 */
@TestFor(AssayService)
@Mock([MeasureContextItem, Assay, MeasureContext, Measure, ResultType])
class AssayServiceUnitTests {

    void testGetMeasureContextItemsForAssay() {

        MeasureContextItem item1 = new MeasureContextItem()
        MeasureContextItem item2 = new MeasureContextItem()
        item1.groupNo = 1
        item2.groupNo = 1
        MeasureContextItem item3 = new MeasureContextItem()
        MeasureContextItem item4 = new MeasureContextItem()

        ResultType resultType = new ResultType()
        Measure measure = new Measure(resultType: resultType)

        MeasureContext context = new MeasureContext(measure: measure)
        context.addToMeasureContextItems(item3)
        context.addToMeasureContextItems(item4)

        Assay assay = new Assay()
        assay.addToMeasureContextItems(item1)
        assay.addToMeasureContextItems(item2)
        assay.addToMeasureContextItems(item3)
        assay.addToMeasureContextItems(item4)
        assay.addToMeasures(measure)

        Map map = service.getMeasureContextItemsForAssay(assay)

        Set items = map.measureContextItems

        assert items.size() == 4
        assert items.contains(item1)
        assert items.contains(item2)

    }
}
