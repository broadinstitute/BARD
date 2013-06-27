package bard.validation.extext

import bard.validation.ext.ExternalItem
import bard.validation.ext.ExternalOntologyAPI
import bard.validation.ext.ExternalOntologyException

import java.net.URI;
import java.util.Properties;

import org.apache.commons.lang.StringUtils

import bard.db.people.Person
import bard.validation.ext.ExternalOntologyCreator

// to see why the package name is odd, see
// http://jira.grails.org/browse/GRAILS-9016

class ExternalOntologyPerson extends ExternalOntologyAPI {
	
	public static String PERSON_URL = "http://www.bard.nih.gov/person#"
	
	@Override
	public ExternalItem findById(String id) throws ExternalOntologyException {
		String cleanId = cleanId(id);
		ExternalItem item = null
		if( StringUtils.isNotBlank(cleanId)){
			def person = Person.get(cleanId)
			item = getItem(person)
		}
		return item		
	}

	@Override
	public List<ExternalItem> findMatching(String name, int limit) throws ExternalOntologyException {
		String cleanName = cleanName(name);
		List<ExternalItem> items = new ArrayList<ExternalItem>()
		if( StringUtils.isNotBlank(cleanName)){
			String likeTerm = "%" + StringUtils.lowerCase(cleanName) + "%"
			List<Person> persons = Person.findAll("from Person as p where lower(p.userName) like :username or lower(p.fullName) like :fullname order by lower(p.userName) asc", [username: likeTerm, fullname: likeTerm, max:limit])

			for(Person p in persons){
				items.add(getItem(p))
			}
		}
		return items
	}
	
	@Override
	public ExternalItem findByName(String name) throws ExternalOntologyException {
		throw new ExternalOntologyException("Unimplemented method findByName(String name)")
	}


	@Override
	public String getExternalURL(String id) {
		throw new ExternalOntologyException("Unimplemented method getExternalURL(String id)")
	}

	@Override
	public String queryGenerator(String term) {
		throw new ExternalOntologyException("Unimplemented method queryGenerator(String term)")
	}
	
	public static ExternalItem getItem(Person p){
		// Display is the concatenation of username and fullname
		ExternalItem extItem;
		if(p){
			String fullname = StringUtils.trimToEmpty(p.fullName)
			String display = (p.userName != null ? "(${p.userName}) " : "") + fullname
			extItem = new ExternalItem(p.id.toString(), display.trim())
		}		
		return extItem;
	}

}
