package maas

import org.junit.Ignore

/**
 * Created with IntelliJ IDEA.
 * User: xiaorong
 * Date: 3/14/13
 * Time: 8:36 AM
 * To change this template use File | Settings | File Templates.
 */
class AssayHandlerServiceIntegrationTest extends GroovyTestCase {
    def assayHandlerService
//    @Ignore
    public testLoad() {
        def inputdir = ['data/maas/problemExcel']
        def mustLoadedAids = MustLoadAid.mustLoadedAids('data/maas/maasDataset2/aids_dataset_2.csv')
        assayHandlerService.load(inputdir, 'data/maas/problemExcel/output', 'xiaorong',mustLoadedAids)
    }
}
