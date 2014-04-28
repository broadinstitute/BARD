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

import bard.validation.ext.ExternalOntologyAPI
import bard.validation.ext.ExternalOntologyCreator
import bard.validation.ext.ExternalOntologyException
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory

// to see why the package name is odd, see
// http://jira.grails.org/browse/GRAILS-9016

class PersonCreator implements ExternalOntologyCreator {
    static URI PERSON_URI = new URI("http://www.bard.nih.gov/person#")
    private static Log log = LogFactory.getLog(this.getClass())
    @Override
    public ExternalOntologyAPI create(URI uri, Properties props) throws ExternalOntologyException {
        if(PERSON_URI.equals(uri))
            try {
            return new ExternalOntologyPerson();
            } catch(Exception ex) {
                log.error("failed to create ExternalOntologyPerson", ex)
                throw new RuntimeException(ex)
            }
        else
            return null
    }
}
