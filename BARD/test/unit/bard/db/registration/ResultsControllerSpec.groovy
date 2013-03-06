package bard.db.registration

import grails.test.mixin.TestMixin
import grails.test.mixin.support.GrailsUnitTestMixin
import spock.lang.Specification

/**
 * Created with IntelliJ IDEA.
 * User: pmontgom
 * Date: 2/12/13
 * Time: 10:38 AM
 * To change this template use File | Settings | File Templates.
 */
@TestMixin(GrailsUnitTestMixin)
class ResultsControllerSpec extends Specification {
    def 'test FieldListCommand parsing'() {
        setup:
        def itemService = Mock(ItemService)
        itemService.get(_) >> { x -> new ItemService.Item(id: x[0]) }

        when:
        FieldListCommand command = new FieldListCommand(itemService: itemService,
                contextItemIds: ["I:1", "I:2"])

        then:
        def expItems = command.getExperimentContextItems()
        expItems.size() == 2

        when:
        def expItemsIds = expItems.collect { it.id }

        then:
        expItemsIds.contains("I:1")
        expItemsIds.contains("I:2")
    }
}
