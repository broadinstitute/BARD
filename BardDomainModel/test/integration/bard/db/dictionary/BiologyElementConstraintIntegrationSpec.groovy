package bard.db.dictionary

import org.junit.Before
import spock.lang.Unroll

/**
 * Created with IntelliJ IDEA.
 * User: balexand
 * Date: 6/21/12
 * Time: 3:11 PM
 * To change this template use File | Settings | File Templates.
 */
@Unroll
class BiologyElementConstraintIntegrationSpec extends AbstractElementConstraintIntegrationSpec {

    @Before
    void doSetup() {
        domainInstance = BiologyElement.buildWithoutSave()
//        unitElement = AssayElement.build(label: createString(UNIT_MAX_SIZE, 'u'))
    }

}