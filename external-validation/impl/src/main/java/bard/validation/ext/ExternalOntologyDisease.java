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

package bard.validation.ext;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;

import uk.ac.ebi.ontocat.OntologyService;
import uk.ac.ebi.ontocat.OntologyService.SearchOptions;
import uk.ac.ebi.ontocat.OntologyTerm;
import uk.ac.ebi.ontocat.file.FileOntologyService;

public class ExternalOntologyDisease extends AbstractExternalOntology {

	public static class DiseaseCreator implements ExternalOntologyCreator {
		@Override
		public ExternalOntologyAPI create(URI uri, Properties props) throws ExternalOntologyException {
			String host = uri.getHost();
			if (host.endsWith("disease-ontology.org"))
				return new ExternalOntologyDisease();
			return null;
		}
	}

	private static OntologyService service;

	public ExternalOntologyDisease() throws ExternalOntologyException {
		service = getService();
	}
	
	protected OntologyService getService() throws ExternalOntologyException {
		if (service == null) {
			synchronized (ExternalOntologyDisease.class) { // 1
				if (service == null) {
					synchronized (ExternalOntologyDisease.class) { // 3
						try {
							service = new FileOntologyService(new URI("http://www.berkeleybop.org/ontologies/doid.obo"));
						} catch (Exception ex) {
							throw new ExternalOntologyException("Could not create Disease Ontology OntoCat service", ex);
						}
					}
				}
			}
		}
		return service;
	}

	public ExternalItem findById(String id) throws ExternalOntologyException {
		try {
			id = cleanId(id);
			if (StringUtils.isBlank(id))
				return null;
			OntologyTerm term = service.getTerm(id);
			if( term == null )
				return null;
			return new ExternalItemImpl(term.getAccession(), term.getLabel());
		} catch (Exception ex) {
			throw new ExternalOntologyException(ex);
		}
	}

	public ExternalItem findByName(String name) throws ExternalOntologyException {
		try {
			name = cleanName(name);
			if (StringUtils.isBlank(name))
				return null;
			List<OntologyTerm> terms = service.searchAll(name, SearchOptions.EXACT);
			if( terms == null || terms.size() != 1)
				return null;
			return new ExternalItemImpl(terms.get(0).getAccession(), terms.get(0).getLabel());
		} catch (Exception ex) {
			throw new ExternalOntologyException(ex);
		}
	}

	public List<ExternalItem> findMatching(String term, int limit) throws ExternalOntologyException {
		try {
			term = cleanName(term);
			if (StringUtils.isBlank(term))
				return Collections.EMPTY_LIST;
			term = term.toLowerCase();
			List<OntologyTerm> terms = service.searchAll(term, SearchOptions.INCLUDE_PROPERTIES);
			List<ExternalItem> items = new ArrayList<ExternalItem>(terms.size());
			for(OntologyTerm oterm: terms) {
				if( oterm.getLabel().toLowerCase().contains(term) )
					items.add( new ExternalItemImpl(oterm.getAccession(), oterm.getLabel()) );
			}
			return items;
		} catch (Exception ex) {
			throw new ExternalOntologyException(ex);
		}
	}

	protected List<ExternalItem> getExternalItems(Map<String, String> map, int limit) {
		List<ExternalItem> items = new ArrayList<ExternalItem>(map.size());
		int count = 0;
		for (Map.Entry<String, String> entry : map.entrySet()) {
			ExternalItem item = new ExternalItemImpl(entry.getKey(), entry.getValue());
			items.add(item);
			if (limit > 0 && ++count >= limit)
				break;
		}
		return items;
	}

	public String getExternalURL(String id) {
		id = cleanId(id);
		return "http://www.disease-ontology.org/";
	}

	public String cleanId(String id) {
		id = super.cleanId(id);
		return id;
	}

    @Override
    public boolean matchesId(String potentialId) {
        return false; // at the moment, not clear on ID format
    }
}
