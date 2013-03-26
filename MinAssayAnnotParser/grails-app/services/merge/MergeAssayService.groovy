package merge

import bard.db.registration.Assay
import bard.db.registration.AssayContextItem
import bard.db.registration.AttributeType
import org.apache.commons.lang3.StringUtils
import bard.db.registration.AssayDocument
import bard.db.registration.Measure
import bard.db.experiment.Experiment
import bard.db.registration.AssayContext
import bard.db.experiment.ExperimentContext
import bard.db.experiment.ExperimentContextItem
import bard.db.model.AbstractContext
import bard.db.model.AbstractContextItem
import groovy.sql.Sql
import bard.db.experiment.ExperimentMeasure
import bard.db.registration.AssayContextMeasure
import bard.db.model.AbstractDocument
import org.apache.commons.lang3.builder.EqualsBuilder
import org.apache.commons.math3.util.Precision
import bard.db.enums.AssayStatus

class MergeAssayService {

    /**
     * This is the assay we want to keep
     * @param assays
     * @return the assay we want to keep
     */
    private Assay keepAssay(List<Assay> assays) {
        Assay assay = assays.max {  // keep assay that having the largest number of contextItems
            it.assayContextItems.size()
        }
        println("Assay ${assay.id} has max # of contextItem ${assay.assayContextItems.size()}, keep it")
        return assay
    }

    private List<Assay> assaysNeedToRemove(List<Assay> assays, Assay assayWillKeep) {
        List<Assay> removingAssays = []
        assays.each {
            if (it.id != assayWillKeep.id)
                removingAssays.add(it)
        }
        return removingAssays
    }

    // assay context items:  identify assay_context_items that do not occur in all the removingAssays
    // 1.  if the assay_context_item does not occur in the single kept assay, add it but change it to AttributeType "list"
    // 2.  if there is an assay_context_item in the single kept assay that has the same attribute, but a different value,
    //     change it to a AttributeType to list and add the value from the duplicate assay to the List
    // 3.  for an experiment associated with an assay that has an assay_context_item that is part of one of these new lists,
    //     add an experiment_context_item that corresponds the attribute with a value as specified in the original assay

    def mergeAssayContextItem(List<Assay> removingAssays, Assay assayWillKeep, String modifiedBy) {
        List<AssayContextItem> candidateContextItems = []
        removingAssays.each {
            candidateContextItems.addAll(it.assayContextItems)
        }

        // there are maybe new contexts not in assayWillKeep, make sure we added context first
        List<AssayContext> candidateContexts = []
        removingAssays.each {
            candidateContexts.addAll(it.assayContexts)
        }

        // Add missing context item
        int assayContextItemInKeep = 0  // count number of assaycontextitem in kept assay
        int assayContextItemInKeepWithDifferentValue = 0 // count number of assaycontextitem in kept assay with same attribute,
        // but different value
        int assayContextItemNotInKeep = 0 // count number of assaycontextitem not in kept assay
        for (AssayContextItem item : candidateContextItems) {
            if (!item)
                continue
            if (isAssayContextItemIn(assayWillKeep.assayContextItems, item)) {
                assayContextItemInKeep++
                continue
            }
            AssayContextItem second = assayWillKeep.assayContextItems.find {it.attributeElement == item.attributeElement && it.valueElement != item.valueElement}
            if (second) {
                assayContextItemInKeepWithDifferentValue++
                createExperimentContextAndItem(item.assayContext, modifiedBy)
                createExperimentContextAndItem(second.assayContext, modifiedBy)
                // add value
                item.modifiedBy = modifiedBy + "-movedFromA-${item.assayContext.assay.id}"
                item.assayContext.removeFromAssayContextItems(item)
                item.assayContext = second.assayContext
                second.assayContext.addToAssayContextItems(item)
                //item.attributeType = AttributeType.List
                second.attributeType = AttributeType.List
            }
            else {
                assayContextItemNotInKeep++
                // At the point we should be able to find a corresponding context
                AssayContext context = assayWillKeep.assayContexts.find {
                    item.assayContext.contextName == it.contextName && item.assayContext.contextGroup == it.contextGroup
                }
                if (!context) {
                    // Add assay context to assay that will be kept if there is no one exist
                    int assayContextNotInKeep = 0 // count number of assayContext not seeing in kept assay
                    context = candidateContexts.find {
                        item.assayContext.contextName == it.contextName && item.assayContext.contextGroup == it.contextGroup
                    }

                    if (context) {
                        assayContextNotInKeep++
                        context.modifiedBy = modifiedBy + "-addedFromA-${context.assay.id}"
                        context.assay.removeFromAssayContexts(context)
                        context.assay = assayWillKeep
                        assayWillKeep.addToAssayContexts(context)
                    }

                }


                if (context) {
                    item.modifiedBy = modifiedBy + "-movedFromA-${item.assayContext.assay.id}"
                    item.assayContext.removeFromAssayContextItems(item)
                    context.addToAssayContextItems(item)
                    item.assayContext = context
                }
                item.attributeType = AttributeType.List
            }
        }
        println("""Total candidate candidateContextItems: ${candidateContextItems.size()},
                    assaycontextitem # in keep assay ${assayContextItemInKeep},
                    assaycontextitem # in keep assay with different value ${assayContextItemInKeepWithDifferentValue},
                    assaycontextitem # not in keep assay  ${assayContextItemNotInKeep}""")
        assayWillKeep.save()
        // Assay.findById(assayWillKeep.id)
    }

    private void createExperimentContextAndItem(AssayContext assayContext, String modifiedBy) {
        for (Experiment experiment : assayContext.assay.experiments) {
            def foundExperimentContext = experiment.experimentContexts.find {it?.contextName == assayContext.contextName && it?.contextGroup == assayContext.contextGroup}
            if (!foundExperimentContext) {
                foundExperimentContext = new ExperimentContext(contextName: assayContext?.contextName, contextGroup: assayContext?.contextGroup, experiment: experiment)
                experiment.addToExperimentContexts(foundExperimentContext)
                foundExperimentContext.experiment = experiment
                foundExperimentContext.modifiedBy = modifiedBy + "-addedFromACI-${assayContext.id}"
                if (!foundExperimentContext.save()) {
                    println(foundExperimentContext.errors)
                }
            }

            for (AssayContextItem assayContextItem : assayContext.assayContextItems) {
                if (!isContextItemExist(foundExperimentContext, assayContextItem) && assayContextItem.attributeType == AttributeType.Fixed) {  // create an explicit experiment context for fixed
                    ExperimentContextItem newExperimentContextItem = createExperimentContextItem(assayContextItem)
                    foundExperimentContext.addToExperimentContextItems(newExperimentContextItem)
                    newExperimentContextItem.experimentContext = foundExperimentContext
                    newExperimentContextItem.modifiedBy = modifiedBy + "-addedFromACI-${assayContextItem.id}"
                    newExperimentContextItem.save()
                }
            }
        }
    }

    private ExperimentContextItem createExperimentContextItem(AssayContextItem assayContextItem) {
        def newExperimentContextItem = new ExperimentContextItem(attributeElement: assayContextItem.attributeElement,
                valueElement: assayContextItem.valueElement,
                extValueId: assayContextItem.extValueId,
                qualifier: assayContextItem.qualifier,
                valueNum: assayContextItem.valueNum,
                valueMin: assayContextItem.valueMin,
                valueMax: assayContextItem.valueMax,
                valueDisplay: assayContextItem.valueDisplay
        )
        return newExperimentContextItem
    }

    //experiments:  change the experiments associated with all the removingAssays in the duplicate set so that they point to the single kept  assay
    def handleExperiments(List<Assay> removingAssays, Assay assayWillKeep, String modifiedBy) {

        List<Experiment> experiments = []
        removingAssays.each {Assay assay ->
            experiments.addAll(assay.experiments)
        }
        int addExperimentToKept = 0 // count number of experiments added to kept assays
        experiments.each {Experiment experiment ->
            Experiment found = assayWillKeep.experiments.find {it.id == experiment.id}
            if (!found) {
                addExperimentToKept++
                experiment.modifiedBy = modifiedBy + "-movedFromA-${experiment?.assay?.id}"
                experiment.assay.removeFromExperiments(experiment)

                experiment.assay = assayWillKeep
                assayWillKeep.addToExperiments(experiment)
            }
        }
        println("Total candidate experiments: ${experiments.size()}, added ${addExperimentToKept}")
        assayWillKeep.save()
        // Assay.findById(assayWillKeep.id)
    }

    //assay documents:  check for exact string matches, copy over everything that does not match
    def handleDocuments(List<Assay> removingAssays, Assay assayWillKeep, String modifiedBy) {
        List<AssayDocument> docs = []
        removingAssays.each {Assay assay ->
            docs.addAll(assay.documents)
        }
        int addDocsToKeep = 0 // count number of documents added to kept assay
        docs.each {AssayDocument doc ->
            if (!isDocumentInAssay(doc, assayWillKeep)) {
                addDocsToKeep++
                doc.modifiedBy = modifiedBy + "-movedFromA-${doc.assay.id}"
                doc.assay.removeFromAssayDocuments(doc)

                assayWillKeep.addToAssayDocuments(doc)
                doc.assay = assayWillKeep
            }
        }
        println("Total candidate documents: ${docs.size()}, added ${addDocsToKeep}")
        // assayWillKeep.save()
        Assay.findById(assayWillKeep.id)
    }

    // Measures:  keep the measures that are the unique set of all the measures in the duplicate set of removingAssays.
    // delete the measures that are duplicates (and the assay-measures)
    def handleMeasure(List<Assay> removingAssays, Assay assayWillKeep, String modifiedBy) {
        assayWillKeep = Assay.findById(assayWillKeep.id)
        List<Measure> measures = []
        removingAssays.each {Assay assay ->
            measures.addAll(assay.measures)
        }
        int addMeasureToKeep = 0 // count number of measures added to assay
        int addMeasureToExperimentInKeep = 0  //count number of measures added to experiments
        int deletedMeasure = 0 // count number of measures deleted
        def deleteMeasures = []
        def measureMap = [:]     // map to keep id and found measure
        for (Measure measure : measures) {
            Measure found = assayWillKeep.measures.find{it.id == measure.id}
            if (found)
                continue
            found = assayWillKeep.measures.find {
                (it.resultType == measure.resultType) &&
                         (it.statsModifier == measure.statsModifier ) //&&
                         //(it.parentMeasure == measure.parentMeasure )
            }

            if (!found) {
                measure.modifiedBy = modifiedBy + "-movedFromA-${measure.assay.id}"
                addMeasureToKeep++
                measure.assay.removeFromMeasures(measure)
                assayWillKeep.addToMeasures(measure)
                measure.assay = assayWillKeep
                measure.assayContextMeasures.each {it.assayContext.assay = assayWillKeep}

            } else if (measure.assayContextMeasures.size() != 0) {
                measure.assayContextMeasures.each {
                    it.assayContext.assay = assayWillKeep
                    found.addToAssayContextMeasures(it)
                    //it.assayContext.removeFromAssayContextMeasures(it)
                }
                measureMap.put(measure.id, found)
               // deleteMeasures.add(measure)
            } else {
               // deleteMeasures.add(measure)
                measureMap.put(measure.id, found)
            }
        }

        for (Measure measure : deleteMeasures) {
            for (ExperimentMeasure experimentMeasure : measure.experimentMeasures) {
                measure.removeFromExperimentMeasures(experimentMeasure)
                experimentMeasure.measure = null
            }
            measure.assay.removeFromMeasures(measure)
            measure.assay = null
            measure.delete()
        }


        for (Experiment experiment : assayWillKeep.experiments) {
            for (ExperimentMeasure experimentMeasure : experiment.experimentMeasures) {
                if (!experimentMeasure.measure) continue
                if (measureMap.containsKey(experimentMeasure.measure.id)) {
                    experimentMeasure.measure = measureMap.get(experimentMeasure.measure.id)
                }
            }
        }


        println("Total candidate measure: ${measures.size()}, added to assay ${addMeasureToKeep}, delete ${deletedMeasure}, add to experiment ${addMeasureToExperimentInKeep}")
        assayWillKeep.save()
        // Assay.findById(assayWillKeep.id)
    }

    def updateStatus(List<Assay> assays, String modifiedBy) {
        assays.each{ Assay assay->
            assay.assayStatus = AssayStatus.RETIRED
            assay.modifiedBy = modifiedBy
            if (!assay.save()){
                println("Error happened when update assay status ${assay.errors}")
            }
        }
    }

    def ExperimentMeasure isMeasureInExperiments(Measure measure, Collection<Experiment> experiments) {
        for (Experiment experiment : experiments) {
            for (ExperimentMeasure experimentMeasure : experiment.experimentMeasures) {
                if (experimentMeasure.measure == measure) {
                    return experimentMeasure
                }
            }
        }
        return null
    }

    //TODO:finish me
    def delete(Long assayid, Sql sql) {
        assert sql
        def updateSql = "delete from assay_document where assay_id=${assayid}"
        sql.execute(updateSql)
        updateSql = "delete from assay_context where assay_id=${assayid}"
        sql.execute(updateSql)
        updateSql = "delete from assay_context_item where assay_context_id in (select assay_context_id from assay_context where assay_id=${assayid}"
        sql.execute(updateSql)
        updateSql = "delete from exprmt_context_item where exprmt_context_id in (select exprmt_context_id from exprmt_context where experiment_id in (select experiment_id from experiment where assay_id=${assayid})"
        sql.execute(updateSql)
        updateSql = "delete from exprmt_context where experiment_id in (select experiment_id from experiment where assay_id=${assayid})"
        sql.execute(updateSql)
        updateSql = "delete from experiment where assay_id=${assayid})"
        sql.execute(updateSql)

    }

    def deleteAssay(Assay assay) {
        assay.experiments.each{
            it.delete()
        }
        assay.experiments.removeAll(assay.experiments)
        assay.documents.each{
            it.delete()
        }
        assay.documents.removeAll(assay.documents)

        assay.measures.each{Measure measure->
            measure.assayContextMeasures.removeAll(measure.assayContextMeasures)
            measure.experimentMeasures.removeAll(measure.experimentMeasures)
        }
        assay.measures.removeAll(assay.measures)

        assay.assayContextItems.each {
            it.delete()
        }
        assay.assayContextItems.removeAll(assay.assayContextItems)

        assay.assayContexts.each{
            it.delete()
        }
        assay.assayContexts.removeAll(assay.assayContexts)
        if (!assay.delete(flush: true)) {
            println(assay.errors)
        }
    }

    def boolean isAssayContextItemIn(List<AssayContextItem> items, AssayContextItem item) {
        for (AssayContextItem assayContextItem : items) {
            if (!assayContextItem)
                continue
            if (isAssayContextItemEquals(assayContextItem, item))
                return true
        }
    }

    def boolean isAssayContextItemEquals(AssayContextItem a, AssayContextItem b) {
        float eps = 0.00001
        if (a.is(b))
            return true
        if (b.id == a.id)
            return true
        if (a.attributeElement != b.attributeElement)
            return false
        if (a.attributeType == AttributeType.Free && b.attributeType == AttributeType.Free)  // if they are the same, we don't care the rest
            return true
        if ((a.attributeType == b.attributeType) &&
             (a.valueElement == b.valueElement) &&
             (a.extValueId == b.extValueId) &&
             (Precision.equalsIncludingNaN(nullToNaN(a.valueNum), nullToNaN(b.valueNum), eps) && StringUtils.equals(a.qualifier, b.qualifier)) &&
             (Precision.equalsIncludingNaN(nullToNaN(a.valueMin), nullToNaN(b.valueMin), eps) && Precision.equalsIncludingNaN(nullToNaN(a.valueMax), nullToNaN(b.valueMax), eps)) &&
             (StringUtils.equals(a.valueDisplay, b.valueDisplay))
            )
            return true
        return false
    }

    Float nullToNaN(Float a) {
        if (!a)
            return Float.NaN
        return a
    }

    /**
     * Loop over the whole context to see if a particular item exist or not
     *
     * @param context
     * @param item
     * @return
     */
    boolean isContextItemExist(AbstractContext context, AbstractContextItem item) {
        for (AbstractContextItem aci : context.contextItems) {
            if (isAbstractContextItemSame(aci, item))
                return true
        }
        return false
    }

    boolean isAbstractContextItemSame(AbstractContextItem a, AbstractContextItem b) {
        float eps = 0.00001
        if (    (a.attributeElement == b.attributeElement) &&
                (a.valueElement == b.valueElement) &&
                (a.extValueId == b.extValueId) &&
                (Precision.equalsIncludingNaN(nullToNaN(a.valueNum), nullToNaN(b.valueNum), eps) && StringUtils.equals(a.qualifier, b.qualifier)) &&
                (Precision.equalsIncludingNaN(nullToNaN(a.valueMin), nullToNaN(b.valueMin), eps) && Precision.equalsIncludingNaN(nullToNaN(a.valueMax), nullToNaN(b.valueMax), eps)) &&
                (StringUtils.equals(a.valueDisplay, b.valueDisplay))
        )
            return true
       return false
    }

    boolean isDocumentEqual(AssayDocument a, AssayDocument b) {
        if (a.id == b.id)
            return true
        new EqualsBuilder().append(a.documentName, b.documentName).append(a.documentType, b.documentType).append(a.documentContent, b.documentContent).isEquals()
    }

    boolean isDocumentInAssay(AssayDocument a, Assay assay) {
        for (AssayDocument ad : assay.assayDocuments) {
            if (isDocumentEqual(ad, a))
                return true
        }
        return false
    }
}
