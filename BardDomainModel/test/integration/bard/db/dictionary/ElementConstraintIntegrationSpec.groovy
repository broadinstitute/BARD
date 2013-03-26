package bard.db.dictionary

import spock.lang.Unroll

import org.junit.Before

import static bard.db.dictionary.AbstractElement.LABEL_MAX_SIZE
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
        domainInstance = Element.build()
        unitElement = Element.build()
    }

}