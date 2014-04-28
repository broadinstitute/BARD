/* Copyright (c) 2014, The Broad Institute
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
    * Redistributions of source code must retain the above copyright
      notice, this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright
      notice, this list of conditions and the following disclaimer in the
      documentation and/or other materials provided with the distribution.
    * Neither the name of The Broad Institute nor the
      names of its contributors may be used to endorse or promote products
      derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL The Broad Institute BE LIABLE FOR ANY
DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package bard.db.registration

import bard.db.enums.AssayType
import bard.db.enums.ReadyForExtraction
import bard.db.enums.Status
import bard.db.experiment.AssayContextExperimentMeasure
import bard.db.experiment.Experiment
import bard.db.experiment.ExperimentMeasure
import bard.db.people.Role
import grails.buildtestdata.mixin.Build
import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import grails.test.mixin.TestMixin
import grails.test.mixin.services.ServiceUnitTestMixin
import org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils
import registration.AssayService
import spock.lang.Specification

/**
 * Created with IntelliJ IDEA.
 * User: pmontgom
 * Date: 5/2/13
 * Time: 2:07 PM
 * To change this template use File | Settings | File Templates.
 */
@Build([Role, Assay, Experiment, AssayContextExperimentMeasure, ExperimentMeasure, AssayContext, AssayContextItem, AssayDocument])
@Mock([Role, Assay, Experiment, AssayContextExperimentMeasure, ExperimentMeasure, AssayContext, AssayContextItem, AssayDocument])
@TestMixin(ServiceUnitTestMixin)
@TestFor(AssayService)
public class AssayServiceUnitSpec extends Specification {


    void 'test cloneDocuments'() {
        given:
        SpringSecurityUtils.metaClass.'static'.ifAnyGranted = { String role ->
            return true
        }
        Role role = Role.build()
        Assay assay = Assay.build(assayName: "assayName", ownerRole: role)
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
        SpringSecurityUtils.metaClass.'static'.ifAnyGranted = { String role ->
            return true
        }
        Assay assay = Assay.build()
        AssayContext context = AssayContext.build(assay: assay, contextName: "alpha")
        AssayContextItem.build(assayContext: context)
        Assay clonedAssay = service.cloneAssayOnly(assay, assay.dateCreated, "me", "Clone ")

        when:
        Map<AssayContext, AssayContext> map = service.cloneContexts(assay, clonedAssay, false)
        then:
        assert map
        assert map.size() == assay.assayContexts.size()
    }

    void "test cloneAssayOnly #desc"() {
        given:
        SpringSecurityUtils.metaClass.'static'.ifAnyGranted = { String role ->
            return true
        }
        Assay assay = assayBuild.call()
        when:
        Assay clonedAssay = service.cloneAssayOnly(assay, assay.dateCreated, "me", assayNamePrefix)
        then:
        assert clonedAssay.assayName == expectedAssayName
        assert clonedAssay.assayStatus == expectedAssayStatus
        assert clonedAssay.readyForExtraction == assay.readyForExtraction
        assert clonedAssay.assayVersion == "1"
        assert clonedAssay.designedBy == "me"

        where:
        desc                                        | assayBuild                              | assayNamePrefix | expectedAssayName  | expectedAssayStatus
        "Cloned Assay with blank assay Name prefix" | { Assay.build(assayName: "assayName") } | ""              | "assayName"        | Status.DRAFT
        "Cloned Assay with an assay Name prefix"    | { Assay.build(assayName: "assayName") } | "Cloned "       | "Cloned assayName" | Status.DRAFT
    }

    void 'test clone assay for editing'() {
        given:
        Assay assay = Assay.build(assayType: AssayType.TEMPLATE)
        AssayContext context = AssayContext.build(assay: assay, contextName: "alpha")
        AssayContextItem contextItem = AssayContextItem.build(assayContext: context)
        AssayDocument.build(assay: assay)
        SpringSecurityUtils.metaClass.'static'.ifAnyGranted = { String role ->
            return true
        }
        Role.metaClass.'static'.findByAuthority = { String authority ->
            new Role()
        }
        when:
        Assay newAssay = service.cloneAssayForEditing(assay, assay.designedBy);

        then:
        // test assay props are good
        assay != newAssay
        assert "Clone of ${assay.assayName}" == newAssay.assayName
        assert Status.DRAFT == newAssay.assayStatus
        assert assay.assayType != newAssay.assayType
        assert newAssay.assayType == AssayType.REGULAR
        assert assay.designedBy == newAssay.designedBy
        assert newAssay.assayVersion == "1"
        assert ReadyForExtraction.NOT_READY == newAssay.readyForExtraction

        // test assay documents are good
        assert newAssay.assayDocuments.isEmpty()

        // test assay context is good
        newAssay.assayContexts.size() == 1
        AssayContext newContext = newAssay.assayContexts.first()
        newContext != context
        newContext.contextName == context.contextName
        newContext.contextType == context.contextType

        // test assay context items are good
        AssayContextItem newContextItem = newContext.assayContextItems.first()
        newContextItem != contextItem
        newContextItem.attributeType == contextItem.attributeType
    }
    //Commenting this out and writing an integration test instead
/*    void 'test assay clone'() {
        setup:
        SpringSecurityUtils.metaClass.'static'.ifAnyGranted = { String role ->
            return true
        }
        Assay assay = Assay.build()
        AssayContext context = AssayContext.build(assay: assay, contextName: "alpha")
        AssayContextItem contextItem = AssayContextItem.build(assayContext: context)
        AssayDocument document = AssayDocument.build(assay: assay)
        Experiment experiment = Experiment.build(assay: assay)
        ExperimentMeasure experimentMeasure = ExperimentMeasure.build(experiment: experiment)
        AssayContextExperimentMeasure.build(assayContext: context, experimentMeasure: experimentMeasure)
        and:
        service.metaClass.cloneAssayOnly = { return [assay: assay, measureOldToNew: [:]]}
        when:
        Assay newAssay = service.cloneAssay(assay).assay;

        then:
        // test assay props are good
        assay != newAssay
        assay.assayName == newAssay.assayName
        assay.assayStatus == newAssay.assayStatus
        assay.assayType == newAssay.assayType
        assay.designedBy == newAssay.designedBy
        newAssay.assayVersion == "1"
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
        newContext.contextType == context.contextType

        // test assay context items are good
        AssayContextItem newContextItem = newContext.assayContextItems.first()
        newContextItem != contextItem
        newContextItem.attributeType == contextItem.attributeType
    } */
}
