package bard.db.registration

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

    public List createMeasureTreeWithSelections(Assay assay, Experiment experiment, boolean contextsAsChildren) {
        Set measuresUsed = [] as Set
        experiment.experimentMeasures.each {
            measuresUsed.add(it.measure.id)
        }

        List tree = createMeasureTree(assay, true)
        copyKeyToMeasureId(tree, measuresUsed)

        return tree;
    }

    public List createMeasureTree(Assay assay, boolean contextsAsChildren) {
        def roots = []

        Collection rootMeasures = assay.measures.findAll { it.parentMeasure == null}

        for (measure in rootMeasures) {
            roots.add(createTreeFromMeasure(measure, contextsAsChildren))
        }

        sortByKey(roots)
        return roots
    }

    public List createMeasureTree(Experiment experiment, boolean contextsAsChildren) {
        def roots = []

        for (m in experiment.experimentMeasures.findAll { it.parent == null}) {
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
        String title = experimentMeasure.measure.displayLabel
        String relationship = experimentMeasure.parentChildRelationship


        if (relationship) {
            title = "(${relationship}) ${title}"
        }

        Long measureId = experimentMeasure.measure.id

        def children = []

        for (m in experimentMeasure.childMeasures) {
            children.add(createTreeFromExperimentMeasure(m, contextsAsChildren))
        }

        if (contextsAsChildren) {
            for (m in experimentMeasure.measure.assayContextMeasures) {
                AssayContext context = m.assayContext
                children.add([key: "context-${context.id}", title: "Context: ${context.contextName}"])
            }
        }

        return [key: key, title: title, children: children, expand: true, relationship: relationship, measureId: measureId];
    }

    public Map createTreeFromMeasure(Measure measure, boolean contextsAsChildren) {
        def key = measure.id;
        String title = measure.displayLabel
        String relationship = measure.parentChildRelationship
        if (relationship) {
            title = "(${relationship}) ${title}"
        }
        def children = []

        for (m in measure.childrenMeasuresSorted) {
            children.add(createTreeFromMeasure(m, contextsAsChildren))
        }

        if (contextsAsChildren) {
            for (m in measure.assayContextMeasures) {
                AssayContext context = m.assayContext
                children.add([key: "context-${context.id}", title: "Context: ${context.contextName}", hideCheckbox: true, unselectable: true, icon: false])
            }
        }
        return [key: key, title: title, children: children, expand: true, relationship: relationship];
    }
}
