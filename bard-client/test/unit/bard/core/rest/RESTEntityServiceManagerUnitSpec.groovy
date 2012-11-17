package bard.core.rest

import bard.core.interfaces.EntityService
import spock.lang.Specification
import spock.lang.Unroll
import bard.core.*

@Unroll
class RESTEntityServiceManagerUnitSpec extends Specification {
    RESTEntityServiceManager restEntityServiceManager


    void setup() {
        this.restEntityServiceManager = new RESTEntityServiceManager("base")
    }

    void tearDown() {
        // Tear down logic here
    }

    void "getServices()"() {

        when:
        final List services = this.restEntityServiceManager.getServices()
        then:
        assert services.size() == 5
    }

    void "getService #label"() {

        when:
        final EntityService<? extends bard.core.Entity> service = this.restEntityServiceManager.getService(clazz)
        then:
        assert service?.getClass() == expectedClazz
        where:
        label             | clazz            | expectedClazz
        "With Assay"      | Assay.class      | RESTAssayService.class
        "With Compound"   | Compound.class   | RESTCompoundService.class
        "With Experiment" | Experiment.class | RESTExperimentService.class
        "With Substancee" | Substance.class  | RESTSubstanceService.class
        "With Project"    | Project.class    | RESTProjectService.class
        "With Biology"    | Biology.class    | null

    }
}
