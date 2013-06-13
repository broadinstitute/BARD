package bard.db

import bard.db.context.item.BasicContextItemCommand
import bard.db.model.AbstractContext
import bard.db.model.AbstractContextItem

/**
 * Created with IntelliJ IDEA.
 * User: ddurkin
 * Date: 5/29/13
 * Time: 2:32 PM
 * To change this template use File | Settings | File Templates.
 */
class ContextItemService {

    /**
     * lifting method out of command object to demarcate transactional boundary
     * @param basicContextItemCommand
     */
    boolean updateContextItem(BasicContextItemCommand basicContextItemCommand) {
        boolean updateSuccessful = false
        basicContextItemCommand.with {
            AbstractContextItem contextItem = attemptFindItem()
            if (contextItem) {
                if (version?.longValue() != contextItem.version.longValue()) {
                    getErrors().reject('default.optimistic.locking.failure', [BasicContextItemCommand.getContextItemClass(getContextClass())] as Object[], 'optimistic lock failure')
                    copyFromDomainToCmd(contextItem)
                } else {
                    copyFromCmdToDomain(contextItem)
                    updateSuccessful = attemptSave(contextItem)
                    copyFromDomainToCmd(contextItem)
                }
            }
        }
        return updateSuccessful
    }

    boolean delete(BasicContextItemCommand basicContextItemCommand) {
        boolean deleteSuccessful = false
        basicContextItemCommand.with {
            AbstractContext context = basicContextItemCommand.findContext()
            if (context) {
                AbstractContextItem contextItem = context.contextItems.find { it.id == contextItemId }
                if (contextItem) {
                    context.contextItems.remove(contextItem)
                    contextItem.delete()
                    deleteSuccessful = true
                }
            }
        }
        return deleteSuccessful
    }
}
