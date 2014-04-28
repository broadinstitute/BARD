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

import bard.core.rest.spring.compounds.Compound;

/**
 * @author Jacob Asiedu
 */
public class Probe implements Serializable {
    // private static final long serialVersionUID = 8196705055192706779L;
    final Long cid;
    final String probeId;
    final String url;
    final String smiles;
    final Long bardProjectId
    final Long capProjectId

    public Probe(Compound compound) {
        this(compound.cid, compound.probeId, compound.url, compound.smiles, compound.bardProjectId, compound.capProjectId)
    }

    public Probe(Long cid, String probeId, String url, String smiles, Long bardProjectId, Long capProjectId) {
        this.cid = cid;
        this.probeId = probeId;
        this.url = url;
        this.smiles = smiles;
        this.bardProjectId = bardProjectId
        this.capProjectId = capProjectId
    }

    public Long getCapProjectId() {
        return this.capProjectId
    }

    public Long getBardProjectId() {
        return this.bardProjectId
    }
    /**
     *
     * @return String
     */
    public String getUrl() {
        return url;
    }

    /**
     *
     * @return String
     */
    public String getSmiles() {
        return smiles;
    }

    /**
     *
     * @return String
     */
    public String getProbeId() {
        return probeId;
    }

    /**
     *
     * @return Long
     */
    public Long getCid() {
        return cid;
    }

}
