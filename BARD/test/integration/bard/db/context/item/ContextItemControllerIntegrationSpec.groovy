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

package bard.db.context.item

import bard.db.ContextItemService
import bard.db.audit.BardContextUtils
import bard.db.project.ProjectContextItem
import grails.plugin.spock.IntegrationSpec
import org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils
import org.hibernate.SessionFactory
import spock.lang.Unroll

/**
 * Created with IntelliJ IDEA.
 * User: ddurkin
 * Date: 5/24/13
 * Time: 4:46 PM
 * To change this template use File | Settings | File Templates.
 */
@Unroll
class ContextItemControllerIntegrationSpec extends IntegrationSpec {


    ContextItemController controller

    ProjectContextItem contextItem
    SessionFactory sessionFactory
    ContextItemService contextItemService
    void setup() {
        SpringSecurityUtils.reauthenticate('integrationTestUser', null)
        BardContextUtils.setBardContextUsername(sessionFactory.currentSession, 'integrationTestUser')
        controller = new ContextItemController()
    }

    void "test update number handling #desc"() {
        given:
        ProjectContextItem contextItem = ProjectContextItem.build()
        // unclear what is going on with the build method that requires a flush
        // after the save.
        // if the flush does not happen, then it occurs in the service call which causes a version mismatch
        ProjectContextItem.withSession { session ->
            session.flush()
        }
        BasicContextItemCommand contextItemCommand =
            new BasicContextItemCommand(attributeElementId: contextItem.attributeElement.id, valueNum: valueNumParam,qualifier: '= ')

        setContextItemRelatedParams(contextItem,contextItemCommand)

          when:
        controller.update(contextItemCommand)

        then:
        controller.modelAndView.model.instance.contextItem == contextItem
        controller.modelAndView.model.instance.valueNum == expectedValueNum

        where:
        desc | valueNumParam | expectedValueNum
        ''   | '3.5e-3'      | '0.0035'
        ''   | '3.5E-3'      | '0.0035'
        ''   | '0.0035'      | '0.0035'
        ''   | '0.0012'      | '0.0012'
        ''   | '0.1234567'   | '0.1234567'
        ''   | '0.3'         | '0.3'
        ''   | '0.000003'    | '0.000003'
        ''   | '0.000003'    | '0.000003'


    }

    void "test delete"() {
        given:
        ProjectContextItem contextItem = ProjectContextItem.build()
        final Long id = contextItem.id

        BasicContextItemCommand contextItemCommand =
            new BasicContextItemCommand()

        setContextItemRelatedParams(contextItem,contextItemCommand)

        when:
        controller.delete(contextItemCommand)

        then:
        ProjectContextItem.findById(id) == null

    }

    private void setContextItemRelatedParams(ProjectContextItem contextItem,BasicContextItemCommand contextItemCommand) {
        contextItemCommand.contextOwnerId = contextItem.context?.owner?.id
        contextItemCommand.contextId = contextItem.context?.id
        contextItemCommand.contextItemId = contextItem.id
        contextItemCommand.version = contextItem.version
        contextItemCommand.contextClass = 'ProjectContext'
        contextItemCommand.contextItemService = contextItemService
    }


}
