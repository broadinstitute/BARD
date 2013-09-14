package bard.db.registration

import bard.db.dictionary.Element
import bard.db.enums.HierarchyType
import bard.db.experiment.AssayContextExperimentMeasure
import bard.db.experiment.Experiment
import bard.db.experiment.ExperimentMeasure
import org.springframework.security.access.prepost.PreAuthorize

/**
 * Created with IntelliJ IDEA.
 * User: ddurkin
 * Date: 8/31/12
 * Time: 3:36 PM
 * To change this template use File | Settings | File Templates.
 */
class AssayContextService {

    @PreAuthorize("hasPermission(#id, 'bard.db.registration.Assay', admin) or hasRole('ROLE_BARD_ADMINISTRATOR')")
    public AssayContext addItem(AssayContextItem sourceItem, AssayContext targetAssayContext, Long id) {
        if (sourceItem && sourceItem.assayContext != targetAssayContext) {
            return addItem(targetAssayContext.assayContextItems.size(), sourceItem, targetAssayContext)
        }
    }
    /**
     * This should not be called outside of this service. Making protected so we can test
     * @param index
     * @param sourceItem
     * @param targetAssayContext
     * @return
     */
    protected AssayContext addItem(int index, AssayContextItem sourceItem, AssayContext targetAssayContext) {
        AssayContext sourceAssayContext = sourceItem.assayContext
        sourceAssayContext.removeFromAssayContextItems(sourceItem)
        sourceItem.assayContext = targetAssayContext
        targetAssayContext.assayContextItems.add(index, sourceItem)
        return targetAssayContext
    }

    @PreAuthorize("hasPermission(#id, 'bard.db.registration.Assay', admin) or hasRole('ROLE_BARD_ADMINISTRATOR')")
    public AssayContext updateCardName(Long assayContextId, String name, Long id) {
        AssayContext assayContext = AssayContext.get(assayContextId)
        if (assayContext && assayContext.contextName != name) {
            assayContext.contextName = name
        }
        return assayContext
    }

    @PreAuthorize("hasPermission(#id,'bard.db.registration.Assay',admin) or hasRole('ROLE_BARD_ADMINISTRATOR')")
    public ExperimentMeasure changeParentChildRelationship(ExperimentMeasure experimentMeasure, HierarchyType hierarchyType, Long id) {
        experimentMeasure.parentChildRelationship = hierarchyType
        experimentMeasure.save(flush: true)
        return experimentMeasure
    }
    @Deprecated
    @PreAuthorize("hasPermission(#id,'bard.db.registration.Assay',admin) or hasRole('ROLE_BARD_ADMINISTRATOR')")
    public void associateContext(ExperimentMeasure experimentMeasure, AssayContext context, Long id) {
        throw new RuntimeException("Needs rework")
       /* AssayContextExperimentMeasure assayContextExperimentMeasure = new AssayContextExperimentMeasure();
        assayContextExperimentMeasure.experimentMeasure = experimentMeasure;
        assayContextExperimentMeasure.assayContext = context;
        experimentMeasure.assayContextExperimentMeasures.add(assayContextExperimentMeasure)
        context.assayContextExperimentMeasures.add(assayContextExperimentMeasure) */
    }
    @PreAuthorize("hasPermission(#id,'bard.db.registration.Assay',admin) or hasRole('ROLE_BARD_ADMINISTRATOR')")
    public void associateExperimentContext(ExperimentMeasure experimentMeasure, AssayContext context, Long id) {
        AssayContextExperimentMeasure assayContextExperimentMeasure = new AssayContextExperimentMeasure();
        assayContextExperimentMeasure.experimentMeasure = experimentMeasure;
        assayContextExperimentMeasure.assayContext = context;
        experimentMeasure.assayContextExperimentMeasures.add(assayContextExperimentMeasure)
        context.assayContextExperimentMeasures.add(assayContextExperimentMeasure)
    }
    @Deprecated
    @PreAuthorize("hasPermission(#id, 'bard.db.registration.Assay', admin) or hasRole('ROLE_BARD_ADMINISTRATOR')")
    public boolean disassociateContext(ExperimentMeasure experimentMeasure, AssayContext context, Long id) {
        throw new RuntimeException("Needs rework")
//        AssayContextExperimentMeasure found = null;
//        for (assayContextExperimentMeasure in context.assayContextExperimentMeasures) {
//            if (assayContextExperimentMeasure.experimentMeasure == experimentMeasure && assayContextExperimentMeasure.assayContext) {
//                found = assayContextExperimentMeasure;
//                break;
//            }
//        }
//
//        if (found == null) {
//            return false;
//        } else {
//            experimentMeasure.removeFromAssayContextExperimentMeasures(found)
//            context.removeFromAssayContextExperimentMeasures(found)
//            found.delete(flush: true)
//            return true;
//        }
    }

    @PreAuthorize("hasPermission(#id, 'bard.db.registration.Assay', admin) or hasRole('ROLE_BARD_ADMINISTRATOR')")
    public boolean disassociateAssayContext(ExperimentMeasure experimentMeasure, AssayContext context, Long id) {
        AssayContextExperimentMeasure found = null;
        for (assayContextExperimentMeasure in context.assayContextExperimentMeasures) {
            if (assayContextExperimentMeasure.experimentMeasure == experimentMeasure && assayContextExperimentMeasure.assayContext) {
                found = assayContextExperimentMeasure;
                break;
            }
        }

        if (found == null) {
            return false;
        } else {
            experimentMeasure.removeFromAssayContextExperimentMeasures(found)
            context.removeFromAssayContextExperimentMeasures(found)
            found.delete(flush: true)
            return true;
        }
    }

    @PreAuthorize("hasPermission(#id, 'bard.db.registration.Assay', admin) or hasRole('ROLE_BARD_ADMINISTRATOR')")
    @Deprecated
    def addMeasure(Long id, ExperimentMeasure parentMeasure, Element resultType, Element statsModifier, Element entryUnit, HierarchyType hierarchyType) {
        throw new RuntimeException("Needs rework");
       /* Assay assay = Assay.findById(id)
        Measure measure = Measure.findByAssayAndResultTypeAndStatsModifierAndParentMeasure(assay, resultType, statsModifier, parentMeasure)
        if (measure) {
            //we need to throw an exception here
            throw new RuntimeException("Duplicate measures cannot be added to the same assay")
        }

        measure =
            new Measure(assay: assay, resultType: resultType, statsModifier: statsModifier,
                    entryUnit: entryUnit, parentMeasure: parentMeasure, parentChildRelationship: hierarchyType);
        assay.addToMeasures(measure)
        measure.save()

        return measure*/
    }

    @PreAuthorize("hasPermission(#id, 'bard.db.experiment.Experiment', admin) or hasRole('ROLE_BARD_ADMINISTRATOR')")
    public ExperimentMeasure addExperimentMeasure(Long id, ExperimentMeasure parentMeasure, Element resultType, Element statsModifier, Element entryUnit, HierarchyType hierarchyType) {
        Experiment experiment = Experiment.findById(id)
        ExperimentMeasure experimentMeasure = ExperimentMeasure.findByExperimentAndResultTypeAndStatsModifierAndParent(experiment, resultType, statsModifier, parentMeasure)
        if (experimentMeasure) {
            //we need to throw an exception here
            throw new RuntimeException("Duplicate measures cannot be added to the same assay")
        }

        experimentMeasure =
            new ExperimentMeasure(experiment: experiment, resultType: resultType, statsModifier: statsModifier,
                    entryUnit: entryUnit, parent: parentMeasure, parentChildRelationship: hierarchyType);
        experiment.addToExperimentMeasures(experimentMeasure)
        experimentMeasure.save()

        return experimentMeasure
    }
}