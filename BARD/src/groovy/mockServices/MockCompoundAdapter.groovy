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

package mockServices

import bard.core.interfaces.CompoundAdapterInterface
import bard.core.rest.spring.compounds.ProbeAnnotation

/**
 * Created with IntelliJ IDEA.
 * User: jasiedu
 * Date: 3/10/13
 * Time: 8:36 PM
 * To change this template use File | Settings | File Templates.
 */
class MockCompoundAdapter implements CompoundAdapterInterface {

    Long pubChemCID = 354624
    String structureSMILES = "NC1=NC(Cl)=NC2=C1N=CN2C1OC(CO)C(O)C1F"
    String name = "5-(6-amino-2-chloropurin-9-yl)-4-fluoro-2-(hydroxymethyl)oxolan-3-ol"
    Double mwt
    String formula
    Double exactMass
    Integer hbondDonor
    Integer hbondAcceptor
    Integer rotatable
    Double tpsa
    String iupacName
    String compoundClass
    Integer numAssays
    Integer numActiveAssays
    Long id
    /*
    * MolecularData interface
    */

    public String formula() { return formula ?: "C9H5Cl2NO" }

    public Double mwt() { return this.mwt ?: new Double(214.048) }

    public Double exactMass() { return this.exactMass ?: new Double(212.975) }
    @Override
    public boolean hasProbeAnnotations(){
        return false
    }
    @Override
    Integer hbondDonor() {
        return this.hbondDonor ?: 2  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    Integer hbondAcceptor() {
        return this.hbondAcceptor ?: 2  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    Integer rotatable() {
        return this.rotatable ?: 1  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Double TPSA() {
        return this.tpsa ?: new Double(33.1);
    }

    public Double logP() {
        return new Double(0.00);
    }

    @Override
    String getIupacName() {
        return this.iupacName ?: "propan-2-ol"  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    String getUrl() {
        return "http://www.compound.com"  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    Integer getComplexity() {
        return 1  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    String getCompoundClass() {
        return this.compoundClass ?: "Drug"  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    int getNumberOfAssays() {
        return numAssays ?: 10  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    int getNumberOfActiveAssays() {
        return numActiveAssays ?: 5  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    String resourcePath() {
        return "/compound/223"  //To change body of implemented methods use File | Settings | File Templates.
    }


    @Override
    boolean isDrug() {
        return getCompoundClass() == "Drug"  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    List<ProbeAnnotation> getProbeAnnotations() {
        return null  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    ProbeAnnotation getProbeCid() {
        return null  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    ProbeAnnotation getProbe() {
        return null  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    ProbeAnnotation getProbeSid() {
        return null  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    String getProbeId() {
        return 2  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    boolean isProbe() {
        return true  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    Long getId() {
        return this.id ?: pubChemCID ?: 2  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    String getHighlight() {
        return "Score: 2.0, Matched Field: Name"
    }
}
