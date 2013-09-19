package bard.db.experiment

import bard.core.SearchParams
import bard.core.rest.spring.ExperimentRestService
import bard.core.rest.spring.experiment.ExperimentSearch
import bard.core.rest.spring.experiment.ExperimentSearchResult
import bard.db.enums.ExperimentStatus
import bard.db.enums.HierarchyType
import bard.db.enums.ReadyForExtraction
import bard.db.registration.Assay
import bard.db.registration.Measure
import org.apache.commons.lang.StringUtils
import org.codehaus.groovy.grails.web.json.JSONArray
import org.codehaus.groovy.grails.web.json.JSONObject
import org.springframework.security.access.prepost.PreAuthorize
import registration.AssayService


class ExperimentService {

    AssayService assayService;
    ExperimentRestService experimentRestService

    @PreAuthorize("hasPermission(#id, 'bard.db.experiment.Experiment', admin) or hasRole('ROLE_BARD_ADMINISTRATOR')")
    Experiment updateRunFromDate(final Long id, final Date runDateFrom) {
        Experiment experiment = Experiment.findById(id)
        experiment.runDateFrom = runDateFrom
        experiment.save(flush: true)
        return Experiment.findById(id)
    }

    @PreAuthorize("hasPermission(#id, 'bard.db.experiment.Experiment', admin) or hasRole('ROLE_BARD_ADMINISTRATOR')")
    Experiment updateRunToDate(final Long id, final Date runDateTo) {
        Experiment experiment = Experiment.findById(id)
        experiment.runDateTo = runDateTo
        experiment.save(flush: true)
        return Experiment.findById(id)
    }

    @PreAuthorize("hasPermission(#id, 'bard.db.experiment.Experiment', admin) or hasRole('ROLE_BARD_ADMINISTRATOR')")
    Experiment updateHoldUntilDate(final Long id, final Date newHoldUntilDate) {
        Experiment experiment = Experiment.findById(id)
        experiment.holdUntilDate = newHoldUntilDate
        experiment.save(flush: true)
        return Experiment.findById(id)
    }

    @PreAuthorize("hasPermission(#id, 'bard.db.experiment.Experiment', admin) or hasRole('ROLE_BARD_ADMINISTRATOR')")
    Experiment updateExperimentDescription(final Long id, final String newExperimentDescription) {
        Experiment experiment = Experiment.findById(id)
        experiment.description = newExperimentDescription

        experiment.save(flush: true)
        return Experiment.findById(id)
    }

    @PreAuthorize("hasPermission(#id, 'bard.db.experiment.Experiment', admin) or hasRole('ROLE_BARD_ADMINISTRATOR')")
    Experiment updateExperimentName(final Long id, final String newExperimentName) {
        Experiment experiment = Experiment.findById(id)
        experiment.experimentName = newExperimentName
        //validate version here
        experiment.save(flush: true)
        return Experiment.findById(id)
    }

    @PreAuthorize("hasPermission(#id, 'bard.db.experiment.Experiment', admin) or hasRole('ROLE_BARD_ADMINISTRATOR')")
    Experiment updateExperimentStatus(final Long id, final ExperimentStatus experimentStatus) {
        Experiment experiment = Experiment.findById(id)
        experiment.experimentStatus = experimentStatus
        experiment.save(flush: true)
        return Experiment.findById(id)
    }
    //TODO: This method is too long. Need to refactor
    @PreAuthorize("hasPermission(#experimentId, 'bard.db.experiment.Experiment', admin) or hasRole('ROLE_BARD_ADMINISTRATOR')")
    void updateMeasures(Long experimentId, JSONArray edges) {
        Experiment experiment = Experiment.findById(experimentId)
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
            measure.parentChildRelationship = null
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

    @PreAuthorize("hasPermission(#id, 'bard.db.registration.Assay', admin) or hasRole('ROLE_BARD_ADMINISTRATOR')")
    Experiment createNewExperiment(Long id, String experimentName, String description) {
        Assay assay = Assay.findById(id)
        Experiment experiment = new Experiment(assay: assay, experimentName: experimentName, description: description, dateCreated: new Date())

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
            if (child.measure.parentMeasure) {
                ExperimentMeasure parent = measureToExpMeasure[child.measure.parentMeasure]
                child.parentChildRelationship = HierarchyType.SUPPORTED_BY
                child.parent = parent
            }
        }

        experiment.experimentMeasures = new HashSet(measureToExpMeasure.values())
    }


    void validateExperimentsToMerge(Assay oldAssay, List<Experiment> experiments) {
        List<String> errorMessages = []
        for (Experiment experiment : experiments) {
            if (experiment?.assay?.id != oldAssay?.id) {
                errorMessages.add("Experiment EID: ${experiment?.id} , does not belong to Assay ADID: ${oldAssay?.id}")
            }
        }
        if (errorMessages) {
            throw new RuntimeException(StringUtils.join(errorMessages, ","))
        }
    }

    @PreAuthorize("hasRole('ROLE_BARD_ADMINISTRATOR')")
    Assay splitExperimentsFromAssay(Long assayId, List<Experiment> experiments) {
        Assay oldAssay = Assay.findById(assayId)

        validateExperimentsToMerge(oldAssay, experiments)

        return splitExperiments(oldAssay, experiments)
    }

    private Assay splitExperiments(Assay oldAssay, List<Experiment> experiments) {


        def mapping = assayService.cloneAssay(oldAssay)

        Assay newAssay = mapping.assay
        newAssay.fullyValidateContextItems = false

        Map<Measure, Measure> measureOldToNew = mapping.measureOldToNew


        for (Experiment experiment : experiments) {
            oldAssay.removeFromExperiments(experiment)
            newAssay.addToExperiments(experiment)

            // map measures over to new assay
            for (ExperimentMeasure experimentMeasure : experiment.experimentMeasures) {
                Measure oldMeasure = experimentMeasure.measure
                Measure newMeasure = measureOldToNew[oldMeasure]
                assert newMeasure != null

                oldMeasure.removeFromExperimentMeasures(experimentMeasure)
                newMeasure.addToExperimentMeasures(experimentMeasure)
            }
        }
        oldAssay.save(flush: true)

        newAssay = newAssay.save(flush: true)
        return Assay.findById(newAssay.id)
    }


    List<String> loadNCGCExperimentIds() {
        //look for experiments with an NCGC ID of null and Ready for extraction status of complete
        List<Long> capIds = Experiment.findAllByNcgcWarehouseIdIsNullAndReadyForExtraction(ReadyForExtraction.COMPLETE).collect { it.id }

        if (!capIds) {
            return
        }
        int start = 0
        final int NUMBER_OF_RECORDS_TO_PROCESS_PER_ROUND = 5
        int end = capIds.size() > NUMBER_OF_RECORDS_TO_PROCESS_PER_ROUND ? NUMBER_OF_RECORDS_TO_PROCESS_PER_ROUND : capIds.size()
        List<String> updateStatements = []
        List<Long> toProcess = capIds.subList(start, end)
        while (toProcess) {
            updateStatements.addAll(loadNCGCExperimentIdsFromList(toProcess))
            start = end
            end = end + NUMBER_OF_RECORDS_TO_PROCESS_PER_ROUND

            if (end > capIds.size()) {
                end = capIds.size()
            }
            if (start == end) {
                toProcess = []
            } else {
                toProcess = capIds.subList(start, end)
            }
        }

        return updateStatements
    }

    List<String> loadNCGCExperimentIdsFromList(List<Long> capExperimentIds) {
        if (!capExperimentIds) {
            return
        }
        List<String> updateStatements = []
        SearchParams searchParams = new SearchParams(skip: 0, top: capExperimentIds.size())

        try {
            ExperimentSearchResult experimentSearchResult = experimentRestService.searchExperimentsByCapIds(capExperimentIds, searchParams, false)
            if (experimentSearchResult) {
                final List<ExperimentSearch> experiments = experimentSearchResult.experiments
                if (experiments) {
                    for (ExperimentSearch experimentSearch : experiments) {
                        if (experimentSearch) {
                            //batch update or do it one by one?
                            final Experiment experiment = Experiment.get(experimentSearch.capExptId)
                            if (experiment) {
                                updateStatements.add("UPDATE EXPERIMENT SET NCGC_WAREHOUSE_ID=${experimentSearch.bardExptId} WHERE EXPERIMENT_ID=${experimentSearch.capExptId};")
                                experiment.ncgcWarehouseId = experimentSearch.bardExptId
                                experiment.save(flush: true)
                            } else {
                                log.error("Could not find Experiment with id ${experimentSearch.capExptId}")
                            }
                        }
                    }
                } else {
                    log.error("The following Cap Experiment Ids ${capExperimentIds} could not be found in the warehouse")
                }
            } else {
                log.error("The following Cap Experiment Ids ${capExperimentIds} could not be found in the warehouse")
            }
        } catch (Exception ee) {
            log.error("Error From loadNCGCExperimentIdsFromList " + ee.message)
            ee.stackTrace
        }
        return updateStatements
    }

    public static int getTotalLengthRec2(final List<String> lst) {
        return getTotalLengthRecHelper(lst, 0, lst.size());
    }

    public static int getTotalLengthRecHelper(final List<String> lst, final int from, final int to) {
        if (to - from <= 1)
            return lst.get(from).length();
        final int middle = (from + to) >> 1;
        return getTotalLengthRecHelper(lst, from, middle) + getTotalLengthRecHelper(lst, middle, to);
    }

}
