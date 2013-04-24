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
