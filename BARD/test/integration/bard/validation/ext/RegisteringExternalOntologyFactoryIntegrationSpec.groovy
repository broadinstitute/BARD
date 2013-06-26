package bard.validation.ext

import bard.db.audit.BardContextUtils
import bard.validation.extext.ExternalOntologyPerson
import bard.validation.extext.PersonCreator
import org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils
import grails.plugin.spock.IntegrationSpec
import org.hibernate.SessionFactory;

import bard.db.people.Person;
import spock.lang.Unroll

@Unroll
class RegisteringExternalOntologyFactoryIntegrationSpec extends IntegrationSpec {
	
	RegisteringExternalOntologyFactory reoFactory;
//	ExternalOntologyPerson extOntologyPerson
	ExternalOntologyAPI extOntologyPerson
	List<Person> persons;
	SessionFactory sessionFactory

	def setup() {
		BardContextUtils.setBardContextUsername(sessionFactory.currentSession, 'integrationTestUser')
		SpringSecurityUtils.reauthenticate('integrationTestUser', null)
		
		for (int i = 1; i < 11; i++) {			
			Person p = Person.build(userName: "testuserExternalOntologyPersonIntegrationSpec" + i, fullName: "New ExternalOntologyPersonIntegrationSpec Test User" + i)
			p.save(flush: true)
		}
		Person p1 = Person.build(userName: "userABCDEExternalOntologyPersonIntegrationSpec", fullName: "New ExternalOntologyPersonIntegrationSpec User")
		p1.save(flush: true)
		Person p2 = Person.build(userName: "userExternalOntologyPersonIntegrationSpec1000", fullName: "New ExternalOntologyPersonIntegrationSpec XYZ123 User")
		p2.save(flush: true)
		persons = Person.findAllByUserNameIlike("%ExternalOntologyPersonIntegrationSpec%")
		
		reoFactory = RegisteringExternalOntologyFactory.instance;
		reoFactory.creators.add(new PersonCreator())

		extOntologyPerson = reoFactory.getExternalOntologyAPI(ExternalOntologyPerson.PERSON_URI, null)
	}

	def cleanup() {
		for (Person p in persons) {
			p.delete(flush: true)
		}
	}

	void "test Factory for Person findById"() {
		when:
		Person testPerson = persons.get(0)
		ExternalItem extItem1 = extOntologyPerson.findById(testPerson.id.toString())
		ExternalItem extItem2 = extOntologyPerson.findById("10000")

		then:

		extItem1.id == testPerson.id.toString()
		extItem1.display == 'testuserExternalOntologyPersonIntegrationSpec1 (New ExternalOntologyPersonIntegrationSpec Test User1)'
		extItem2 == null
	}
	
	void "test Factory for Person findMatching"() {
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