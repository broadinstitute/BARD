package bard.db.model

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

    abstract Map<String, String> getGroupDesc();

    List<String> groupContextKeys() {
        return new ArrayList<String>(groupDesc.keySet())
    }

    final static String SECTION_BIOLOGY = "biology"
    final static String SECTION_ASSAY_PROTOCOL = "Assay Protocol"
    final static String SECTION_ASSAY_TYPE = "assay protocol> assay type>"
    final static String SECTION_ASSAY_FORMAT = "assay protocol> assay format>"
    final static String SECTION_ASSAY_DESIGN = "assay protocol> assay design>"
    final static String SECTION_ASSAY_READOUT = "assay protocol> assay readout>"
    final static String SECTION_ASSAY_COMPONENTS = "assay protocol> assay component>"
    final static String SECTION_EXPERIMENTAL_VARIABLES = "project management"
    final static String SECTION_UNCLASSIFIED = "unclassified>"

    final static Map<String, List<String>> SECTION_NAME_MAP = [
            (SECTION_BIOLOGY): [SECTION_BIOLOGY],
            (SECTION_ASSAY_PROTOCOL): [SECTION_ASSAY_TYPE,SECTION_ASSAY_FORMAT],
            (SECTION_ASSAY_TYPE): [SECTION_ASSAY_TYPE],
            (SECTION_ASSAY_FORMAT): [SECTION_ASSAY_FORMAT],
            (SECTION_ASSAY_DESIGN): [SECTION_ASSAY_DESIGN],
            (SECTION_ASSAY_READOUT): [SECTION_ASSAY_READOUT],
            (SECTION_ASSAY_COMPONENTS):[SECTION_ASSAY_COMPONENTS],
            (SECTION_EXPERIMENTAL_VARIABLES): [SECTION_EXPERIMENTAL_VARIABLES],
            (SECTION_UNCLASSIFIED): [SECTION_UNCLASSIFIED]
    ]

    ContextGroup groupBySection(String section){
        List<AbstractContext> values = []
        if(SECTION_NAME_MAP.containsKey(section)){
            for (ContextGroup contextGroup : groupContexts()) {
                if(contextGroup.key.equalsIgnoreCase(section?.decodeURL())){
                    values.addAll(contextGroup.value)
                }
                else {
                    for(String contextGroupPath in SECTION_NAME_MAP.get(section)){
                        if (contextGroup.key.startsWith(contextGroupPath)) {
                            values.addAll(contextGroup.value)
                        }
                    }
                }
            }
            return new ContextGroup(key: section, description: section, value: values)
        }
        return null
    }
    ContextGroup groupUnclassified() {
        groupBySection(SECTION_UNCLASSIFIED)
    }

    ContextGroup groupAssayType() {
        groupBySection(SECTION_ASSAY_TYPE)
    }

    ContextGroup groupAssayFormat() {
        groupBySection(SECTION_ASSAY_FORMAT)
    }

    ContextGroup groupAssayDesign() {
        groupBySection(SECTION_ASSAY_DESIGN)
    }

    ContextGroup groupAssayReadout() {
        groupBySection(SECTION_ASSAY_READOUT)
    }

    ContextGroup groupAssayComponents() {
        groupBySection(SECTION_ASSAY_COMPONENTS)
    }

    ContextGroup groupBiology() {
        groupBySection(SECTION_BIOLOGY)
    }

    ContextGroup groupExperimentalVariables() {
        groupBySection(SECTION_EXPERIMENTAL_VARIABLES)
    }
    ContextGroup groupAssayProtocol() {
        groupBySection(SECTION_ASSAY_PROTOCOL)
    }


    /**
     * Create a map where all the assayContexts are grouped a common root in the ontology hierarchy based on a prefered
     * descriptor for the context.
     *
     * @return a Map keyed by the first 2 levels of the ontology hierarchy path with a each key having a list of assayContexts
     */
    List<ContextGroup> groupContexts() {
        Map<String, List<AbstractContext>> mapByPath = getContexts().groupBy { AbstractContext context ->
            def contextGroup = context.getContextGroup()
            if (contextGroup == null) {
                return "unclassified>"
            }
            return contextGroup.toLowerCase().trim()
        }

        mapByPath.keySet().each { if (!groupDesc.containsKey(it)) groupDesc.put(it, "") }

        return (groupDesc.keySet() as List).collect {
            def group = mapByPath.get(it)
            if (group == null)
                group = []

            return new ContextGroup(key: it, description: groupDesc.get(it), value: group)
        }
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
