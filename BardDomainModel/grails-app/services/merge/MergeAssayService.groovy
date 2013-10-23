package merge

import bard.db.dictionary.Element
import bard.db.enums.AssayStatus
import bard.db.experiment.Experiment
import bard.db.experiment.ExperimentContext
import bard.db.experiment.ExperimentContextItem
import bard.db.experiment.ExperimentMeasure
import bard.db.model.AbstractContext
import bard.db.model.AbstractContextItem
import bard.db.registration.*
import groovy.sql.Sql
import org.apache.commons.lang3.StringUtils
import org.apache.commons.lang3.builder.EqualsBuilder
import org.apache.commons.math3.util.Precision

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

//    private void validateAssayConsistent(Assay assay) {
////        for (measure in assay.measures) {
////            for (expMeasure in measure.experimentMeasures) {
////                assert expMeasure.experiment.assay == assay
////            }
////        }
//
//        for (experiment in assay.experiments) {
//            for (expMeasure in experiment.experimentMeasures) {
//                assert expMeasure.experiment.assay == assay
//            }
//        }
//    }

//    private void createExperimentContextAndItem(AssayContext assayContext, String modifiedBy) {
//        for (Experiment experiment : assayContext.assay.experiments) {
//            def foundExperimentContext = experiment.experimentContexts.find { it?.contextName == assayContext.contextName && it?.contextType == assayContext.contextType }
//            if (!foundExperimentContext) {
//                foundExperimentContext = new ExperimentContext(contextName: assayContext?.contextName, contextType: assayContext?.contextType, experiment: experiment)
//                experiment.addToExperimentContexts(foundExperimentContext)
//                foundExperimentContext.experiment = experiment
//                foundExperimentContext.modifiedBy = modifiedBy + "-addedFromACI-${assayContext.id}"
//                if (!foundExperimentContext.save(failOnError: true)) {
//                    println(foundExperimentContext.errors)
//                }
//            }
//
//            for (AssayContextItem assayContextItem : assayContext.assayContextItems) {
//                if (!isContextItemExist(foundExperimentContext, assayContextItem) && assayContextItem.attributeType == AttributeType.Fixed) {  // create an explicit experiment context for fixed
//                    ExperimentContextItem newExperimentContextItem = createExperimentContextItem(assayContextItem)
//                    foundExperimentContext.addToExperimentContextItems(newExperimentContextItem)
//                    newExperimentContextItem.experimentContext = foundExperimentContext
//                    newExperimentContextItem.modifiedBy = modifiedBy + "-addedFromACI-${assayContextItem.id}"
//                    newExperimentContextItem.save(failOnError: true)
//                }
//            }
//        }
//    }

//    private ExperimentContextItem createExperimentContextItem(AssayContextItem assayContextItem) {
//        def newExperimentContextItem = new ExperimentContextItem(attributeElement: assayContextItem.attributeElement,
//                valueElement: assayContextItem.valueElement,
//                extValueId: assayContextItem.extValueId,
//                qualifier: assayContextItem.qualifier,
//                valueNum: assayContextItem.valueNum,
//                valueMin: assayContextItem.valueMin,
//                valueMax: assayContextItem.valueMax,
//                valueDisplay: assayContextItem.valueDisplay
//        )
//        return newExperimentContextItem
//    }

    //experiments:  change the experiments associated with all the removingAssays in the duplicate set so that they point to the single kept  assay
    def handleExperiments(List<Assay> removingAssays, Assay assayWillKeep, String modifiedBy) {

        List<Experiment> experiments = []
        removingAssays.each { Assay assay ->
            experiments.addAll(assay.experiments)
        }
        moveExperiments(experiments, assayWillKeep, modifiedBy)
    }

    def moveExperiments(List<Experiment> experiments, Assay assayWillKeep, String modifiedBy) {
        int addExperimentToKept = 0 // count number of experiments added to kept assays
        experiments.each { Experiment experiment ->
            Experiment found = assayWillKeep.experiments.find { it.id == experiment.id }
            if (!found) {
                addExperimentToKept++
                experiment.modifiedBy = modifiedBy + "-movedFromA-${experiment?.assay?.id}"
                experiment.assay.removeFromExperiments(experiment)

                experiment.assay = assayWillKeep
                assayWillKeep.addToExperiments(experiment)
            }
        }
        println("Total candidate experiments: ${experiments.size()}, added ${addExperimentToKept}")
        assayWillKeep.save(failOnError: true)
    }

    //assay documents:  check for exact string matches, copy over everything that does not match
//    def handleDocuments(List<Assay> removingAssays, Assay assayWillKeep, String modifiedBy) {
//        List<AssayDocument> docs = []
//        removingAssays.each { Assay assay ->
//            docs.addAll(assay.documents)
//        }
//        int addDocsToKeep = 0 // count number of documents added to kept assay
//        docs.each { AssayDocument doc ->
//            if (!isDocumentInAssay(doc, assayWillKeep)) {
//                addDocsToKeep++
//                doc.modifiedBy = modifiedBy + "-movedFromA-${doc.assay.id}"
//                doc.assay.removeFromAssayDocuments(doc)
//
//                assayWillKeep.addToAssayDocuments(doc)
//                doc.assay = assayWillKeep
//            }
//        }
//        println("Total candidate documents: ${docs.size()}, added ${addDocsToKeep}")
//        // assayWillKeep.save(failOnError: true)
//        Assay.findById(assayWillKeep.id)
//    }

    def constructMeasureKey(ExperimentMeasure measure) {
        String key = "r=${measure.resultType.id} s=${measure.statsModifier?.id}"
        if (measure.parent != null) {
            key = "${constructMeasureKey(measure.parent)}, ${key}";
        }
        return key
    }

    /**
     *
     */
    void handleMeasures(Assay target, List<Assay> originalAssays) {
        for(assay in originalAssays) {
            for(experiment in assay.experiments) {
                for(measure in experiment.experimentMeasures) {
                    for(link in measure.assayContextExperimentMeasures) {
                        AssayContext sourceContext = link.assayContext
                        sourceContext.removeFromAssayContextExperimentMeasures(link)
                        AssayContext targetContext = findOrCreateEquivalentContext(target, sourceContext)
                        targetContext.addToAssayContextExperimentMeasures(link)
                    }
                }
            }
        }
    }

    AssayContext findOrCreateEquivalentContext(Assay target, AssayContext context) {
        return context.clone(target)
    }

//    List<ExperimentMeasure> collectMeasuresSortedByPath(List<Assay> assays) {
//        Map measuresByKey = [:]
//        for (assay in assays) {
//            for (experiment in assay.experiments) {
//                for (measure in experiment.experimentMeasures)
//                    measuresByKey[constructMeasureKey(measure)] = measure
//            }
//        }
//
//        List<String> keys = new ArrayList(measuresByKey.keySet())
//        keys.sort()
//
//        return keys.collect { measuresByKey[it] }
//    }
//
//    def validateMeasures(def measures) {
//        for (found in measures) {
//            assert ((found.parent == null && found.parentChildRelationship == null) || (found.parent != null && found.parentChildRelationship != null));
//        }
//    }

    def updateStatus(List<Assay> assays, String modifiedBy) {
        assays.each { Assay assay ->
            assay.assayStatus = AssayStatus.RETIRED
            assay.modifiedBy = modifiedBy
            if (!assay.save(failOnError: true)) {
                println("Error happened when update assay status ${assay.errors}")
            }
        }
    }

//    def boolean isAssayContextItemIn(List<AssayContextItem> items, AssayContextItem item) {
//        for (AssayContextItem assayContextItem : items) {
//            if (!assayContextItem)
//                continue
//            if (isAssayContextItemEquals(assayContextItem, item))
//                return true
//        }
//    }
//
//    def boolean isAssayContextItemEquals(AssayContextItem a, AssayContextItem b) {
//        float eps = 0.00001
//        if (a.is(b))
//            return true
//        if (b.id == a.id)
//            return true
//        if (a.attributeElement != b.attributeElement)
//            return false
//        if (a.attributeType == AttributeType.Free && b.attributeType == AttributeType.Free)  // if they are the same, we don't care the rest
//            return true
//        if ((a.attributeType == b.attributeType) &&
//                (a.valueElement == b.valueElement) &&
//                (a.extValueId == b.extValueId) &&
//                (Precision.equalsIncludingNaN(nullToNaN(a.valueNum), nullToNaN(b.valueNum), eps) && StringUtils.equals(a.qualifier, b.qualifier)) &&
//                (Precision.equalsIncludingNaN(nullToNaN(a.valueMin), nullToNaN(b.valueMin), eps) && Precision.equalsIncludingNaN(nullToNaN(a.valueMax), nullToNaN(b.valueMax), eps)) &&
//                (StringUtils.equals(a.valueDisplay, b.valueDisplay))
//        )
//            return true
//        return false
//    }
//
//    Float nullToNaN(Float a) {
//        if (!a)
//            return Float.NaN
//        return a
//    }

//    /**
//     * Loop over the whole context to see if a particular item exist or not
//     *
//     * @param context
//     * @param item
//     * @return
//     */
//    boolean isContextItemExist(AbstractContext context, AbstractContextItem item) {
//        for (AbstractContextItem aci : context.contextItems) {
//            if (isAbstractContextItemSame(aci, item))
//                return true
//        }
//        return false
//    }

//    boolean isAbstractContextItemSame(AbstractContextItem a, AbstractContextItem b) {
//        float eps = 0.00001
//        if ((a.attributeElement == b.attributeElement) &&
//                (a.valueElement == b.valueElement) &&
//                (a.extValueId == b.extValueId) &&
//                (Precision.equalsIncludingNaN(nullToNaN(a.valueNum), nullToNaN(b.valueNum), eps) && StringUtils.equals(a.qualifier, b.qualifier)) &&
//                (Precision.equalsIncludingNaN(nullToNaN(a.valueMin), nullToNaN(b.valueMin), eps) && Precision.equalsIncludingNaN(nullToNaN(a.valueMax), nullToNaN(b.valueMax), eps)) &&
//                (StringUtils.equals(a.valueDisplay, b.valueDisplay))
//        )
//            return true
//        return false
//    }
//
//    boolean isDocumentEqual(AssayDocument a, AssayDocument b) {
//        if (a.id == b.id)
//            return true
//        new EqualsBuilder().append(a.documentName, b.documentName).append(a.documentType, b.documentType).append(a.documentContent, b.documentContent).isEquals()
//    }
//
//    boolean isDocumentInAssay(AssayDocument a, Assay assay) {
//        for (AssayDocument ad : assay.assayDocuments) {
//            if (isDocumentEqual(ad, a))
//                return true
//        }
//        return false
//    }
}
