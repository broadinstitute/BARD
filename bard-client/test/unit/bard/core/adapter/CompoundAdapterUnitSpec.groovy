package bard.core.adapter

import bard.core.Compound
import bard.core.DataSource
import bard.core.Format
import bard.core.MolecularValue
import bard.core.impl.MolecularDataJChemImpl
import bard.core.interfaces.MolecularData
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

    void "test set Compound with Molecular Value"() {
        given:
        final Compound compound = new Compound("name")
        final MolecularData md = new MolecularDataJChemImpl();
        md.setMolecule("CC");
        compound.addValue(new MolecularValue(new DataSource("name"), Compound.MolecularValue, md));
        when:
        final CompoundAdapter compoundAdapter = new CompoundAdapter(compound)
        then:
        assert compoundAdapter.name
        assert !compoundAdapter.getProbeId()
        assert !compoundAdapter.isDrug()
        assert !compoundAdapter.isProbe()
        assert compoundAdapter.compound
        assert !compoundAdapter.getPubChemCID()
        assert compoundAdapter.getStructureMOL()
        assert compoundAdapter.formula()
        assert compoundAdapter.getStructureNative()
        assert compoundAdapter.formula()
        assert compoundAdapter.mwt()
        assert compoundAdapter.exactMass()
        assert !compoundAdapter.hbondDonor()
        assert !compoundAdapter.hbondAcceptor()
        assert !compoundAdapter.rotatable()
        assert compoundAdapter.definedStereo() == 0
        assert compoundAdapter.stereocenters() == 0
        assert !compoundAdapter.TPSA()
        assert !compoundAdapter.logP()
        assert !compoundAdapter.ruleOf5()
        assert compoundAdapter.toFormat(Format.MOL)
//        assert !compoundAdapter.fingerprint()
        assert !compoundAdapter.toImage(1, 1)
    }

    void "test set Molecule"() {
        given:
        final Compound compound = new Compound("name")
        final MolecularData md = new MolecularDataJChemImpl();
        final MolecularValue value = new MolecularValue(new DataSource("name"), Compound.MolecularValue, md)
        compound.addValue(value);
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
        assert compoundAdapter.getStructureMOL()
        assert compoundAdapter.formula()
        assert compoundAdapter.getStructureNative()
        assert compoundAdapter.formula()
        assert compoundAdapter.mwt()
        assert compoundAdapter.exactMass()
        assert !compoundAdapter.hbondDonor()
        assert !compoundAdapter.hbondAcceptor()
        assert !compoundAdapter.rotatable()
        assert compoundAdapter.definedStereo() == 0
        assert compoundAdapter.stereocenters() == 0
        assert !compoundAdapter.TPSA()
        assert !compoundAdapter.logP()
        assert !compoundAdapter.ruleOf5()
        assert compoundAdapter.toFormat(Format.MOL)
//        assert !compoundAdapter.fingerprint()
        assert !compoundAdapter.toImage(1, 1)
    }

    void "test set Molecule with no Compound"() {
        given:
        final CompoundAdapter compoundAdapter = new CompoundAdapter()
        when:
        compoundAdapter.setMolecule("CC")
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

