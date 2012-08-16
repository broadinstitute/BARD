package test.bard.db.registration

import org.junit.Before
import spock.lang.Specification
import spock.lang.Unroll

/**
 * Created with IntelliJ IDEA.
 * User: ddurkin
 * Date: 8/16/12
 * Time: 3:18 PM
 * To change this template use File | Settings | File Templates.
 */
@Unroll
abstract class AbstractDomainSpec extends Specification {

    /**
     * set up the domain in the concrete class
     * to play nice the @Build annotation
     * i.e. domainInstance = Assay.buildWithoutSave()
     */
    @Before
    abstract void doSetup()

}
