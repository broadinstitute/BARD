package bard.db.registration

import bard.db.dictionary.Element
import bard.db.enums.HierarchyType
import grails.buildtestdata.mixin.Build
import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

/**
 * Created with IntelliJ IDEA.
 * User: ddurkin
 * Date: 9/4/12
 * Time: 11:30 AM
 * To change this template use File | Settings | File Templates.
 */
@Build([Assay,AssayContext, AssayContextItem, AssayContextMeasure, Measure])
@Mock([AssayContext, AssayContextItem, AssayContextMeasure, Measure])
@TestFor(AssayContextService)
@Unroll
class AssayContextServiceUnitSpec extends Specification {

    @Shared String ORIGINAL_CONTEXT_NAME = 'original title'
    @Shared String NEW_CONTEXT_NAME = 'new title'

    void "test deleteAssayContext"() {
        given:
        Assay assay = Assay.build()
        3.times({assay.addToAssayContexts(AssayContext.build(assay:assay))})
        final AssayContext assayContext = assay.assayContexts.first()
         int initialNumberOfContexts = assay.assayContexts.size()
        when:
        service.deleteAssayContext(assayContext)
        then:
        assert assay.assayContexts.size() == (initialNumberOfContexts-1)
    }

    void "test changeParentChildRelationship #desc"() {
        given:
        Measure measure = Measure.build()
        when:
        Measure foundMeasure = service.changeParentChildRelationship(measure, hierarchyType)
        then:
        assert foundMeasure

        where:
        desc                                              | hierarchyType
        "Hierarchy Type ${HierarchyType.CALCULATED_FROM}" | HierarchyType.CALCULATED_FROM
        "Hierarchy Type ${HierarchyType.SUPPORTED_BY}"    | HierarchyType.SUPPORTED_BY
        ""                                                | null

    }

    void "test addItemToEndOfList #desc"() {
        given: 'an a'

        AssayContext targetAssayContext = AssayContext.build(assayContextItems: createAssayContextItem(numberOfExistingContextItems))
        AssayContext sourceAssayContext = AssayContext.build(contextName: ORIGINAL_CONTEXT_NAME)
        sourceAssayContext.addToAssayContextItems(AssayContextItem.build())
        AssayContextItem draggedAssayContextItem = sourceAssayContext.assayContextItems.first()
        assert sourceAssayContext.assayContextItems.size() == 1
        assert sourceAssayContext == draggedAssayContextItem.assayContext

        when:
        service.addItem(draggedAssayContextItem, targetAssayContext)

        then:
        assertItemAdded(targetAssayContext, draggedAssayContextItem, sizeAfterAdd, indexOfAddedItem)

        where:
        desc                            | numberOfExistingContextItems | indexOfAddedItem | sizeAfterAdd
        'add item to empty list'        | 0                            | 0                | 1
        'add item to list with 1 item'  | 1                            | 1                | 2
        'add item to list with 2 items' | 2                            | 2                | 3

    }

    void "test addItemToEndOfList when item is already in list"() {
        given: 'an item already in an AssayContext'
        AssayContext assayContext = AssayContext.build()
        AssayContextItem item = new AssayContextItem()
        assayContext.addToAssayContextItems(item)


        when: 'it is added to the assayContext again'
        service.addItem(assayContext.assayContextItems.first(), assayContext)

        then: 'do nothing, particulary throw an IOOBE' // if you didn't guess the earlier code was resulting in a IOOBE
        notThrown(IndexOutOfBoundsException)
        assayContext.assayContextItems.size() == 1
        item == assayContext.assayContextItems.first()
    }

    void "test addItemAtIndex #desc"() {
        given:
        AssayContext targetAssayContext = AssayContext.build(assayContextItems: createAssayContextItem(numberOfExistingContextItems))
        AssayContext sourceAssayContext = AssayContext.build(contextName: ORIGINAL_CONTEXT_NAME)
        sourceAssayContext.addToAssayContextItems(AssayContextItem.build())
        AssayContextItem draggedAssayContextItem = sourceAssayContext.assayContextItems.first()
        assert sourceAssayContext.assayContextItems.size() == 1
        assert sourceAssayContext == draggedAssayContextItem.assayContext

        when:
        service.addItem(indexOfAddedItem, draggedAssayContextItem, targetAssayContext)

        then:
        assertItemAdded(targetAssayContext, draggedAssayContextItem, sizeAfterAdd, indexOfAddedItem)


        where:
        desc                    | numberOfExistingContextItems | indexOfAddedItem | sizeAfterAdd
        'addItem to empty list' | 0                            | 0                | 1
        'addItem at index 0'    | 1                            | 0                | 2
        'addItem at index 1'    | 2                            | 1                | 3

    }

    void "test associate and disassociate measure with context"() {
        given:
        mockDomain(AssayContextMeasure.class)
        AssayContext context = AssayContext.build()
        Measure measure = Measure.build()

        when:
        service.associateContext(measure, context)

        then:
        measure.assayContextMeasures.size() == 1
        context.assayContextMeasures.size() == 1
        measure.assayContextMeasures.first() == context.assayContextMeasures.first()
        AssayContextMeasure link = measure.assayContextMeasures.first()
        link.measure == measure
        link.assayContext == context

        when:
        service.disassociateContext(measure, context)

        then:
        measure.assayContextMeasures.size() == 0
        context.assayContextMeasures.size() == 0
    }


    private void assertItemAdded(AssayContext targetAssayContext, AssayContextItem draggedAssayContextItem, int sizeAfterAdd, int indexOfAddedItem) {
        assert draggedAssayContextItem.assayContext == targetAssayContext
        assert draggedAssayContextItem in targetAssayContext.assayContextItems
        assert sizeAfterAdd == targetAssayContext.assayContextItems.size()
        assert indexOfAddedItem == targetAssayContext.assayContextItems.indexOf(draggedAssayContextItem)
    }

    private List<AssayContextItem> createAssayContextItem(int i) {
        List<AssayContextItem> items = []
        i.times {
            items << new AssayContextItem(attributeType: AttributeType.Fixed, attributeElement: new Element())
        }
        items
    }


}
