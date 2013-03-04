package bard.validation.ext;

import java.util.ArrayList;
import java.util.List;

import uk.ac.ebi.kraken.interfaces.uniprot.ProteinDescription;
import uk.ac.ebi.kraken.interfaces.uniprot.UniProtEntry;
import uk.ac.ebi.kraken.interfaces.uniprot.description.Field;
import uk.ac.ebi.kraken.interfaces.uniprot.description.FieldType;
import uk.ac.ebi.kraken.interfaces.uniprot.description.Name;
import uk.ac.ebi.kraken.uuw.services.remoting.EntryIterator;
import uk.ac.ebi.kraken.uuw.services.remoting.Query;
import uk.ac.ebi.kraken.uuw.services.remoting.UniProtJAPI;
import uk.ac.ebi.kraken.uuw.services.remoting.UniProtQueryBuilder;
import uk.ac.ebi.kraken.uuw.services.remoting.UniProtQueryService;

public class ExternalOntologyUniprot extends ExternalOntologyAPI {
	
	private UniProtQueryService uniProtQueryService;
	
	public ExternalOntologyUniprot() {
		uniProtQueryService = UniProtJAPI.factory.getUniProtQueryService();
	}

	public ExternalItem findById(String id) throws ExternalOntologyException {
		Query query = UniProtQueryBuilder.buildExactMatchIdentifierQuery(id);
		EntryIterator<UniProtEntry> entryIterator = uniProtQueryService.getEntryIterator(query);
		int resultSize = entryIterator.getResultSize();
		if (resultSize != 1)
			throw new ExternalOntologyException(String.format("'%s' is not a unique Uniprot identifier", id));
		return getExternalItems(entryIterator).get(0);
	}

	public ExternalItem findByName(String name) throws ExternalOntologyException {
		Query query = UniProtQueryBuilder.buildQuery(name);
		EntryIterator<UniProtEntry> entryIterator = uniProtQueryService.getEntryIterator(query);
		if (entryIterator.getResultSize() != 1)
			throw new ExternalOntologyException(String.format("'%s' is not a unique Uniprot protein name", name));
		return getExternalItems(entryIterator).get(0);
	}

	public List<ExternalItem> findMatching(String term) throws ExternalOntologyException {
		Query query = UniProtQueryBuilder.buildFullTextSearch(term);
		EntryIterator<UniProtEntry> entryIterator = uniProtQueryService.getEntryIterator(query);
		return getExternalItems(entryIterator);
	}

	
	protected String getDisplay(UniProtEntry entry) {
		ProteinDescription desc = entry.getProteinDescription();
		Name name = desc.getRecommendedName();
		List<Field> fields = name.getFieldsByType(FieldType.FULL);
		if( fields.size() > 0 ) {
			Field field = fields.get(0);
			return field.getValue();
		}
		else {
			List<Name> names = desc.getSubNames();
			for(Name name2: names) {
				fields = name2.getFieldsByType(FieldType.FULL);
				if( fields.size() > 0 )
					return fields.get(0).getValue();
			}
		}
		return null;
	}
	
	protected List<ExternalItem> getExternalItems(EntryIterator<UniProtEntry> entryIterator) {
		List<ExternalItem> items = new ArrayList();
		for (UniProtEntry entry : entryIterator) {
			String id = entry.getPrimaryUniProtAccession().getValue();
			String display = getDisplay(entry);
			ExternalItem item = new ExternalItem(id, display);
			items.add(item);
		}
		return items;
	}

	public String getExternalURL(String id) {
		return String.format("http://www.uniprot.org/uniprot/%s", id);
	}

	public String queryGenerator(String term) {
		term = term.trim();
		return term;
	}

}
