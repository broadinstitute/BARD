package bard.validation.ext

import org.apache.commons.lang.StringUtils

import bard.db.people.Person

class ExternalOntologyPerson extends ExternalOntologyAPI {	

	@Override
	public ExternalItem findById(String id) throws ExternalOntologyException {
//		String cleanId = cleanId(id);
		String cleanId = StringUtils.trimToEmpty(id)
		ExternalItem item = null
		if( StringUtils.isNotBlank(cleanId)){
			def person = Person.get(cleanId)
			item = getItem(person)
		}
		return item		
	}

	@Override
	public List<ExternalItem> findMatching(String name, int limit) throws ExternalOntologyException {
//		String cleanName = cleanName(name);
		String cleanName = StringUtils.trimToEmpty(name)
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
