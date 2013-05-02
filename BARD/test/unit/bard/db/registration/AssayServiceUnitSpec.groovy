package bard.db.registration

import bard.db.experiment.Experiment
import bard.db.experiment.ExperimentMeasure;
import bard.db.experiment.ExperimentService;
import grails.buildtestdata.mixin.Build;
import grails.test.mixin.Mock;
import grails.test.mixin.TestMixin;
import grails.test.mixin.services.ServiceUnitTestMixin
import registration.AssayService;

/**
 * Created with IntelliJ IDEA.
 * User: pmontgom
 * Date: 5/2/13
 * Time: 2:07 PM
 * To change this template use File | Settings | File Templates.
 */
@Build([Assay, Measure, Experiment, AssayContextMeasure, AssayContext, ExperimentMeasure, AssayContextItem, AssayDocument])
@Mock([Assay, Measure, Experiment,ExperimentMeasure,AssayContext,AssayContextMeasure, AssayContextItem, AssayDocument])
@TestMixin(ServiceUnitTestMixin)

public class AssayServiceUnitSpec extends spock.lang.Specification {
    void 'test assay clone'() {
        setup:
        AssayService assayService = new AssayService()

        Assay assay = Assay.build()
        AssayContext context = AssayContext.build(assay: assay, contextName: "alpha")
        AssayContextItem contextItem = AssayContextItem.build(assayContext: context)
        AssayDocument document = AssayDocument.build(assay: assay)
        Measure measure = Measure.build(assay: assay)
        AssayContextMeasure assayContextMeasure = AssayContextMeasure.build(assayContext: context, measure: measure)

        when:
        Assay newAssay = assayService.cloneAssay(assay).assay;

        then:
        // test assay props are good
        assay != newAssay
        assay.assayName == newAssay.assayName
        assay.assayShortName == newAssay.assayShortName
        assay.assayStatus == newAssay.assayStatus
        assay.assayType == newAssay.assayType
        assay.designedBy == newAssay.designedBy
        assay.assayVersion == newAssay.assayVersion
        assay.readyForExtraction == newAssay.readyForExtraction

        // test assay documents are good
        newAssay.assayDocuments.size() == 1
        AssayDocument newDocument = newAssay.assayDocuments.first()
        newDocument.documentName == document.documentName
        newDocument.documentContent == document.documentContent
        newDocument.documentType == document.documentType

        // test assay context is good
        newAssay.assayContexts.size() == 1
        AssayContext newContext = newAssay.assayContexts.first()
        newContext != context
        newContext.contextName == context.contextName
        newContext.contextGroup == context.contextGroup

        // test assay context items are good
        AssayContextItem newContextItem = newContext.assayContextItems.first()
        newContextItem != contextItem
        newContextItem.attributeType == contextItem.attributeType

        // test all measure properties are good
        newAssay.measures.size() == 1
        Measure newMeasure = newAssay.measures.first()
        newMeasure != measure
        measure.resultType == newMeasure.resultType
        measure.statsModifier == newMeasure.statsModifier
        measure.parentChildRelationship == newMeasure.parentChildRelationship

        // test assay context measure props
        newContext.assayContextMeasures.size() == 1
        AssayContextMeasure newAssayContextMeasure = newContext.assayContextMeasures.first()
        newAssayContextMeasure != assayContextMeasure
        newAssayContextMeasure.measure == newMeasure
        newAssayContextMeasure.assayContext == newContext
    }
}
