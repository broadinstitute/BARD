package bard.db.dictionary

import org.junit.Before
import spock.lang.Unroll

import static bard.db.dictionary.AbstractElement.UNIT_MAX_SIZE
import static test.TestUtils.createString

/**
 * Created with IntelliJ IDEA.
 * User: balexand
 * Date: 6/21/12
 * Time: 3:11 PM
 * To change this template use File | Settings | File Templates.
 */
@Unroll
class ElementConstraintIntegrationSpec extends AbstractElementConstraintIntegrationSpec {

    @Before
    void doSetup() {
        domainInstance = Element.buildWithoutSave()
        //TODO: Dan please take a look had to comment this out to get it to work with grails 2.1.1
       // unitElement = Element.build(label: createString(UNIT_MAX_SIZE, 'u'))
    }

}