package bard.validation.ext

import grails.plugin.spock.IntegrationSpec

import org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils
import org.hibernate.SessionFactory

import spock.lang.Unroll;

import bard.db.audit.BardContextUtils
import bard.db.people.Person

@Unroll
class ExternalOntologyPersonIntegrationSpec extends IntegrationSpec {
	
	ExternalOntologyPerson extOntologyPerson
	List<Person> persons;
	SessionFactory sessionFactory
	
	def setup() {
		BardContextUtils.setBardContextUsername(sessionFactory.currentSession, 'integrationTestUser')
		SpringSecurityUtils.reauthenticate('integrationTestUser', null)	
		
		Person.executeUpdate("delete from Person")		
		extOntologyPerson = ExternalOntologyPerson.PERSON_INSTANCE

		for(int i = 1; i < 11; i++){
			Person p = new Person(userName: "testuser" + i, fullName: "New Test User" + i, accountExpired: false, accountLocked: false, accountEnabled: true)
			p.save(flush: true)
		}
		Person p1 = new Person(userName: "userABCDE", fullName: "New User", accountExpired: false, accountLocked: false, accountEnabled: true)
		p1.save(flush: true)
		Person p2 = new Person(userName: "user1000", fullName: "New XYZ123 User", accountExpired: false, accountLocked: false, accountEnabled: true)
		p2.save(flush: true)
		persons = Person.list()
	}

	def cleanup() {
	}

	void "test findById"() {
		when:
		Person testPerson = persons.get(0)
		ExternalItem extItem1 = extOntologyPerson.findById(testPerson.id.toString())
		ExternalItem extItem2 = extOntologyPerson.findById("10000")
		
		then:
		
		extItem1.id == testPerson.id.toString()
		extItem1.display == 'testuser1 (New Test User1)'
		extItem2 == null
	}
	
	void "test findById desc: '#desc' item: '#item' itemDisplay: '#itemDisplay'"(){		
		when:
		Person testPerson = persons.get(0)
		ExternalItem extItem = extOntologyPerson.findById(searchId)
		
		then:
		item == extItem
		itemDisplay == extItem?.display
		
		where:
		desc                                        		 | item			| itemDisplay   				| searchId
		"Return null with invalid null searchId"			 | null		    | null      					| null
		"Return null with invalid empty searchId"			 | null		    | null      					| ''
		"Return null with invalid blank searchId"			 | null		    | null      					| '   '
		"Return null with not found searchId"			     | null		    | null      					| '10000'
		
	}
	
	void "test findMatching and check # of items returned desc: '#desc' numberOfPersons: '#numberOfPersons'"(){
		when:
		List<ExternalItem> items = extOntologyPerson.findMatching(searchTerm, limit)
		
		then:
		numberOfPersons == items.size()
		
		where:
		desc                                        		 | numberOfPersons | searchTerm   | limit
		"1 person"											 | 1               | 'testuser5'  | 20
		"0 person due to searchTerm no match"       		 | 0               | 'test123'    | 20
		"0 person due to searchTerm null"           		 | 0               | null         | 20
		"0 person due to searchTerm empty"          		 | 0               | ''           | 20
		"10 persons that contain searchTerm"        		 | 10              | 'test'       | 20
		"5 persons due to limit set to 5"           		 | 5               | 'test'       | 5
		"1 person with lower and upper case searchTerm"      | 1               | 'abCDE'      | 20
		"12 persons with searchTerm in fullname"      		 | 12              | 'new'        | 20
		"12 persons with searchTerm in username & fullname"  | 12              | 'user'       | 20
		"10 persons with lower and upper case searchTerm"    | 10              | 'TESTuser'   | 20
		
	}
	
	void "test findMatching and check list of names returned desc: '#desc' expectedPersons: '#expectedPersons'"(){
		when:
		List<ExternalItem> items = extOntologyPerson.findMatching(searchTerm, limit)
		
		then:
		expectedPersons == items*.display
		
		where:
		desc                                               | expectedPersons                     | searchTerm   | limit
		"1 person"									       | ['testuser5 (New Test User5)']	  	 | 'testuser5'  | 20
		"0 person due to searchTerm no match"              | []								     | 'test1234'   | 20
		"0 person due to searchTerm null"                  | []								     | null         | 20
		"0 person due to searchTerm empty"                 | []								     | ''           | 20
		"0 person due to searchTerm with multiple spaces"  | []								     | '   '        | 20
		"3 persons" 									   | ['testuser1 (New Test User1)', 'testuser10 (New Test User10)', 'user1000 (New XYZ123 User)'] | 'user1'      | 20
		"2 persons due to limit set to 2" 				   | ['testuser1 (New Test User1)', 'testuser2 (New Test User2)'] | 'new'      | 2
		
	}
	
}