package maas

/**
 * Created with IntelliJ IDEA.
 * User: xiaorong
 * Date: 3/7/13
 * Time: 8:48 AM
 * To change this template use File | Settings | File Templates.
 */
class MustLoadAidIntegrationTest extends GroovyTestCase {
    def testMustLoadedAids() {
        String fileName = 'test/exampleData/maas/most_recent_probe_aids.csv'
        List<Long> aids = MustLoadAid.mustLoadedAids(fileName)
        assert aids.size()> 1
    }
}
