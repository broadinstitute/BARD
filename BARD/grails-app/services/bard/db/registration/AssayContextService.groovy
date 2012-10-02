package bard.db.registration

/**
 * Created with IntelliJ IDEA.
 * User: ddurkin
 * Date: 8/31/12
 * Time: 3:36 PM
 * To change this template use File | Settings | File Templates.
 */
class AssayContextService {

    public AssayContext addItem(AssayContextItem sourceItem, AssayContext targetAssayContext) {
        if(sourceItem && sourceItem.assayContext != targetAssayContext){
            return addItem(targetAssayContext.assayContextItems.size(), sourceItem, targetAssayContext)
        }
    }

    public AssayContext addItem(int index, AssayContextItem sourceItem, AssayContext targetAssayContext) {
        AssayContext sourceAssayContext = sourceItem.assayContext
        sourceAssayContext.removeFromAssayContextItems(sourceItem)
        sourceItem.assayContext = targetAssayContext
        targetAssayContext.assayContextItems.add(index, sourceItem)
        return targetAssayContext
    }

    public void updateContextName(AssayContext targetAssayContext, AssayContextItem sourceAssayContextItem) {
        if (targetAssayContext && targetAssayContext == sourceAssayContextItem.assayContext) {
            targetAssayContext.contextName = sourceAssayContextItem.valueDisplay
        }
    }

    public AssayContext deleteItem(AssayContextItem assayContextItem) {
        AssayContext assayContext = assayContextItem.assayContext
        assayContext.removeFromAssayContextItems(assayContextItem)
        assayContextItem.delete()
        optionallyChangeContextName(assayContext)
        return assayContext
    }
}