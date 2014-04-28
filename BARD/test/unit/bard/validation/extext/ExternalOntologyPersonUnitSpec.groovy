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

package bard.validation.extext

import bard.validation.ext.ExternalItem
import bard.validation.ext.ExternalOntologyException
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
		"Return concatenation of username and fullname"			 | '(user500) User 500'	  | 'user500'      	| 'User 500'
		"Return username since fullname is null"			     | '(user500)'	          | 'user500'      	| null

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
