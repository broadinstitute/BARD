package cars

/**
 * Created with IntelliJ IDEA.
 * User: xiaorong
 * Date: 3/6/13
 * Time: 11:49 AM
 * To change this template use File | Settings | File Templates.
 */
class RemoverServiceIntegrationTest extends GroovyTestCase {
    def removerService

    void testDeleteProjects() {
        String modifiedBy = 'dlahr'
        removerService.deleteProject(modifiedBy)
    }
}
