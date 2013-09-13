package bard.db.registration

import bard.db.dictionary.Element
import bard.db.enums.HierarchyType
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
    public Measure changeParentChildRelationship(Measure measure, HierarchyType hierarchyType, Long id) {
        measure.parentChildRelationship = hierarchyType
        measure.save(flush: true)
        return measure
    }

    @PreAuthorize("hasPermission(#id,'bard.db.registration.Assay',admin) or hasRole('ROLE_BARD_ADMINISTRATOR')")
    public void associateContext(Measure measure, AssayContext context, Long id) {
        AssayContextMeasure assayContextMeasure = new AssayContextMeasure();
        assayContextMeasure.measure = measure;
        assayContextMeasure.assayContext = context;
        measure.assayContextMeasures.add(assayContextMeasure)
        context.assayContextMeasures.add(assayContextMeasure)
    }

    @PreAuthorize("hasPermission(#id, 'bard.db.registration.Assay', admin) or hasRole('ROLE_BARD_ADMINISTRATOR')")
    public boolean disassociateContext(Measure measure, AssayContext context, Long id) {
        AssayContextMeasure found = null;
        for (assayContextMeasure in context.assayContextMeasures) {
            if (assayContextMeasure.measure == measure && assayContextMeasure.assayContext) {
                found = assayContextMeasure;
                break;
            }
        }

        if (found == null) {
            return false;
        } else {
            measure.removeFromAssayContextMeasures(found)
            context.removeFromAssayContextMeasures(found)
            found.delete(flush: true)
            return true;
        }
    }

    @PreAuthorize("hasPermission(#id, 'bard.db.registration.Assay', admin) or hasRole('ROLE_BARD_ADMINISTRATOR')")
    public Measure addMeasure(Long id, Measure parentMeasure, Element resultType, Element statsModifier, Element entryUnit, HierarchyType hierarchyType) {
        Assay assay = Assay.findById(id)
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

        return measure
    }
}