package bard.core.adapter

import bard.core.rest.spring.compounds.Compound
import spock.lang.Specification
import spock.lang.Unroll
import spock.lang.Shared
import com.fasterxml.jackson.databind.ObjectMapper

@Unroll
class CompoundAdapterUnitSpec extends Specification {
    @Shared
    ObjectMapper objectMapper = new ObjectMapper()

    static final String COMPOUND_EXPANDED = '''
   {
       "cid": 2722,
       "probeId":null,
       "url": "http://pubchem.org",
       "smiles": "OC1=C(Cl)C=C(Cl)C2=C1N=CC=C2",
       "name": "Chloroxine",
       "iupacName": "5,7-dichloroquinolin-8-ol",
       "mwt": 214.048,
       "tpsa": 33.1,
       "exactMass": 212.975,
       "xlogp": 3.5,
       "complexity": 191,
       "rotatable": 0,
       "hbondAcceptor": 2,
       "hbondDonor": 1,
       "compoundClass": "Drug",
       "numAssay": 500,
       "numActiveAssay": 60,
       "highlight": null,
       "resourcePath": "/compounds/2722"
   }
'''



    void "test compound Adapter"() {
        given:
        final Compound compound = objectMapper.readValue(COMPOUND_EXPANDED, Compound.class)

        when:
        final CompoundAdapter compoundAdapter = new CompoundAdapter(compound)
        then:
        assert compoundAdapter.isDrug()
        assert !compoundAdapter.getProbeId()
        assert !compoundAdapter.isProbe()
        assert compoundAdapter.getId() ==2722
        assert compoundAdapter.getPubChemCID()==2722
        assert compoundAdapter.getStructureSMILES() == "OC1=C(Cl)C=C(Cl)C2=C1N=CC=C2"
        assert !compoundAdapter.getStructureMOL()
        assert !compoundAdapter.formula()
        assert compoundAdapter.mwt()==214.048
        assert compoundAdapter.exactMass() ==212.975
        assert compoundAdapter.hbondDonor()==1
        assert compoundAdapter.hbondAcceptor()==2
        assert compoundAdapter.rotatable()==0
        assert !compoundAdapter.definedStereo()
        assert !compoundAdapter.stereocenters()

        assert compoundAdapter.TPSA()==33.1
        assert compoundAdapter.logP()==3.5
        assert compoundAdapter.name=="Chloroxine"
        assert compoundAdapter.getIupacName() =="5,7-dichloroquinolin-8-ol"
        assert compoundAdapter.url=="http://pubchem.org"
        assert compoundAdapter.getComplexity()==191
        assert compoundAdapter.getCompoundClass()=="Drug"
        assert compoundAdapter.getNumberOfAssays()==500
        assert compoundAdapter.getNumberOfActiveAssays()==60
        assert compoundAdapter.resourcePath() == "/compounds/2722"
        assert !compoundAdapter.getStructureNative()
        assert !compoundAdapter.formula()


    }
}

