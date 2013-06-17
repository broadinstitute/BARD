package bard.db

import bard.db.model.AbstractContext
import bard.db.model.AbstractContextOwner
import bard.db.registration.Assay
import bard.db.registration.AssayContext
import bard.db.registration.AssayContextItem

class ContextService {

    AbstractContext createContext(AbstractContextOwner owner, String name, String section) {
        AbstractContext context = owner.createContext(contextName: name, contextGroup: section)
        context.save(flush: true)
        return context
    }

    void deleteContext(AbstractContext context) {
        AbstractContextOwner owner = context.owner
        owner.removeContext(context)
        context.delete()
    }

//    public void updateContextName(AssayContext targetAssayContext, AssayContextItem sourceAssayContextItem) {
//        if (targetAssayContext && targetAssayContext == sourceAssayContextItem.assayContext) {
//            targetAssayContext.contextName = sourceAssayContextItem.valueDisplay
//        }
//    }

}
