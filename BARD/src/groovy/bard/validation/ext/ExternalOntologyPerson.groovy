package bard.validation.ext

import java.util.List
import java.util.jar.Pack200;

import bard.db.people.Person
import org.apache.commons.lang.StringUtils;

class ExternalOntologyPerson extends ExternalOntologyAPI {
	
	public static ExternalOntologyPerson PERSON_INSTANCE;
	
	static {
		PERSON_INSTANCE = new ExternalOntologyPerson();
	}

	@Override
	public ExternalItem findById(String id) throws ExternalOntologyException {
//		String cleanId = cleanId(id);
		String cleanId = StringUtils.trimToEmpty(id);
		if( StringUtils.isBlank(cleanId))
			return null;
		def person = Person.get(cleanId)
		if(person){
			ExternalItem extItem = new ExternalItem(person.id.toString(), person.userName)
			return extItem
		}
		else
			return null;
	}

	@Override
	public ExternalItem findByName(String name) throws ExternalOntologyException {
		throw new ExternalOntologyException("Unimplemented method findByName(String name)")
	}

	@Override
	public List<ExternalItem> findMatching(String name, int limit) throws ExternalOntologyException {
//		String cleanName = cleanName(name);
		String cleanName = StringUtils.trimToEmpty(name);
		if( StringUtils.isBlank(cleanName))
			return Collections.EMPTY_LIST;
		String likeTerm = "%" + cleanName + "%"
		List<Person> persons = Person.findAllByUserNameIlikeOrFullNameIlike( likeTerm, likeTerm, [max: limit] )
		if(persons){			
			List<ExternalItem> items = new ArrayList<ExternalItem>();
			for(Person p in persons){
				// Display is the concatenation of username and fullname
				String display = p.userName + (p.fullName ? (" (" + p.fullName + ")") : "")
				ExternalItem extItem = new ExternalItem(p.id.toString(), display)				
				items.add(extItem)
			}
			return items			
		}
		else
			return Collections.EMPTY_LIST;
	}

	@Override
	public String getExternalURL(String id) {
		throw new ExternalOntologyException("Unimplemented method getExternalURL(String id)")
	}

	@Override
	public String queryGenerator(String term) {
		throw new ExternalOntologyException("Unimplemented method queryGenerator(String term)")
	}

}
