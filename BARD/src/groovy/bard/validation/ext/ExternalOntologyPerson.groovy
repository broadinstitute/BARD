package bard.validation.ext

import java.net.URI;
import java.util.Properties;

import org.apache.commons.lang.StringUtils

import bard.db.people.Person
import bard.validation.ext.ExternalOntologyCreator

class ExternalOntologyPerson extends ExternalOntologyAPI {	
	
	public static String PERSON_URL = "http://www.bard.nih.gov/person#"
	
	public static URI PERSON_URI = new URI(PERSON_URL)
	
	public static class PersonCreator implements ExternalOntologyCreator {
		@Override
		public ExternalOntologyAPI create(URI uri, Properties props) throws ExternalOntologyException {
			if(PERSON_URI.equals(uri))
				return new ExternalOntologyPerson();
			else
				return null
		}
		
	}

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
			String display = p.userName + (fullname != "" ? (" (" + p.fullName + ")") : "")
			extItem = new ExternalItem(p.id.toString(), display)
		}		
		return extItem;
	}

}
