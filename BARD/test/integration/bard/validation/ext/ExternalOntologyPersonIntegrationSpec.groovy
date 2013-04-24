package bard.validation.ext

import grails.plugin.spock.IntegrationSpec

import org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils
import org.hibernate.SessionFactory

import bard.db.audit.BardContextUtils
import bard.db.people.Person


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
		extItem1.display == testPerson.userName
		extItem2 == null
	}
	
	void "test findMatching"() {
		when:
		List<ExternalItem> items1 = extOntologyPerson.findMatching("test", 20)
		List<ExternalItem> items2 = extOntologyPerson.findMatching("test", 5)
		List<ExternalItem> items3 = extOntologyPerson.findMatching("ABCDE", 20)
		List<ExternalItem> items4 = extOntologyPerson.findMatching("XYZ123", 20)
		List<ExternalItem> items5 = extOntologyPerson.findMatching("test123", 20)
		List<ExternalItem> items6 = extOntologyPerson.findMatching("new", 20)
		List<ExternalItem> items7 = extOntologyPerson.findMatching("abCDE", 20)
		List<ExternalItem> items8 = extOntologyPerson.findMatching("testuser5", 20)
		List<ExternalItem> items9 = extOntologyPerson.findMatching("TESTuser", 20)
		
		
		then:
		items1.size() == 10
		items2.size() == 5
		items3.size() == 1
		items4.size() == 1
		items5.size() == 0
		items6.size() == 12
		items7.size() == 1
		items8.size() == 1
		items8.get(0).display == "testuser5 (New Test User5)"
		items9.size() == 10
		
	}
}