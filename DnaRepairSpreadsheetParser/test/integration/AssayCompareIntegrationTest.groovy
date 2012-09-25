import bard.db.registration.Assay
import bard.dm.assaycompare.AssayCompare
/**
 * Created with IntelliJ IDEA.
 * User: dlahr
 * Date: 9/24/12
 * Time: 10:06 PM
 * To change this template use File | Settings | File Templates.
 */
class AssayCompareIntegrationTest extends GroovyTestCase {

    private AssayCompare assayCompare

    void setUp() {
        assayCompare = new AssayCompare()
    }

    /**
     * !!!NEED TO CHANGE test datasource in order for this to work.  Instead of using in memory, use personal schema with real data.
     * Edit DataSource.groovy, environments.test
     */
    void testWhat() {
        Assay assay1 = Assay.findById(113)
        Assay assay2 = Assay.findById(692)

        assayCompare.compareAssays(assay1, assay2)
    }
}
