package bard.db.registration

import bard.db.experiment.Experiment
import bard.db.experiment.ExperimentMeasure

class MeasureTreeService {
    public List createMeasureTree(Assay assay, boolean contextsAsChildren) {
        def roots = []

        Collection rootMeasures = assay.measures.findAll { it.parentMeasure == null}

        for(measure in rootMeasures) {
            roots.add(createTreeFromMeasure(measure, contextsAsChildren))
        }

        sortByKey(roots)
        return roots
    }

    public List createMeasureTree(Experiment experiment, boolean contextsAsChildren) {
        def roots = []

        for(m in experiment.experimentMeasures.findAll { it.parent == null} ) {
            roots << createTreeFromExperimentMeasure(m, contextsAsChildren)
        }

        sortByKey(roots)
        return roots
    }

    public void sortByKey(List<Map> children) {
        for(c in children) {
            if (c.containsKey("children"))
                sortByKey(c["children"])
        }

        children.sort { Map a, Map b -> a["title"].toLowerCase().compareTo(b["title"].toLowerCase()) }
    }

    public Map createTreeFromExperimentMeasure(ExperimentMeasure experimentMeasure, boolean contextsAsChildren) {
        def key = experimentMeasure.id;
        def title = experimentMeasure.measure.displayLabel

        def children = []

        for(m in experimentMeasure.childMeasures) {
            children.add(createTreeFromExperimentMeasure(m, contextsAsChildren))
        }

        if (contextsAsChildren) {
            for(m in experimentMeasure.measure.assayContextMeasures) {
                AssayContext context = m.assayContext
                children.add([key: "context-${context.id}", title: "Context: ${context.contextName}"])
            }
        }

        def map = [key: key, title: title, children: children, expand: true];
    }

    public Map createTreeFromMeasure(Measure measure, boolean contextsAsChildren) {
        def key = measure.id;
        def title = measure.displayLabel

        def children = []

        for(m in measure.childrenMeasuresSorted) {
            children.add(createTreeFromMeasure(m, contextsAsChildren))
        }

        if (contextsAsChildren) {
            for(m in measure.assayContextMeasures) {
                AssayContext context = m.assayContext
                children.add([key: "context-${context.id}", title: "Context: ${context.contextName}", hideCheckbox: true, unselectable: true, icon: false])
            }
        }

        return [key: key, title: title, children: children, expand: true];
    }
}