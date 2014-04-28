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

package bard.db.dictionary

import spock.lang.Unroll
import org.junit.Before
import bard.db.BardIntegrationSpec
import bard.db.audit.BardContextUtils
import org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils

/**
 * Created with IntelliJ IDEA.
 * User: dlahr
 * Date: 4/23/13
 * Time: 12:11 PM
 * To change this template use File | Settings | File Templates.
 */
@Unroll
class ElementIntegrationSpec extends BardIntegrationSpec {

    @Before
    void doSetup() {
        BardContextUtils.setBardContextUsername(sessionFactory.currentSession, 'integrationTestUser')
        SpringSecurityUtils.reauthenticate('integrationTestUser', null)
    }

    void "test just one parent one child"() {
        setup:
        final String relationshipType = "relationShapes"

        Element parent = Element.build()
        assert parent.id
        System.err.println("parent ${parent.id}")

        Element child = Element.build()
        assert child.id
        System.err.println("child ${child.id}")

        ElementHierarchy elementHierarchy = new ElementHierarchy(parentElement: parent, childElement: child,
                relationshipType: relationshipType, dateCreated: new Date())
        assert elementHierarchy.save()

        parent.parentHierarchies.add(elementHierarchy)
        child.childHierarchies.add(elementHierarchy)

        Element.withSession {session -> session.flush()}

        when:
        Element foundParent = Element.findById(parent.id)
        Element foundChild = Element.findById(child.id)

        then:
        foundParent.parentHierarchies.size() == 1
        foundParent.parentHierarchies.iterator().next() == elementHierarchy
        elementHierarchy.parentElement == foundParent

        foundChild.childHierarchies.size() == 1
        foundChild.childHierarchies.iterator().next() == elementHierarchy
        elementHierarchy.childElement == foundChild
    }


    void "test just 3 children"() {
        setup:
        final int num = 3;
        final String relationshipType = "relationShapes"

        Element element = Element.build()
        assert element.id

        for (int i = 0; i < num; i++) {
            Element child = Element.build()
            assert child.id

            ElementHierarchy elementHierarchy = new ElementHierarchy(parentElement: element, childElement:  child,
                    relationshipType: relationshipType, dateCreated: new Date())
            assert elementHierarchy.save()

            element.parentHierarchies.add(elementHierarchy)

            child.childHierarchies.add(elementHierarchy)
        }
        Element.withSession {session -> session.flush()}

        when:
        Element foundElement = Element.findById(element.id)

        then:
        foundElement.childHierarchies.size() == 0
        foundElement.parentHierarchies.size() == 3
    }


    void "test just 3 parents"() {
        setup:
        final int num = 3;
        final String relationshipType = "relationShapes"

        Element element = Element.build()
        assert element.id

        for (int i = 0; i < num; i++) {
            Element parent = Element.build()
            assert parent.id

            ElementHierarchy elementHierarchy = new ElementHierarchy(parentElement: parent, childElement:  element,
                    relationshipType: relationshipType, dateCreated: new Date())
            assert elementHierarchy.save()

            element.childHierarchies.add(elementHierarchy)

            parent.parentHierarchies.add(elementHierarchy)
        }
        Element.withSession {session -> session.flush()}

        when:
        Element foundElement = Element.findById(element.id)

        then:
        foundElement.childHierarchies.size() == 3
        foundElement.parentHierarchies.size() == 0
    }


    void "test 3 parents and 3 children"() {
        setup:
        final int num = 3;
        final String relationshipType = "relationShapes"

        Element element = Element.build()
        assert element.id

        for (int i = 0; i < num; i++) {
            Element parent = Element.build()
            assert parent.id

            ElementHierarchy elementHierarchy = new ElementHierarchy(parentElement: parent, childElement:  element,
                    relationshipType: relationshipType, dateCreated: new Date())
            assert elementHierarchy.save()

            element.childHierarchies.add(elementHierarchy)

            parent.parentHierarchies.add(elementHierarchy)


            Element child = Element.build()
            assert child.id

            elementHierarchy = new ElementHierarchy(parentElement: element, childElement:  child,
                    relationshipType: relationshipType, dateCreated: new Date())
            assert elementHierarchy.save()

            element.parentHierarchies.add(elementHierarchy)

            child.childHierarchies.add(elementHierarchy)
        }
        Element.withSession {session -> session.flush()}

        when:
        Element foundElement = Element.findById(element.id)

        then:
        foundElement.childHierarchies.size() == 3
        foundElement.parentHierarchies.size() == 3
    }
}
