package bard.db.experiment

import bard.db.enums.ExperimentStatus
import bard.db.registration.Assay
import bard.db.registration.Measure
import org.codehaus.groovy.grails.web.json.JSONArray
import org.codehaus.groovy.grails.web.json.JSONObject
import bard.db.enums.HierarchyType
import org.apache.commons.lang.StringUtils
import registration.AssayService

class ExperimentService {

    AssayService assayService;
    Experiment updateRunFromDate(final Long experimentId, final Date runDateFrom){
        Experiment experiment = Experiment.findById(experimentId)
        experiment.runDateFrom = runDateFrom
        experiment.save(flush: true)
        return Experiment.findById(experimentId)
    }
    Experiment updateRunToDate(final Long experimentId, final Date runDateTo){
        Experiment experiment = Experiment.findById(experimentId)
        experiment.runDateTo = runDateTo
        experiment.save(flush: true)
        return Experiment.findById(experimentId)
    }
    Experiment updateHoldUntilDate(final Long experimentId, final Date newHoldUntilDate) {
        Experiment experiment = Experiment.findById(experimentId)
        experiment.holdUntilDate = newHoldUntilDate
        experiment.save(flush: true)
        return Experiment.findById(experimentId)
    }
    Experiment updateExperimentDescription(final Long experimentId, final String newExperimentDescription) {
        Experiment experiment = Experiment.findById(experimentId)
        experiment.description = newExperimentDescription

        experiment.save(flush: true)
        return Experiment.findById(experimentId)
    }
    Experiment updateExperimentName(final Long experimentId, final String newExperimentName) {
        Experiment experiment = Experiment.findById(experimentId)
        experiment.experimentName = newExperimentName
        //validate version here
        experiment.save(flush: true)
        return Experiment.findById(experimentId)
    }

    Experiment updateExperimentStatus(final Long experimentId, final ExperimentStatus experimentStatus) {
        Experiment experiment = Experiment.findById(experimentId)
        experiment.experimentStatus = experimentStatus
        experiment.save(flush: true)
        return Experiment.findById(experimentId)
    }

    void updateMeasures(Experiment experiment, JSONArray edges) {
        // populate map with ids as strings
        Map byId = [:]
        experiment.experimentMeasures.each {
            byId[it.id.toString()] = it;
        }

        // keep track of experiment measures which are no longer in use
        Set unused = new HashSet(experiment.experimentMeasures)

        // clear relationships between measures
        unused.each { ExperimentMeasure measure ->
            measure.childMeasures = new HashSet();
            measure.parent = null;
            measure.parentChildRelationship=null
        }

        // start over fresh and walk through the elements in tree
        experiment.experimentMeasures = new HashSet()

        for (int i = 0; i < edges.length(); i++) {
            JSONObject record = edges.getJSONObject(i);
            String id = record.getString("id");
            Long measureId = record.getLong("measureId");

            ExperimentMeasure experimentMeasure;
            if (id.startsWith("new-")) {
                Measure measure = Measure.get(measureId);
                experimentMeasure = new ExperimentMeasure(experiment: experiment, measure: measure, dateCreated: new Date());
                experimentMeasure.save()
                byId[id] = experimentMeasure;
            } else {
                experimentMeasure = byId[id];
                if (experimentMeasure == null) {
                    throw new RuntimeException("Could not find measure with id " + id);
                }
                unused.remove(experimentMeasure);
            }
            experiment.experimentMeasures.add(experimentMeasure);
        }

        // at this point, we've got all of the experimentMeasures indexable by ids
        // so re-walk the list of edges and assign parent child relationships
        for (int i = 0; i < edges.length(); i++) {
            JSONObject record = edges.getJSONObject(i);
            String id = record.getString("id");
            String parentId = null;
            if (!record.isNull("parentId"))
                parentId = record.getString("parentId");

            String relationship = null;
            HierarchyType hierarchyType = null;
            if (record.has("relationship")) {
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
            if (StringUtils.isNotBlank(relationship) && relationship != 'null') {
                hierarchyType = HierarchyType.byId(relationship);
            }
            child.parentChildRelationship = hierarchyType;
        }

        // now get rid of the unused measures
        unused.each { ExperimentMeasure measure ->
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

    void splitExperimentsFromAssay(List<Experiment> experiments) {
        Assay oldAssay = experiments.first().assay

        def mapping = assayService.cloneAssay(oldAssay)

        Assay newAssay = mapping.assay
        Map<Measure, Measure> measureOldToNew = mapping.measureOldToNew

        for (experiment in experiments) {
            oldAssay.removeFromExperiments(experiment)
            newAssay.addToExperiments(experiment)

            // map measures over to new assay
            for (experimentMeasure in experiment.experimentMeasures) {
                Measure oldMeasure = experimentMeasure.measure
                Measure newMeasure = measureOldToNew[oldMeasure]
                assert newMeasure != null

                oldMeasure.removeFromExperimentMeasures(experimentMeasure)
                newMeasure.addToExperimentMeasures(experimentMeasure)
            }
        }
    }
}
