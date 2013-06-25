package bard.validation.ext

import bard.validation.extext.ExternalOntologyPerson
import grails.buildtestdata.mixin.Build
import spock.lang.Specification
import spock.lang.Unroll

import bard.db.people.Person

@Unroll
@Build([Person])
class ExternalOntologyPersonUnitSpec extends Specification {

	ExternalOntologyPerson extOntologyPerson

	def setup() {
		extOntologyPerson = new ExternalOntologyPerson()
	}

	def cleanup() {
	}

	void "test null returned for null person"() {
		when:
		ExternalItem extItem2 = ExternalOntologyPerson.getItem(null)

		then:
		extItem2 == null
	}

	void "test id and display populated for getItem  desc: '#desc' itemDisplay: '#itemDisplay'"() {
		when:
		Person person = Person.build( userName: username, fullName: fullName)
		ExternalItem extItem = ExternalOntologyPerson.getItem(person)

		then:
        extItem.id == person.id.toString()
		extItem.display == itemDisplay

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