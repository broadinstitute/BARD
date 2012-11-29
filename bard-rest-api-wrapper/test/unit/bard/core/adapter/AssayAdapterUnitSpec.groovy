package bard.core.adapter

import bard.core.interfaces.AssayCategory
import bard.core.rest.spring.assays.Assay
import spock.lang.Specification
import spock.lang.Unroll
import bard.core.interfaces.AssayType
import bard.core.interfaces.AssayRole

@Unroll
class AssayAdapterUnitSpec extends Specification {
    void setup() {

    }

    void tearDown() {
        // Tear down logic here
    }

    void "test Constructor()"() {

        given:
        Assay assay = new Assay()
        final String name = "name"
        assay.name = name

        when:
        AssayAdapter assayAdapter = new AssayAdapter(assay)
        then:
        assert assayAdapter.name == name
    }

    void "test getters"() {

        given:
        final String protocol = "Please see linked AIDs for a detailed description of each assay."
        final String comments = "This project is on-going and will be updated at a later point with our findings."
        final String name = "name"
        Long assayId = new Long(588636)
        Assay assay = new Assay()
        assay.name = name
        assay.setAssayId(assayId)
        assay.category = 2
        assay.protocol = protocol
        assay.comments = comments

        when:
        AssayAdapter assayAdapter = new AssayAdapter(assay)

        then:
        assert assayAdapter.getAnnotations().isEmpty()
        assert assayAdapter.name == name
        assert !assayAdapter.getDescription()
        assert assayAdapter.getRole() == AssayRole.Primary
        assert assayAdapter.getType()  == AssayType.Other
        assert assayAdapter.getProtocol() == protocol
        assert assayAdapter.getComments() == comments
        assert assayAdapter.getCategory() == AssayCategory.MLPCN
    }


}

