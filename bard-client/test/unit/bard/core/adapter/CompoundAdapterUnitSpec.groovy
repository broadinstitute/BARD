package bard.core.adapter

import bard.core.Compound
import bard.core.Format
import spock.lang.Specification
import spock.lang.Unroll

@Unroll
class CompoundAdapterUnitSpec extends Specification {
    void setup() {

    }

    void tearDown() {
        // Tear down logic here
    }

    void "test Constructor()"() {

        given:
        Compound compound = new Compound("name")
        when:
        CompoundAdapter compoundAdapter = new CompoundAdapter(compound)
        then:
        assert compoundAdapter.name == "name"
        assert !compoundAdapter.getProbeId()
        assert !compoundAdapter.isDrug()
        assert !compoundAdapter.isProbe()
        assert compoundAdapter.compound
        assert !compoundAdapter.getPubChemCID()
        assert !compoundAdapter.getStructureMOL()

    }

    void "test No Args Constructor()"() {
        when:
        CompoundAdapter compoundAdapter = new CompoundAdapter()
        then:
        assert !compoundAdapter.name
        assert !compoundAdapter.getProbeId()
        assert !compoundAdapter.isDrug()
        assert !compoundAdapter.isProbe()
        assert !compoundAdapter.compound
        assert !compoundAdapter.getPubChemCID()
        assert !compoundAdapter.getStructureMOL()
        assert !compoundAdapter.formula()
        assert !compoundAdapter.getStructureNative()
        assert !compoundAdapter.formula()
        assert !compoundAdapter.mwt()
        assert !compoundAdapter.exactMass()
        assert !compoundAdapter.hbondDonor()
        assert !compoundAdapter.hbondAcceptor()
        assert !compoundAdapter.rotatable()
        assert !compoundAdapter.definedStereo()
        assert !compoundAdapter.stereocenters()
        assert !compoundAdapter.TPSA()
        assert !compoundAdapter.logP()
        assert !compoundAdapter.ruleOf5()
        assert !compoundAdapter.toFormat(Format.MOL)
        assert !compoundAdapter.fingerprint()
        assert !compoundAdapter.toImage(1, 1)
    }

}

