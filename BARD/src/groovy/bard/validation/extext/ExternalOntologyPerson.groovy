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
import bard.validation.ext.ExternalOntologyAPI
import bard.validation.ext.ExternalOntologyException
import org.apache.commons.lang.math.NumberUtils

import java.net.URI;
import java.util.Properties;

import org.apache.commons.lang.StringUtils

import bard.db.people.Person
import bard.validation.ext.ExternalOntologyCreator

// to see why the package name is odd, see
// http://jira.grails.org/browse/GRAILS-9016

class ExternalOntologyPerson implements ExternalOntologyAPI {
	
	public static String PERSON_URL = "http://www.bard.nih.gov/person#"
    private static int DEFAULT_LIMIT = 50
	
	@Override
	public ExternalItem findById(String id) throws ExternalOntologyException {
		String cleanId = cleanId(id);
		ExternalItem item = null
		if( StringUtils.isNotBlank(cleanId)){
			def person = Person.findById(cleanId)
			item = getItem(person)
		}
		return item		
	}

	@Override
	public List<ExternalItem> findMatching(String name, int limit) throws ExternalOntologyException {
        final List<ExternalItem> externalItems = [] as List<ExternalItem>
		final String cleanName = cleanName(name);
		if( StringUtils.isNotBlank(cleanName)){
            if(matchesId(cleanName)){
                final ExternalItem itemById = findById(cleanName)
                if(itemById){
                    externalItems.add(itemById)
                }
            }
            if(externalItems.isEmpty()){
                String likeTerm = "%" + StringUtils.lowerCase(cleanName) + "%"
                List<Person> persons = Person.findAll("from Person as p where lower(p.userName) like :username or lower(p.fullName) like :fullname order by lower(p.userName) asc", [username: likeTerm, fullname: likeTerm, max:limit])
                for(Person p in persons){
                    externalItems.add(getItem(p))
                }
            }

		}
		return externalItems
	}
	
	@Override
	public ExternalItem findByName(String name) throws ExternalOntologyException {
		throw new ExternalOntologyException("Unimplemented method findByName(String name)")
	}

    @Override
    List<ExternalItem> findMatching(String s) throws ExternalOntologyException {
        return findMatching(s, DEFAULT_LIMIT)
    }

    @Override
	public String getExternalURL(String id) {
		throw new ExternalOntologyException("Unimplemented method getExternalURL(String id)")
	}

	@Override
	public String queryGenerator(String term) {
		throw new ExternalOntologyException("Unimplemented method queryGenerator(String term)")
	}

    @Override
    String cleanId(String s) {
        return StringUtils.trimToEmpty(s)  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    String cleanName(String s) {
        return StringUtils.trimToEmpty(s)  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    boolean matchesId(String s) {
        return NumberUtils.isDigits(s);
    }

    @Override
    boolean validate(String name, String id) throws ExternalOntologyException {
        ExternalItem item = findByName(cleanName(name));
        ExternalItem item2 = findById(cleanId(id));
        return item.equals(item2);
    }

    public static ExternalItem getItem(Person p){
		// Display is the concatenation of username and fullname
		ExternalItem extItem;
		if(p){
			String fullname = StringUtils.trimToEmpty(p.fullName)
			String display = StringUtils.trimToEmpty((p.userName != null ? "(${p.userName}) " : "") + fullname)
			extItem = new bard.validation.ext.ExternalItemImpl(p.id as String,display)
		}		
		return extItem;
	}

}
