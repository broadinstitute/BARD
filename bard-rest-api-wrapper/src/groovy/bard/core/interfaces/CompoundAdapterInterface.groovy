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

package bard.core.interfaces

import bard.core.rest.spring.compounds.ProbeAnnotation

public interface CompoundAdapterInterface {
    public String getHighlight();
    public boolean isDrug();
    public List<ProbeAnnotation> getProbeAnnotations();
    ProbeAnnotation getProbeCid();
    ProbeAnnotation getProbe();
    ProbeAnnotation getProbeSid();
    public boolean hasProbeAnnotations();
    public String getProbeId();

    public boolean isProbe();

    public Long getId();

    public Long getPubChemCID();

    public String getStructureSMILES();

    public Double mwt();

    public Double exactMass();

    public Integer hbondDonor();

    public Integer hbondAcceptor();

    public Integer rotatable();

    public Double TPSA();

    public Double logP();


    public String getName();

    public String getIupacName();

    public String getUrl();

    public Integer getComplexity();

    public String getCompoundClass();

    public int getNumberOfAssays();

    public int getNumberOfActiveAssays();

    public String resourcePath();
}
