package maas

/**
 * Created with IntelliJ IDEA.
 * User: xiaorong
 * Date: 3/13/13
 * Time: 2:25 PM
 * To change this template use File | Settings | File Templates.
 */
class ExternalTermMappingIntegrationTest extends GroovyTestCase {
    void testBuildMap() {
        def terms = ExternalTermMapping.build()
        assert terms.size() > 1
    }
}
