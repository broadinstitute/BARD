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

package bard.db.project

import bard.db.BardIntegrationSpec
import bard.db.people.Role
import bard.db.registration.ExternalReference
import org.junit.Before
import org.springframework.dao.DataIntegrityViolationException

/**
 * Created with IntelliJ IDEA.
 * User: ddurkin
 * Date: 8/23/12
 * Time: 12:07 AM
 * To change this template use File | Settings | File Templates.
 */
class ProjectIntegrationSpec extends BardIntegrationSpec {

    Project domainInstance
    ProjectContext projectContext
    ProjectContextItem contextItem

    ProjectSingleExperiment projectExperiment

    ExternalReference externalReference

    @Before
    void doSetup() {
        Role role = Role.build(authority: "authority")
        domainInstance = Project.buildWithoutSave(ownerRole: role)
    }

    public initializeProjectExperiment() {
        projectExperiment = ProjectSingleExperiment.buildWithoutSave()
        projectExperiment.experiment.save()
        domainInstance.addToProjectExperiments(projectExperiment)
    }

    void initializeProjectContextItem() {
        contextItem = ProjectContextItem.buildWithoutSave()
        projectContext.addToContextItems(contextItem)
        contextItem.attributeElement.save()
    }

    void initializeExternalReference() {
        externalReference = ExternalReference.buildWithoutSave()
        domainInstance.addToExternalReferences(externalReference)
    }

    void initializeProjectContext() {
        projectContext = ProjectContext.buildWithoutSave()
        domainInstance.addToContexts(projectContext)
        projectContext
    }


    void "test context cascade save"() {
        given:
        initializeProjectContext()
        assert projectContext.id == null
        when:
        domainInstance.save(flush: true)
        then:
        projectContext.id != null
    }

    void "test contextItems cascade save"() {
        given:
        initializeProjectContext()
        initializeProjectContextItem()
        assert contextItem.id == null
        when:
        domainInstance.save(flush: true)
        then:
        contextItem.id != null
    }

    void "test projectExperiments cascade save"() {
        given:
        initializeProjectExperiment()
        assert projectExperiment.id == null
        when:
        domainInstance.save(flush: true)
        then:
        projectExperiment.id != null
    }

    void "test externalReferences cascade save"() {
        given:
        initializeExternalReference()
        assert externalReference.id == null
        when:
        domainInstance.save()
        then:
        externalReference.id != null
    }

    void "test externalReferences cascade delete"() {
        given:
        initializeExternalReference()
        domainInstance.save()
        assert externalReference.id != null
        flushAndClear()

        when:
        domainInstance.refresh().delete()
        flushAndClear()

        then: 'externalReference should be deleted to to belongsTo cascade delete'
        notThrown(DataIntegrityViolationException)
        ExternalReference.findById(externalReference.id) == null
    }

    void flushAndClear() {
        Project.withSession { session ->
            session.flush()
            session.clear()
        }
    }
}
