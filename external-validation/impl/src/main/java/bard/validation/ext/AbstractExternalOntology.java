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

import java.util.List;

import org.apache.commons.lang.StringUtils;

/**
 * Contract for External Ontology implementations
 * 
 * @author southern
 * 
 */
public abstract class AbstractExternalOntology implements ExternalOntologyAPI {

    /**
	 * given term is searched against the underlying external ontology system
	 * the term is first run through the <b>queryGenerator</b> method so it is
	 * suitable for the given system
	 */
	@Override
    public List<ExternalItem> findMatching(String term) throws ExternalOntologyException {
		return findMatching(term, -1);
	}

    /**
	 * formats a query term. E.g. adds % for SQL LIKE, trims the term etc.
	 */
	@Override
    public String queryGenerator(String term) {
		return StringUtils.trimToEmpty(term);
	}
	
	@Override
    public String cleanId(String id) {
		return StringUtils.trimToEmpty(id);
	}
	
	@Override
    public String cleanName(String name) {
		return StringUtils.trimToEmpty(name);
	}

    @Override
    public boolean validate(String name, String id) throws ExternalOntologyException {
		ExternalItem item = findByName(cleanName(name));
		ExternalItem item2 = findById(cleanId(id));
		return item.equals(item2);
	}
}
