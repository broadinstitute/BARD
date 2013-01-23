package bard.db.model

import bard.db.dictionary.Descriptor
import bard.db.registration.AssayContext

/**
 * Created with IntelliJ IDEA.
 * User: ddurkin
 * Date: 1/7/13
 * Time: 4:35 PM
 * To change this template use File | Settings | File Templates.
 */
abstract class AbstractContextOwner {

    abstract List getContexts()

    /**
     * Create a map where all the assayContexts are grouped a common root in the ontology hierarchy based on a prefered
     * descriptor for the context.
     *
     * @return a Map keyed by the first 2 levels of the ontology hierarchy path with a each key having a list of assayContexts
     */
    Map<String, AbstractContext> groupContexts() {
        Map<String, List<AbstractContext>> mapByPath = getContexts().groupBy { AbstractContext context ->
            context.getContextGroup().toLowerCase()
        }
        mapByPath as TreeMap<String, List<AbstractContext>>
    }

    /**
     * a hack to try and split the contexts into columns of relatively equal contextItems
     *
     * an attempt at limiting white space and compressing the view
     *
     * @param contexts
     * @return list of up to 2 lists
     */
    List<List<AbstractContext>> splitForColumnLayout(List<AbstractContext> contexts) {
        int totalNumContextItems = contexts.collect { it.getContextItems().size() }.sum()
        int half = totalNumContextItems / 2
        int count = 0
        List<AssayContext> firstColumnContexts = contexts.findAll { context ->
            count += context.contextItems.size();
            count <= half
        }
        List<AssayContext> secondColumnContexts = contexts - firstColumnContexts
        def splitContexts = [firstColumnContexts, secondColumnContexts].findAll() // eliminates any empty lists
        splitContexts
    }
}
