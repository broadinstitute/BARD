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
import java.net.URISyntaxException;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import bard.validation.ext.util.GOUtil;

public class RegisteringExternalOntologyFactory implements ExternalOntologyFactory {

	private static RegisteringExternalOntologyFactory instance;

	public static ExternalOntologyFactory getInstance() throws ExternalOntologyException {
		if( instance == null )
			synchronized(RegisteringExternalOntologyFactory.class) {
				if( instance == null ) {
					instance = new RegisteringExternalOntologyFactory();
					instance.initialize();
				}
			}
		return instance;
	}

	protected void initialize() throws ExternalOntologyException {
		getCreators().add(new ExternalOntologyNCBI.NCBICreator());
		// instance.getCreators().add( new ExternalOntologyOLS.OLSCreator() );
		getCreators().add(new ExternalOntologyGO.GOCreator(GOUtil.getEBIDataSource()));
		getCreators().add(new ExternalOntologyGOGeneProduct.GOCreator(GOUtil.getEBIDataSource()));
		getCreators().add(new ExternalOntologyUniprot.UniprotCreator());
		getCreators().add(new ExternalOntologyATCC.ATCCCreator());
		getCreators().add(new ExternalOntologyDisease.DiseaseCreator());
	}

	private List<ExternalOntologyCreator> creators = new LinkedList<ExternalOntologyCreator>();

	public List<ExternalOntologyCreator> getCreators() {
		return creators;
	}

	public ExternalOntologyAPI getExternalOntologyAPI(String externalSite) throws ExternalOntologyException {
		return getExternalOntologyAPI(externalSite, new Properties());
	}

	public ExternalOntologyAPI getExternalOntologyAPI(String externalSite, Properties props) throws ExternalOntologyException {
		URI uri = null;
		try {
			uri = new URI(externalSite);
		} catch (URISyntaxException ex) {
			throw new ExternalOntologyException(ex);
		}
		uri.normalize();
		return getExternalOntologyAPI(uri, props);
	}

	public ExternalOntologyAPI getExternalOntologyAPI(URI externalSite, Properties props) throws ExternalOntologyException {
		for (ExternalOntologyCreator creator : getCreators()) {
			ExternalOntologyAPI api = creator.create(externalSite, props);
			if (api != null)
				return api;
		}
		return null;
	}
}
