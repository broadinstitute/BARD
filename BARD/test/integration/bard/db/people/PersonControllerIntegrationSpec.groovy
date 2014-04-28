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

package bard.db.people

import bard.PersonController
import bard.db.audit.BardContextUtils
import grails.plugin.spock.IntegrationSpec
import org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils
import org.hibernate.SessionFactory
import spock.lang.Unroll

/**
 * Created with IntelliJ IDEA.
 * User: ddurkin
 * Date: 7/15/13
 * Time: 11:27 AM
 * To change this template use File | Settings | File Templates.
 */
@Unroll
class PersonControllerIntegrationSpec extends IntegrationSpec {

    SessionFactory sessionFactory
    PersonController controller

    void setup() {
        SpringSecurityUtils.reauthenticate('integrationTestUser', null)
        BardContextUtils.setBardContextUsername(sessionFactory.currentSession, 'integrationTestUser')
        controller = new PersonController()
    }

    void "test list verify model contents and view"() {

        when:
        controller.list()

        then: 'verify model contents'
        controller.modelAndView != null

        def model = controller.modelAndView.model
        model != null
        model.people != null
        model.roles == Role.all
        model.peopleTotal >= 0

        model.personCommand != null
        model.personCommand.username == null
        model.personCommand.email == null

        and: 'verify the view'
        controller.modelAndView.viewName.endsWith('list')
    }

    void "test edit verify model contents and view"() {
        given:
        Person person = Person.build()
        controller.params.id = person.id.toString()

        when:
        controller.edit()

        then: 'verify model contents'
        controller.modelAndView != null
        controller.modelAndView.model != null

        def model = controller.modelAndView.model
        model.person == person
        model.roles == Role.all

        model.personCommand != null
        model.personCommand.username == person.userName
        model.personCommand.email == person.emailAddress

        and: 'verify the view'
        controller.modelAndView.viewName.endsWith('edit')
    }

    void "test edit person desc: #desc"() {
        given:
        controller.params.id = id

        when:
        controller.edit()

        then: 'verify model contents'
        controller.response.redirectUrl.endsWith('list')
        controller.flash.message == "Person not found. Try again"

        where:
        desc              | id
        'null id'         | null
        'some unknown id' | -1000L
    }
}


