package bard.db.registration

import bard.db.enums.AssayStatus
import bard.db.enums.AssayType
import bard.db.enums.ReadyForExtraction
import bard.db.experiment.Experiment
import bard.db.experiment.ExperimentMeasure
import grails.buildtestdata.mixin.Build
import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import grails.test.mixin.TestMixin
import grails.test.mixin.services.ServiceUnitTestMixin
import registration.AssayService
import spock.lang.Specification

/**
 * Created with IntelliJ IDEA.
 * User: pmontgom
 * Date: 5/2/13
 * Time: 2:07 PM
 * To change this template use File | Settings | File Templates.
 */
@Build([Assay, Measure, Experiment, AssayContextMeasure, AssayContext, ExperimentMeasure, AssayContextItem, AssayDocument])
@Mock([Assay, Measure, Experiment, ExperimentMeasure, AssayContext, AssayContextMeasure, AssayContextItem, AssayDocument])
@TestMixin(ServiceUnitTestMixin)
@TestFor(AssayService)
public class AssayServiceUnitSpec extends Specification {


    void "test cloneMeasures"() {
        given:
        Assay assay = Assay.build()
        Assay clonedAssay = service.cloneAssayOnly(assay, assay.dateCreated, "me", "Clone ")
        AssayContext context = AssayContext.build(assay: assay, contextName: "alpha")
        AssayContextItem.build(assayContext: context)
        Measure measure = Measure.build(assay: assay)
        AssayContextMeasure.build(assayContext: context, measure: measure)
        when:
        Map<Measure, Measure> map = service.cloneMeasures(assay, clonedAssay)
        then:
        assert map.size() == assay.measures.size()
    }

    void 'test cloneDocuments'() {
        given:
        Assay assay = Assay.build(assayName: "assayName")
        AssayDocument.build(assay: assay)
        Assay clonedAssay = service.cloneAssayOnly(assay, assay.dateCreated, "me", "Clone ")
        assert !clonedAssay.documents
        when:
        service.cloneDocuments(assay, clonedAssay)
        then:
        assert clonedAssay.documents
    }

    void 'test cloneContexts'() {
        given:
        Assay assay = Assay.build()
        AssayContext context = AssayContext.build(assay: assay, contextName: "alpha")
        AssayContextItem.build(assayContext: context)
        Assay clonedAssay = service.cloneAssayOnly(assay, assay.dateCreated, "me", "Clone ")

        when:
        Map<AssayContext, AssayContext> map = service.cloneContexts(assay, clonedAssay)
        then:
        assert map
        assert map.size() == assay.assayContexts.size()
    }

    void "test cloneAssayOnly #desc"() {
        given:
        Assay assay = assayBuild.call()
        when:
        Assay clonedAssay = service.cloneAssayOnly(assay, assay.dateCreated, "me", assayNamePrefix)
        then:
        assert clonedAssay.assayName == expectedAssayName
        assert clonedAssay.assayStatus == expectedAssayStatus
        assert clonedAssay.readyForExtraction == assay.readyForExtraction
        assert clonedAssay.assayShortName == assay.assayShortName
        assert clonedAssay.assayVersion == "1"
        assert clonedAssay.designedBy == "me"

        where:
        desc                                        | assayBuild                              | assayNamePrefix | expectedAssayName  | expectedAssayStatus
        "Cloned Assay with blank assay Name prefix" | { Assay.build(assayName: "assayName") } | ""              | "assayName"        | AssayStatus.DRAFT
        "Cloned Assay with an assay Name prefix"    | { Assay.build(assayName: "assayName") } | "Cloned "       | "Cloned assayName" | AssayStatus.DRAFT
    }

    void 'test clone assay for editing'() {
        given:
        Assay assay = Assay.build(assayType: AssayType.TEMPLATE)
        AssayContext context = AssayContext.build(assay: assay, contextName: "alpha")
        AssayContextItem contextItem = AssayContextItem.build(assayContext: context)
        AssayDocument.build(assay: assay)
        Measure measure = Measure.build(assay: assay)
        AssayContextMeasure assayContextMeasure = AssayContextMeasure.build(assayContext: context, measure: measure)

        when:
        Assay newAssay = service.cloneAssayForEditing(assay,assay.designedBy);

        then:
        // test assay props are good
        assay != newAssay
        assert "Clone of ${assay.assayName}" == newAssay.assayName
        assert assay.assayShortName == newAssay.assayShortName
        assert AssayStatus.DRAFT == newAssay.assayStatus
        assert assay.assayType != newAssay.assayType
        assert newAssay.assayType == AssayType.REGULAR
        assert assay.designedBy == newAssay.designedBy
        assert newAssay.assayVersion =="1"
        assert ReadyForExtraction.NOT_READY == newAssay.readyForExtraction

        // test assay documents are good
        assert newAssay.assayDocuments.isEmpty()

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

    void 'test assay clone'() {
        setup:
        Assay assay = Assay.build()
        AssayContext context = AssayContext.build(assay: assay, contextName: "alpha")
        AssayContextItem contextItem = AssayContextItem.build(assayContext: context)
        AssayDocument document = AssayDocument.build(assay: assay)
        Measure measure = Measure.build(assay: assay)
        AssayContextMeasure assayContextMeasure = AssayContextMeasure.build(assayContext: context, measure: measure)

        when:
        Assay newAssay = service.cloneAssay(assay).assay;

        then:
        // test assay props are good
        assay != newAssay
        assay.assayName == newAssay.assayName
        assay.assayShortName == newAssay.assayShortName
        assay.assayStatus == newAssay.assayStatus
        assay.assayType == newAssay.assayType
        assay.designedBy == newAssay.designedBy
        newAssay.assayVersion  == "1"
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
