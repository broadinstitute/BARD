package bard.core.adapter

import bard.core.Format
import bard.core.rest.spring.compounds.Compound
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
        Compound compound = new Compound()
        final String name = "name"
        compound.name = name
        when:
        CompoundAdapter compoundAdapter = new CompoundAdapter(compound)
        then:
        assert compoundAdapter.name == name
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

    void "test set Compound with Molecular Value"() {
        given:
        final Compound compound = new Compound()
        compound.name = "name"
        compound.mwt = 2
        compound.exactMass = 2
        when:
        final CompoundAdapter compoundAdapter = new CompoundAdapter(compound)
        then:
        assert compoundAdapter.name
        assert !compoundAdapter.getProbeId()
        assert !compoundAdapter.isDrug()
        assert !compoundAdapter.isProbe()
        assert compoundAdapter.compound
        assert !compoundAdapter.getPubChemCID()
        assert !compoundAdapter.getStructureMOL()
        assert !compoundAdapter.formula()
        assert !compoundAdapter.getStructureNative()
        assert !compoundAdapter.formula()
        assert compoundAdapter.mwt()
        assert compoundAdapter.exactMass()
        assert !compoundAdapter.hbondDonor()
        assert !compoundAdapter.hbondAcceptor()
        assert !compoundAdapter.rotatable()
        assert !compoundAdapter.TPSA()
        assert !compoundAdapter.logP()
        assert !compoundAdapter.ruleOf5()
        assert !compoundAdapter.toImage(1, 1)
    }

    void "test set Molecule"() {
        given:
        final Compound compound = new Compound()
        compound.name = "name"
        final CompoundAdapter compoundAdapter = new CompoundAdapter(compound)
        when:
        compoundAdapter.setMolecule("CC")
        then:
        assert compoundAdapter.name
        assert !compoundAdapter.getProbeId()
        assert !compoundAdapter.isDrug()
        assert !compoundAdapter.isProbe()
        assert compoundAdapter.compound
        assert !compoundAdapter.getPubChemCID()
        assert !compoundAdapter.formula()
        assert !compoundAdapter.getStructureNative()
        assert !compoundAdapter.formula()
        assert !compoundAdapter.mwt()
        assert !compoundAdapter.exactMass()
        assert !compoundAdapter.hbondDonor()
        assert !compoundAdapter.hbondAcceptor()
        assert !compoundAdapter.rotatable()
        assert !compoundAdapter.TPSA()
        assert !compoundAdapter.logP()
        assert !compoundAdapter.ruleOf5()
        assert !compoundAdapter.toImage(1, 1)
    }

    void "test set Molecule with no Compound"() {
        when:
        final CompoundAdapter compoundAdapter = new CompoundAdapter()
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

