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
        return addItem(targetAssayContext.assayContextItems.size(), sourceItem, targetAssayContext)
    }

    public AssayContext addItem(int index, AssayContextItem sourceItem, AssayContext targetAssayContext) {
        AssayContext sourceAssayContext = sourceItem.assayContext
        sourceAssayContext.removeFromAssayContextItems(sourceItem)
        sourceItem.assayContext = targetAssayContext
        targetAssayContext.assayContextItems.add(index, sourceItem)
        optionallyChangeContextName(sourceAssayContext)
        optionallyChangeContextName(targetAssayContext)
        return targetAssayContext
    }

    /**
     * If we can find a match for the contextName in the existing assayContextItems, do nothing
     * if we can't find a match use the first assayContextItem if it exists
     * @param sourceAssayContext
     */
    public void optionallyChangeContextName(AssayContext sourceAssayContext) {
        if (sourceAssayContext.assayContextItems.find {sourceAssayContext.contextName == it.valueDisplay}) {
            // item responsible for contextName found in assayContextItems
        }
        else {
            AssayContextItem first = sourceAssayContext.assayContextItems[0]
            sourceAssayContext.contextName = first?.valueDisplay?: 'Empty Card, consider deleting!'
        }
    }
}