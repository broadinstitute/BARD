package bard.core

import bard.core.impl.MolecularDataJChemImpl
import bard.core.interfaces.MolecularData
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

@Unroll
class MolecularValueUnitSpec extends Specification {
    @Shared String name = "name"
    @Shared DataSource dataSource = new DataSource(name, "version", "url")
    @Shared String id = "ID"
    @Shared Value parent = new Value(dataSource)

    void setup() {

    }

    void tearDown() {
        // Tear down logic here
    }

    void "test Constructors #label"() {
        when:
        MolecularValue currentMolecularValue = molecularValue
        then:
        currentMolecularValue.getId() == expectedId
        currentMolecularValue.getSource().getName() == dataSourceName
        assert currentMolecularValue.getValue()
        where:
        label                               | molecularValue                     | expectedId                 | dataSourceName | expectedValue
        "2 arg constructor with datasource" | new MolecularValue(dataSource, id) | id                         | name           | null
        "2 arg constructor"                 | new MolecularValue(parent, id)     | id                         | name           | null
        "1 arg constructor"                 | new MolecularValue(parent)         | "bard.core.MolecularValue" | name           | null
        "1 arg constructor with datasource" | new MolecularValue(dataSource)     | null                       | name           | null

    }

    void "test Empty Constructors"() {
        when:
        MolecularValue currentMolecularValue = molecularValue
        then:
        currentMolecularValue.getId() == null
        assert currentMolecularValue.getValue()
        where:
        label                   | molecularValue
        "Empty arg constructor" | new MolecularValue()
    }
    void "test set Molecule"() {
        given:
        MolecularValue currentMolecularValue = new MolecularValue()
        when:
        currentMolecularValue.setMolecule("CC");
        then:
        assert !currentMolecularValue.logP()
        assert !currentMolecularValue.ruleOf5()
        assert !currentMolecularValue.toImage(2,2)


    }

    void "test setters"() {
        given:
        MolecularValue currentMolecularValue = new MolecularValue()
        MolecularData md = new MolecularDataJChemImpl();
        md.setMolecule("CC");
        when:
        currentMolecularValue.setValue(md)
        then:
        assert !currentMolecularValue.logP()
        assert !currentMolecularValue.ruleOf5()
        assert !currentMolecularValue.toImage(2,2)


    }

}

