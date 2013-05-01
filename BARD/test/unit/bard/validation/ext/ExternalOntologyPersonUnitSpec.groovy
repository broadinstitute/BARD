package bard.validation.ext

import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

import bard.db.people.Person

@Unroll
class ExternalOntologyPersonUnitSpec extends Specification {
	
	ExternalOntologyPerson extOntologyPerson	

	def setup() {
		extOntologyPerson = new ExternalOntologyPerson()
	}

	def cleanup() {
	}
	
	void "test getItem  desc: '#desc' item: '#item'"() {
		when:
		Person newPerson = new Person(id: 20L, userName: 'user100', fullName: 'User 100', accountExpired: false, accountLocked: false, accountEnabled: true)
		ExternalItem extItem1 = ExternalOntologyPerson.getItem(newPerson)
		ExternalItem extItem2 = ExternalOntologyPerson.getItem(null)
		
		then:
		extItem1 != null
		extItem1.display == 'user100 (User 100)'
		extItem2 == null
		   			
	}

	void "test getItem  desc: '#desc' itemDisplay: '#itemDisplay'"() {
		when:
		Person person = new Person(id: 10L, userName: username, fullName: fullName, accountExpired: false, accountLocked: false, accountEnabled: true)
		ExternalItem extItem = ExternalOntologyPerson.getItem(person)
		
		then:
		itemDisplay == extItem.display
		
		where:
		desc                                        		     | itemDisplay			  | username   		| fullName
		"Return concatenation of username and fullname"			 | 'user500 (User 500)'	  | 'user500'      	| 'User 500'
		"Return username since fullname is null"			     | 'user500'	          | 'user500'      	| null
		
	}
	
	void "test findByName"() {
		when:
		extOntologyPerson.findByName("test")
		
		then:
		ExternalOntologyException e = thrown()
		e.message == 'Unimplemented method findByName(String name)'
	}
	
	void "test getExternalURL"() {
		when:
		extOntologyPerson.getExternalURL("1234")
		
		then:
		ExternalOntologyException e = thrown()
		e.message == 'Unimplemented method getExternalURL(String id)'
	}
	
	void "test queryGenerator"() {
		when:
		extOntologyPerson.queryGenerator("term")
		
		then:
		ExternalOntologyException e = thrown()
		e.message == 'Unimplemented method queryGenerator(String term)'
	}
}