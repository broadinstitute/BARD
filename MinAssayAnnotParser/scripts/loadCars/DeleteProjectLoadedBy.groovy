import bard.db.project.Project
import org.springframework.transaction.support.DefaultTransactionStatus
import cars.RemoverService
/**
 * Created with IntelliJ IDEA.
 * User: xiaorong
 * Date: 3/6/13
 * Time: 2:14 PM
 * To change this template use File | Settings | File Templates.
 */

String username = 'maas'
RemoverService removerService = new RemoverService()
Project.withTransaction { DefaultTransactionStatus status ->
    removerService.deleteProject(username)
}
