package bard.core.adapter

import bard.core.Assay
import bard.core.interfaces.AssayCategory
import spock.lang.Specification
import spock.lang.Unroll

@Unroll
class AssayAdapterUnitSpec extends Specification {
    void setup() {

    }

    void tearDown() {
        // Tear down logic here
    }

    void "test Constructor()"() {

        given:
        Assay assay = new Assay("name")

        when:
        AssayAdapter assayAdapter = new AssayAdapter(assay)
        then:
        assert assayAdapter.name == "name"
    }

    void "test getters"() {

        given:
        AssayAdapter assayAdapter = new AssayAdapter()
        when:

        Assay assay = new Assay("name")
        Long assayId = new Long(588636)
        assay.setId(assayId)
        assayAdapter.assay = assay
        assayAdapter.assay.category = AssayCategory.MLPCN
        assayAdapter.assay.protocol = "Please see linked AIDs for a detailed description of each assay."
        assayAdapter.assay.comments = "This project is on-going and will be updated at a later point with our findings."


        then:
        assert assayAdapter.getAnnotations().isEmpty()
        assert assayAdapter.name == "name"
        assert !assayAdapter.getDescription()
        assert !assayAdapter.getRole()
        assert !assayAdapter.getType()
        assert assayAdapter.getProtocol() == "Please see linked AIDs for a detailed description of each assay."
        assert assayAdapter.getComments() == "This project is on-going and will be updated at a later point with our findings."
        assert assayAdapter.getCategory() == AssayCategory.MLPCN
    }


}

