package bard.db.model

import bard.db.enums.ContextType
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

    public static class ContextGroup {
        String key;
        String description;
        List<AbstractContext> value;
    }

    List<ContextGroup> groupContexts() {
        List<ContextGroup> groups = []
        contexts.groupBy { it.contextType } .each{k,v ->
            groups << new ContextGroup(key: k.id, description: "", value: v)
        }

        return groups
    }

    ContextGroup groupBySection(ContextType type) {
        def result = contexts.findAll { println "${it.contextType}  == ${type}" ; return it.contextType == type }
        return new ContextGroup(key: type.id, description: "", value: result);
    }

    ContextGroup groupUnclassified() {
        groupBySection(ContextType.UNCLASSIFIED)
    }

    ContextGroup groupAssayDesign() {
        groupBySection(ContextType.ASSAY_DESIGN)
    }

    ContextGroup groupAssayReadout() {
        groupBySection(ContextType.ASSAY_READOUT)
    }

    ContextGroup groupAssayComponents() {
        groupBySection(ContextType.ASSAY_COMPONENTS)
    }

    ContextGroup groupBiology() {
        groupBySection(ContextType.BIOLOGY)
    }

    ContextGroup groupExperimentalVariables() {
        groupBySection(ContextType.EXPERIMENT)
    }

    ContextGroup groupAssayProtocol() {
        groupBySection(ContextType.ASSAY_PROTOCOL)
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
        int totalNumContextItems = 0
        if (contexts.size() > 0) {
            totalNumContextItems = contexts.collect { it.getContextItems().size() }.sum()
        }
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

    public abstract void removeContext(AbstractContext context);

    public abstract AbstractContext createContext(Map properties);
}
