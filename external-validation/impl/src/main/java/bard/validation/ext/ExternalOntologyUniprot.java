package bard.validation.ext;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

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

public class ExternalOntologyUniprot extends AbstractExternalOntology {

	public static class UniprotCreator implements ExternalOntologyCreator {
		@Override
		public ExternalOntologyAPI create(URI uri, Properties props) throws ExternalOntologyException {
			String host = uri.getHost();
			if (host.endsWith("uniprot.org"))
				return new ExternalOntologyUniprot();
			return null;
		}
	}

	private UniProtQueryService uniProtQueryService;

    private static final Pattern UNIPROT_ID_PATTERN = Pattern.compile("^[A-Z][0-9A-Z]{5}$");

	public ExternalOntologyUniprot() {
		uniProtQueryService = UniProtJAPI.factory.getUniProtQueryService();
	}

	public ExternalItem findById(String id) throws ExternalOntologyException {
        ExternalItem externalItem = null;
		id = cleanId(id);
		if( StringUtils.isBlank(id))
			return null;
		Query query = UniProtQueryBuilder.buildExactMatchIdentifierQuery(id);
		EntryIterator<UniProtEntry> entryIterator = uniProtQueryService.getEntryIterator(query);
		int resultSize = entryIterator.getResultSize();
		if (resultSize > 1) {
			throw new ExternalOntologyException(String.format("'%s' is not a unique Uniprot identifier", id));
        }
        else if (resultSize == 0 ){
            externalItem = null;
        }
        else {
            externalItem  = getExternalItems(entryIterator, 1).get(0);
            externalItem.setId(id); // should always be the id the user submitted.
        }
		return externalItem;
	}

	public ExternalItem findByName(String name) throws ExternalOntologyException {
		name = cleanName(name);
		if( StringUtils.isBlank(name))
			return null;
		Query query = UniProtQueryBuilder.buildQuery(name);
		EntryIterator<UniProtEntry> entryIterator = uniProtQueryService.getEntryIterator(query);
		if (entryIterator.getResultSize() != 1)
			throw new ExternalOntologyException(String.format("'%s' is not a unique Uniprot protein name", name));
		return getExternalItems(entryIterator, 1).get(0);
	}

	public List<ExternalItem> findMatching(String term, int limit) throws ExternalOntologyException {
        final List<ExternalItem> externalItems = new ArrayList<ExternalItem>();
		final String cleanedTerm = cleanName(term);
        if(StringUtils.isNotBlank(cleanedTerm)){
            if(matchesId(cleanedTerm)){
                final ExternalItem itemById = findById(cleanedTerm);
                if(itemById !=null){
                    externalItems.add(itemById);
                }
            }
            if(externalItems.isEmpty()){
                Query query = UniProtQueryBuilder.buildFullTextSearch(queryGenerator(cleanedTerm));
                EntryIterator<UniProtEntry> entryIterator = uniProtQueryService.getEntryIterator(query);
                externalItems.addAll(getExternalItems(entryIterator, limit));
            }
        }
        return externalItems;
	}

	protected String getDisplay(UniProtEntry entry) {
		ProteinDescription desc = entry.getProteinDescription();
		Name name = desc.getRecommendedName();
		List<Field> fields = name.getFieldsByType(FieldType.FULL);
		if (fields.size() > 0) {
			Field field = fields.get(0);
			return field.getValue();
		} else {
			List<Name> names = desc.getSubNames();
			for (Name name2 : names) {
				fields = name2.getFieldsByType(FieldType.FULL);
				if (fields.size() > 0)
					return fields.get(0).getValue();
			}
		}
		return null;
	}

	protected List<ExternalItem> getExternalItems(EntryIterator<UniProtEntry> entryIterator, int limit) {
		int capacity = limit > 0 ? limit : entryIterator.getResultSize();
		List<ExternalItem> items = new ArrayList(capacity);
		for (UniProtEntry entry : entryIterator) {
			String id = entry.getPrimaryUniProtAccession().getValue();
			String display = getDisplay(entry);
			ExternalItem item = new ExternalItemImpl(id, display);
			items.add(item);
			if (limit > 0 && items.size() >= limit)
				break;
		}
		return items;
	}

	public String getExternalURL(String id) {
		return String.format("http://www.uniprot.org/uniprot/%s", cleanId(id));
	}

    /**
     *
     * @param potentialId Note, this have already been cleaned if needed
     * @return true if it looks like an Id
     */
    @Override
    public boolean matchesId(String potentialId) {
        return UNIPROT_ID_PATTERN.matcher(potentialId).matches();
    }
}