package bard.db.experiment



import grails.test.mixin.*
import org.junit.*
import bard.db.registration.Assay

/**
 * See the API for {@link grails.test.mixin.domain.DomainClassUnitTestMixin} for usage instructions
 */
@TestFor(Experiment)
class ExperimentTests {

    Experiment experiment

    @Before
    public void setUp() {

        mockForConstraintsTests(Experiment)

        experiment = new Experiment(experimentName: "Test", experimentStatus: "Approved", readyForExtraction: "Complete")
        experiment.setAssay(new Assay())
    }

    void testValidConstraints() {

        experiment.validate()
        assertTrue "Experiment is valid " + experiment.errors, experiment.validate()
    }

    void testNoExperimentName() {
        experiment.setExperimentName(null)

        assertFalse "Experiment should not be valid with no name", experiment.validate()
        assertEquals "nullable", experiment.errors["experimentName"]

    }
}
