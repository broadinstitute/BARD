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

    public static class ContextGroup {
        String key;
        String description;
        List<AbstractContext> value;
    }
    static Map<String, String> groupDesc = [
            "assay protocol> assay component>": "",
            "assay protocol> assay design>": "", // Assay method, detection method.  Kind of an overlap with assay readout
            "assay protocol> assay format>": "",  // tiny number of values.  One card at most under this.
            "assay protocol> assay readout>": "",
            "assay protocol> assay type>": "", // relatively small list
            "biology> molecular interaction>": "",
            "biology>": "",
            "result type> item count>": "",
            "project management> project information>": "",
            "project management> experiment>": "",
            "unclassified>": ""
    ]
    /**
     * Just return the keys for the map
     * @return
     */
    static List<String> groupContextKeys() {
        return new ArrayList<String>(groupDesc.keySet())
    }

    ContextGroup groupUnclassified() {
        List<AbstractContext> values = []
        final List<ContextGroup> contexts = groupContexts()
        for (ContextGroup contextGroup : contexts) {
            if (contextGroup.key.startsWith("unclassified>")) {
                values.addAll(contextGroup.value)
            }
        }
        if (values) {
            return new ContextGroup(key: 'unclassified', description: '', value: values)
        }
        return null
    }

    ContextGroup groupAssayType() {
        List<AbstractContext> values = []
        final List<ContextGroup> contexts = groupContexts()
        for (ContextGroup contextGroup : contexts) {
            if (contextGroup.key.startsWith("assay protocol> assay type>")) {
                values.addAll(contextGroup.value)
            }
        }
        if (values) {
            return new ContextGroup(key: 'assay type', description: '', value: values)
        }
        return null
    }

    ContextGroup groupAssayFormat() {
        List<AbstractContext> values = []
        final List<ContextGroup> contexts = groupContexts()
        for (ContextGroup contextGroup : contexts) {
            if (contextGroup.key.startsWith("assay protocol> assay format>")) {
                values.addAll(contextGroup.value)
            }
        }
        if (values) {
            return new ContextGroup(key: 'assay format', description: '', value: values)
        }
        return null
    }

    ContextGroup groupAssayDesign() {
        List<AbstractContext> values = []
        final List<ContextGroup> contexts = groupContexts()
        for (ContextGroup contextGroup : contexts) {
            if (contextGroup.key.startsWith("assay protocol> assay design>")) {
                values.addAll(contextGroup.value)
            }
        }
        if (values) {
            return new ContextGroup(key: 'Assay Design', description: '', value: values)
        }
        return null
    }

    ContextGroup groupAssayReadout() {
        List<AbstractContext> values = []
        final List<ContextGroup> contexts = groupContexts()
        for (ContextGroup contextGroup : contexts) {
            if (contextGroup.key.startsWith("assay protocol> assay readout>")) {
                values.addAll(contextGroup.value)
            }
        }
        if (values) {
            return new ContextGroup(key: 'Assay Readout', description: '', value: values)
        }
        return null
    }

    ContextGroup groupAssayComponents() {
        List<AbstractContext> values = []
        final List<ContextGroup> contexts = groupContexts()
        for (ContextGroup contextGroup : contexts) {
            if (contextGroup.key.startsWith("assay protocol> assay component>")) {
                values.addAll(contextGroup.value)
            }
        }
        if (values) {
            return new ContextGroup(key: 'Assay Components', description: '', value: values)
        }
        return null
    }

    ContextGroup groupBiology() {
        List<AbstractContext> values = []
        final List<ContextGroup> contexts = groupContexts()
        for (ContextGroup contextGroup : contexts) {
            if (contextGroup.key.startsWith("biology")) {
                values.addAll(contextGroup.value)
            }
        }
        if (values) {
            return new ContextGroup(key: 'Biology', description: '', value: values)
        }
        return null
    }
    ContextGroup groupExperimentalVariables() {
        List<AbstractContext> values = []
        final List<ContextGroup> contexts = groupContexts()
        for (ContextGroup contextGroup : contexts) {
            if (contextGroup.key.startsWith("project management")) {
                values.addAll(contextGroup.value)
            }
        }
        if (values) {
            return new ContextGroup(key: 'Experimental Variables', description: '', value: values)
        }
        return null
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

        /* These ten groups are what is currently in the database for groups.  In the future, we'd like to move these group
           definitions out of this code and someplace where the RDM or some end users can maintain it.
        */
//        def groupDesc = [
//                "assay protocol> assay component>":"",
//                "assay protocol> assay design>":"", // Assay method, detection method.  Kind of an overlap with assay readout
//                "assay protocol> assay format>":"",  // tiny number of values.  One card at most under this.
//                "assay protocol> assay readout>":"",
//                "assay protocol> assay type>":"", // relatively small list
//                "biology> molecular interaction>":"",
//                "biology>":"",
//                "result type> item count>":"",
//                "project management> project information>":"",
//                "project management> experiment>":"",
//                "unclassified>":""
//        ]

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
}
