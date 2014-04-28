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

package bard.core

import spock.lang.Specification
import spock.lang.Unroll
import bard.core.rest.spring.compounds.Compound

@Unroll
class ProbeUnitSpec extends Specification {

    void "test Probe"() {
        given:
        final String smiles = "CC"
        final String probeId = "ML108"
        final Long cid = 444
        final String url = "http://www.bard.com"
        final Long bardProjectId = 2
        final Long capProjectId = 1
        when:
        final Probe probe = new Probe(cid, probeId, url, smiles, bardProjectId, capProjectId)
        then:
        probe.cid == cid
        probe.probeId == probeId
        probe.smiles == smiles
        probe.url == url
        probe.bardProjectId == bardProjectId
        probe.capProjectId == capProjectId
    }
    void "test Probe with Compound"() {
        given:
        final String smiles = "CC"
        final String probeId = "ML108"
        final Long cid = 444
        final String url = "http://www.bard.com"
        final Long bardProjectId = 2
        final Long capProjectId = 1
        Compound compound = new Compound(smiles: smiles, cid: cid, probeId: probeId,url: url, bardProjectId: bardProjectId, capProjectId: capProjectId)
        when:
        final Probe probe = new Probe(compound)
        then:
        probe.cid == cid
        probe.probeId == probeId
        probe.smiles == smiles
        probe.url == url
        probe.bardProjectId == bardProjectId
        probe.capProjectId == capProjectId
    }

}

