package bard.db.experiment

import bard.db.dictionary.Element
import bard.db.experiment.Experiment
import bard.db.experiment.ExperimentMeasure
import bard.db.registration.Assay
import bard.db.registration.AssayContext
import bard.db.registration.AssayContextMeasure
import bard.db.registration.Measure
import org.codehaus.groovy.grails.web.json.JSONArray
import org.codehaus.groovy.grails.web.json.JSONObject
import bard.db.enums.HierarchyType
import org.apache.commons.lang.StringUtils

class ExperimentService {

    void updateMeasures(Experiment experiment, JSONArray edges) {
        // populate map with ids as strings
        Map byId = [:]
        experiment.experimentMeasures.each {
            byId[it.id.toString()] = it;
        }

        // keep track of experiment measures which are no longer in use
        Set unused = new HashSet(experiment.experimentMeasures)

        // clear relationships between measures
        unused.each {ExperimentMeasure measure ->
            measure.childMeasures = new HashSet();
            measure.parent = null;
        }

        // start over fresh and walk through the elements in tree
        experiment.experimentMeasures = new HashSet()

        for(int i=0;i<edges.length();i++) {
            JSONObject record = edges.getJSONObject(i);
            String id = record.getString("id");
            Long measureId = record.getLong("measureId");

            ExperimentMeasure experimentMeasure;
            if (id.startsWith("new-")){
                Measure measure = Measure.get(measureId);
                experimentMeasure = new ExperimentMeasure(experiment: experiment, measure: measure, dateCreated: new Date());
                experimentMeasure.save()
                byId[id] = experimentMeasure;
            } else {
                experimentMeasure = byId[id];
                if (experimentMeasure == null) {
                    throw new RuntimeException("Could not find measure with id "+id);
                }
                unused.remove(experimentMeasure);
            }
            experiment.experimentMeasures.add(experimentMeasure);
        }

        // at this point, we've got all of the experimentMeasures indexable by ids
        // so re-walk the list of edges and assign parent child relationships
        for(int i=0;i<edges.length();i++) {
            JSONObject record = edges.getJSONObject(i);
            String id = record.getString("id");
            String parentId = null;
            if (!record.isNull("parentId"))
                parentId = record.getString("parentId");

            String relationship = null;
            HierarchyType hierarchyType = null;
            if (record.has("relationship")){
                relationship = record.getString("relationship");
            }

            ExperimentMeasure child = byId[id];
            ExperimentMeasure parent = null;
            if (parentId != null)
                parent = byId[parentId];

            child.parent = parent;
            if (parent != null) {
                parent.addToChildMeasures(child);
            }
            if(StringUtils.isNotBlank(relationship) && relationship != 'null'){
              hierarchyType = HierarchyType.byId(relationship);
            }
            child.parentChildRelationship = hierarchyType;
        }

        // now get rid of the unused measures
        unused.each {ExperimentMeasure measure ->
            measure.delete();
        }

        println("Added ${experiment.experimentMeasures.size()} measures to tree")
    }

    Experiment createNewExperiment(Assay assay, String experimentName, String description) {
        Experiment experiment = new Experiment(assay: assay)
        experiment.experimentName = experimentName
        experiment.description = description
        experiment.dateCreated = new Date()
        if (experiment.save(flush: true)) {
            populateMeasures(experiment)
        }

        return experiment
    }

    void populateMeasures(Experiment experiment) {
        Map measureToExpMeasure = [:]

        experiment.assay.measures.each { Measure measure ->
            ExperimentMeasure expMeasure = new ExperimentMeasure(experiment: experiment, measure: measure, dateCreated: new Date())
            measureToExpMeasure[measure] = expMeasure
        }

        measureToExpMeasure.values().each { ExperimentMeasure child ->
            if (child.measure.parentMeasure != null) {
                ExperimentMeasure parent = measureToExpMeasure[child.measure.parentMeasure]
                child.parentChildRelationship = HierarchyType.SUPPORTED_BY
                child.parent = parent
            }
        }

        experiment.experimentMeasures = new HashSet(measureToExpMeasure.values())
    }

    Map cloneAssay(Assay assay) {
        Map<AssayContext, AssayContext> assayContextOldToNew = [:]

        Assay newAssay = new Assay();
        for(context in assay.assayContexts) {
            AssayContext newContext = context.clone(newAssay)
            assayContextOldToNew[context] = newContext

            newContext.save()
        }

        // clone all measures
        Map<Measure, Measure> measureOldToNew = [:]
        for(measure in assay.measures) {
            Measure newMeasure = new Measure(
                    assay: newAssay,
                    resultType: measure.resultType,
                    parentChildRelationship: measure.parentChildRelationship,
                    entryUnit: measure.entryUnit,
                    statsModifier: measure.statsModifier,
                    dateCreated: new Date())
            measureOldToNew[measure] = newMeasure
        }

        // assign parent measures now that all measures have been created
        for(measure in assay.measures) {
            measureOldToNew[measure].parentMeasure = measureOldToNew[measure.parentMeasure]
        }

        for(measure in measureOldToNew.values()) {
            measure.save()
        }

        // clone assay context measures
        Set<AssayContextMeasure> assayContextMeasures = assay.measures.collectAll { it.assayContextMeasures }
        for(assayContextMeasure in assayContextMeasures) {
            AssayContextMeasure newAssayContextMeasure = new AssayContextMeasure(
                    assayContext: assayContextOldToNew[assayContextMeasure.assayContext],
                    measure: measureOldToNew[assayContextMeasure.measure])
            newAssayContextMeasure.save()
        }

        return [assay: newAssay, measureOldToNew: measureOldToNew]
    }

    void splitExperimentFromAssay(Experiment experiment) {

    }
}
