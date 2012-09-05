package bard.db.registration

import grails.buildtestdata.mixin.Build
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
@Build([AssayContext, AssayContextItem])
@Unroll
class AssayContextServiceUnitSpec extends Specification {

    AssayContextService service = new AssayContextService()
    @Shared String ORIGINAL_CONTEXT_NAME = 'original title'
    @Shared String NEW_CONTEXT_NAME = 'new title'


    void "test addItem #desc"() {
        given: 'an a'
        AssayContext targetAssayContext = AssayContext.build(assayContextItems: existingAssayContextItems)
        AssayContext sourceAssayContext = AssayContext.build(contextName: ORIGINAL_CONTEXT_NAME)
        sourceAssayContext.addToAssayContextItems(AssayContextItem.build(valueDisplay: ORIGINAL_CONTEXT_NAME))
        AssayContextItem draggedAssayContextItem = sourceAssayContext.assayContextItems.first()
        assert sourceAssayContext.assayContextItems.size() == 1
        assert sourceAssayContext == draggedAssayContextItem.assayContext

        when:
        service.addItem(draggedAssayContextItem, targetAssayContext)

        then:
        assertItemAdded(targetAssayContext, draggedAssayContextItem, sizeAfterAdd, indexOfAddedItem)


        where:
        desc                            | existingAssayContextItems                        | indexOfAddedItem | sizeAfterAdd
        'add item to empty list'        | []                                               | 0                | 1
        'add item to list with 1 item'  | [new AssayContextItem()]                         | 1                | 2
        'add item to list with 2 items' | [new AssayContextItem(), new AssayContextItem()] | 2                | 3

    }

    void "test addItemAfter #desc"() {
        given:
        AssayContext targetAssayContext = AssayContext.build(assayContextItems: existingAssayContextItems)
        AssayContext sourceAssayContext = AssayContext.build(contextName: ORIGINAL_CONTEXT_NAME)
        sourceAssayContext.addToAssayContextItems(AssayContextItem.build(valueDisplay: ORIGINAL_CONTEXT_NAME))
        AssayContextItem draggedAssayContextItem = sourceAssayContext.assayContextItems.first()
        assert sourceAssayContext.assayContextItems.size() == 1
        assert sourceAssayContext == draggedAssayContextItem.assayContext

        when:
        service.addItem(indexOfAddedItem, draggedAssayContextItem, targetAssayContext)

        then:
        assertItemAdded(targetAssayContext, draggedAssayContextItem, sizeAfterAdd, indexOfAddedItem)


        where:
        desc                    | existingAssayContextItems                        | indexOfAddedItem | sizeAfterAdd
        'addItem to empty list' | []                                               | 0                | 1
        'addItem at index 0'    | [new AssayContextItem()]                         | 0                | 2
        'addItem at index 1'    | [new AssayContextItem(), new AssayContextItem()] | 1                | 3

    }



    void "test optionallyChangeContextName #desc"() {
        given: 'an a'
        AssayContext sourceContext = new AssayContext(contextName: ORIGINAL_CONTEXT_NAME,
                assayContextItems: existingAssayContextItems)


        when:
        service.optionallyChangeContextName(sourceContext)

        then:
        expectedContextName == sourceContext.contextName

        where:
        desc                                                             | expectedContextName   | existingAssayContextItems
        'change contextName to null when assayContextItems empty'        | null                  | []
        'contextName not changed with assayContextItem matches title'    | ORIGINAL_CONTEXT_NAME | [new AssayContextItem(valueDisplay: ORIGINAL_CONTEXT_NAME)]
        'contextName not changed with assayContextItem matches title'    | ORIGINAL_CONTEXT_NAME | [new AssayContextItem(valueDisplay: NEW_CONTEXT_NAME), new AssayContextItem(valueDisplay: ORIGINAL_CONTEXT_NAME)]
        'contextName updated when assayContextItem does not match title' | NEW_CONTEXT_NAME      | [new AssayContextItem(valueDisplay: NEW_CONTEXT_NAME)]
    }

    public void assertItemAdded(AssayContext targetAssayContext, AssayContextItem draggedAssayContextItem, int sizeAfterAdd, int indexOfAddedItem) {
        assert draggedAssayContextItem.assayContext == targetAssayContext
        assert draggedAssayContextItem in targetAssayContext.assayContextItems
        assert sizeAfterAdd == targetAssayContext.assayContextItems.size()
        assert indexOfAddedItem == targetAssayContext.assayContextItems.indexOf(draggedAssayContextItem)
    }


}
