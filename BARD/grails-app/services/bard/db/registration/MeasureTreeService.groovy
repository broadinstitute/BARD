package bard.db.registration

import bard.db.enums.HierarchyType
import bard.db.experiment.Experiment
import bard.db.experiment.ExperimentMeasure

class MeasureTreeService {

    void copyKeyToMeasureId(elements, measuresUsed) {
        for (c in elements) {
            c.measureId = c.key;
            if (measuresUsed.contains(c.key)) {
                c.select = true
            }
            copyKeyToMeasureId(c.children, measuresUsed)
        }
    }
    @Deprecated
    public List createMeasureTreeWithSelections(Assay assay, Experiment experiment, boolean contextsAsChildren) {
        throw new RuntimeException(" createMeasureTreeWithSelections Must be reworked")
//        Set measuresUsed = [] as Set
//        experiment.experimentMeasures.each {
//            measuresUsed.add(it.id)
//        }
//
//        List tree = createMeasureTree(assay, true)
//        copyKeyToMeasureId(tree, measuresUsed)
//
//        return tree;
    }
    @Deprecated
    public List createMeasureTree(Assay assay, boolean contextsAsChildren) {
        throw new RuntimeException("createMeasureTree with assays is deprecated")
//        def roots = []
//        for (experiment in assay.experiments) {
//            Collection rootMeasures = experiment.experimentMeasures.findAll { it.parent == null }
//
//            for (measure in rootMeasures) {
//                roots.add(createTreeFromMeasure(measure, contextsAsChildren))
//            }
//        }
//        sortByKey(roots)
//        return roots
    }

    public List createMeasureTree(Experiment experiment, boolean contextsAsChildren) {
        def roots = []

        for (m in experiment.experimentMeasures.findAll { it.parent == null }) {
            roots << createTreeFromExperimentMeasure(m, contextsAsChildren)
        }

        sortByKey(roots)
        return roots
    }

    public void sortByKey(List<Map> children) {
        for (c in children) {
            if (c.containsKey("children"))
                sortByKey(c["children"])
        }

        children.sort { Map a, Map b -> a["title"].toLowerCase().compareTo(b["title"].toLowerCase()) }
    }

    public Map createTreeFromExperimentMeasure(ExperimentMeasure experimentMeasure, boolean contextsAsChildren) {
        def key = experimentMeasure.id;
        String title = experimentMeasure.displayLabel
        HierarchyType relationship = experimentMeasure.parentChildRelationship


        if (relationship) {
            title = "(${relationship.id}) ${title}"
        }

        Long measureId = experimentMeasure.id

        def children = []

        for (m in experimentMeasure.childMeasures) {
            children.add(createTreeFromExperimentMeasure(m, contextsAsChildren))
        }

        if (contextsAsChildren) {
            for (m in experimentMeasure.assayContextExperimentMeasures) {
                AssayContext context = m.assayContext
                children.add([key: "context-${context.id}", title: "Context: ${context.contextName}"])
            }
        }

        return [key: key, title: title, children: children, expand: true, relationship: relationship?.id, measureId: measureId];
    }

    public Map createTreeFromMeasure(ExperimentMeasure experimentMeasure, boolean contextsAsChildren) {
        def key = experimentMeasure.id;
        String title = experimentMeasure.displayLabel
        HierarchyType relationship = experimentMeasure.parentChildRelationship
        if (relationship) {
            title = "(${relationship.id}) ${title}"
        }
        def children = []

        for (m in experimentMeasure.childrenMeasuresSorted) {
            children.add(createTreeFromMeasure(m, contextsAsChildren))
        }

        if (contextsAsChildren) {
            for (m in experimentMeasure.assayContextExperimentMeasures) {
                AssayContext context = m.assayContext
                children.add([key: "context-${context.id}", title: "Context: ${context.contextName}", hideCheckbox: true, unselectable: true, icon: false])
            }
        }
        return [key: key, title: title, children: children, expand: true, relationship: relationship?.id];
    }
}
