package bard.db.registration

import bard.db.enums.HierarchyType
import bard.db.experiment.Experiment
import bard.db.experiment.ExperimentMeasure

class MeasureTreeService {



    public List createMeasureTree(Experiment experiment, String username) {
        def roots = []

        for (m in experiment.experimentMeasures.findAll { it.parent == null }) {
            roots << createTreeFromExperimentMeasure(m, username)
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

    public Map createTreeFromExperimentMeasure(ExperimentMeasure experimentMeasure, String username) {
        def key = experimentMeasure.id;
        String title = experimentMeasure.displayLabel
        HierarchyType relationship = experimentMeasure.parentChildRelationship


        if (relationship) {
            title = "(${relationship.id}) ${title}"
        }

        Long measureId = experimentMeasure.id

        def children = []

        for (m in experimentMeasure.childMeasures) {
            children.add(createTreeFromExperimentMeasure(m, username))
        }
        return [key: key, title: title, children: children, expand: true, relationship: relationship?.id, measureId: measureId, addClass: experimentMeasure.priorityElement ? "priority" : "", username: username?:""];
    }
}
