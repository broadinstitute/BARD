package bard.db.registration

/**
 * Created with IntelliJ IDEA.
 * User: ddurkin
 * Date: 8/31/12
 * Time: 3:36 PM
 * To change this template use File | Settings | File Templates.
 */
class AssayContextService {

    public AssayContext addItem(AssayContextItem source, AssayContext targetAssayContext) {
        AssayContext sourceAssayContext = source.assayContext
        if (source.valueDisplay == sourceAssayContext.contextName) {
            AssayContextItem another = sourceAssayContext.assayContextItems.find {it.valueDisplay != sourceAssayContext.contextName}
            sourceAssayContext.contextName = another?.valueDisplay
        }
        sourceAssayContext.removeFromAssayContextItems(source)
        targetAssayContext.addToAssayContextItems(source)
        return targetAssayContext
    }

    public AssayContext addItemAfter(AssayContextItem source, AssayContextItem target, AssayContext targetAssayContext) {
        AssayContext sourceAssayContext = source.assayContext
        if (source.valueDisplay == sourceAssayContext.contextName) {
            AssayContextItem another = sourceAssayContext.assayContextItems.find {it.valueDisplay != sourceAssayContext.contextName}
            sourceAssayContext.contextName = another?.valueDisplay
        }
        sourceAssayContext.removeFromAssayContextItems(source)

        int indexAfterTargetItem = targetAssayContext.assayContextItems.indexOf(target) + 1
        source.assayContext = targetAssayContext
        targetAssayContext.assayContextItems.add(indexAfterTargetItem, source)
        return targetAssayContext
    }


}
