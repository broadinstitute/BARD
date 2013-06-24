package bard.validation.ext

import bard.db.audit.BardContextUtils
import bard.db.people.Person
import bard.validation.extext.ExternalOntologyPerson
import grails.plugin.spock.IntegrationSpec
import org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils
import org.hibernate.SessionFactory
import spock.lang.Unroll

@Unroll
class ExternalOntologyPersonIntegrationSpec extends IntegrationSpec {

    ExternalOntologyPerson extOntologyPerson
    List<Person> persons;
    SessionFactory sessionFactory

    def setup() {
        BardContextUtils.setBardContextUsername(sessionFactory.currentSession, 'integrationTestUser')
        SpringSecurityUtils.reauthenticate('integrationTestUser', null)

        extOntologyPerson = new ExternalOntologyPerson()

        for (int i = 1; i < 11; i++) {

            Person p = Person.build(userName: "testuserExternalOntologyPersonIntegrationSpec" + i, fullName: "New ExternalOntologyPersonIntegrationSpec Test User" + i)
            p.save(flush: true)
        }
        Person p1 = Person.build(userName: "userABCDEExternalOntologyPersonIntegrationSpec", fullName: "New ExternalOntologyPersonIntegrationSpec User")
        p1.save(flush: true)
        Person p2 = Person.build(userName: "userExternalOntologyPersonIntegrationSpec1000", fullName: "New ExternalOntologyPersonIntegrationSpec XYZ123 User")
        p2.save(flush: true)
        persons = Person.findAllByUserNameIlike("%ExternalOntologyPersonIntegrationSpec%")
    }

    def cleanup() {
        for (Person p in persons) {
            p.delete(flush: true)
        }
    }

    void "test findById"() {
        when:
        Person testPerson = persons.get(0)
        ExternalItem extItem1 = extOntologyPerson.findById(testPerson.id.toString())
        ExternalItem extItem2 = extOntologyPerson.findById("10000")

        then:

        extItem1.id == testPerson.id.toString()
        extItem1.display == 'testuserExternalOntologyPersonIntegrationSpec1 (New ExternalOntologyPersonIntegrationSpec Test User1)'
        extItem2 == null
    }

    void "test findById desc: '#desc' item: '#item' itemDisplay: '#itemDisplay'"() {
        when:
        Person testPerson = persons.get(0)
        ExternalItem extItem = extOntologyPerson.findById(searchId)

        then:
        item == extItem
        itemDisplay == extItem?.display

        where:
        desc                                      | item | itemDisplay | searchId
        "Return null with invalid null searchId"  | null | null        | null
        "Return null with invalid empty searchId" | null | null        | ''
        "Return null with invalid blank searchId" | null | null        | '   '
        "Return null with not found searchId"     | null | null        | '10000'

    }

    void "test findMatching and check # of items returned desc: '#desc' numberOfPersons: '#numberOfPersons'"() {
        when:
        List<ExternalItem> items = extOntologyPerson.findMatching(searchTerm, limit)

        then:
        numberOfPersons == items.size()

        where:
        desc                                                | numberOfPersons | searchTerm                                       | limit
        "1 person"                                          | 1               | 'testuserExternalOntologyPersonIntegrationSpec5' | 20
        "0 person due to searchTerm no match"               | 0               | 'testExternalOntologyPersonIntegrationSpec123'   | 20
        "0 person due to searchTerm null"                   | 0               | null                                             | 20
        "0 person due to searchTerm empty"                  | 0               | ''                                               | 20
        "10 persons that contain searchTerm"                | 10              | 'testuserExternalOntologyPersonIntegrationSpec'  | 20
        "5 persons due to limit set to 5"                   | 5               | 'testuserExternalOntologyPersonIntegrationSpec'  | 5
        "1 person with lower and upper case searchTerm"     | 1               | 'abCDEExternalOntologyPersonIntegrationSpec'     | 20
        "12 persons with searchTerm in fullname"            | 12              | 'new ExternalOntologyPersonIntegrationSpec'      | 20
        "12 persons with searchTerm in username & fullname" | 12              | 'ExternalOntologyPersonIntegrationSpec'          | 20
        "10 persons with lower and upper case searchTerm"   | 10              | 'TESTuserExternalOntologyPersonIntegrationSpec'  | 20

    }

    void "test findMatching and check list of names returned desc: '#desc' expectedPersons: '#expectedPersons'"() {
        when:
        List<ExternalItem> items = extOntologyPerson.findMatching(searchTerm, limit)

        then:
        expectedPersons == items*.display

        where:
        desc                                              | expectedPersons                                                                                                                                                                                                                                                                                                               | searchTerm                                       | limit
        "1 person"                                        | ['testuserExternalOntologyPersonIntegrationSpec5 (New ExternalOntologyPersonIntegrationSpec Test User5)']                                                                                                                                                                                                                     | 'testuserExternalOntologyPersonIntegrationSpec5' | 20
        "0 person due to searchTerm no match"             | []                                                                                                                                                                                                                                                                                                                            | 'test1234ExternalOntologyPersonIntegrationSpec'  | 20
        "0 person due to searchTerm null"                 | []                                                                                                                                                                                                                                                                                                                            | null                                             | 20
        "0 person due to searchTerm empty"                | []                                                                                                                                                                                                                                                                                                                            | ''                                               | 20
        "0 person due to searchTerm with multiple spaces" | []                                                                                                                                                                                                                                                                                                                            | '   '                                            | 20
        "3 persons"                                       | ['testuserExternalOntologyPersonIntegrationSpec1 (New ExternalOntologyPersonIntegrationSpec Test User1)', 'testuserExternalOntologyPersonIntegrationSpec10 (New ExternalOntologyPersonIntegrationSpec Test User10)', 'userExternalOntologyPersonIntegrationSpec1000 (New ExternalOntologyPersonIntegrationSpec XYZ123 User)'] | 'userExternalOntologyPersonIntegrationSpec1'     | 20
        "2 persons due to limit set to 2"                 | ['testuserExternalOntologyPersonIntegrationSpec1 (New ExternalOntologyPersonIntegrationSpec Test User1)', 'testuserExternalOntologyPersonIntegrationSpec10 (New ExternalOntologyPersonIntegrationSpec Test User10)']                                                                                                          | 'new ExternalOntologyPersonIntegrationSpec'      | 2

    }

}