package maas

/**
 * Created with IntelliJ IDEA.
 * User: xiaorong
 * Date: 3/13/13
 * Time: 4:33 PM
 * To change this template use File | Settings | File Templates.
 */
class AttributeNameMappingIntegrationTest extends GroovyTestCase {
    void testBuildMap() {
        def terms = AttributeNameMapping.build()
        assert terms.size() > 1
    }
}
