package dataexport.experiment

import bard.db.experiment.Experiment
import grails.plugin.spock.IntegrationSpec
import groovy.xml.MarkupBuilder

import javax.xml.XMLConstants
import javax.xml.transform.stream.StreamSource
import javax.xml.validation.Schema
import javax.xml.validation.SchemaFactory
import javax.xml.validation.Validator

class ExperimentExportServiceIntegrationSpec extends IntegrationSpec {
    static final String BARD_EXPERIMENT_EXPORT_SCHEMA = "test/integration/dataexport/experiment/experimentSchema.xsd"

    ExperimentExportService experimentExportService
    Writer writer
    MarkupBuilder markupBuilder

    void setup() {
        this.writer = new StringWriter()
        this.markupBuilder = new MarkupBuilder(this.writer)

    }

    void tearDown() {
        // Tear down logic here
    }

    void "test generate and validate Experiment"() {
        given: "Given an Experiment"
        final Experiment experiment = Experiment.get(1)

        when: "A service call is made to generate the experiment"
        this.experimentExportService.generateExperiment(this.markupBuilder, experiment.id)
        then: "An XML is generated that conforms to the expected XML"
        final SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI)
        final Schema schema = factory.newSchema(new StreamSource(new FileReader(BARD_EXPERIMENT_EXPORT_SCHEMA)))
        final Validator validator = schema.newValidator()
        validator.validate(new StreamSource(new StringReader(this.writer.toString())))

    }

    void "test generate and validate Experiments"() {
        given: "Given that 2 experiments exists in the database"


        when: "A service call is made to generate the experiments"
        this.experimentExportService.generateExperiments(this.markupBuilder, 0)
        then: "An XML is generated that conforms to the expected XML"
        println this.writer.toString()
        final SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI)
        final Schema schema = factory.newSchema(new StreamSource(new FileReader(BARD_EXPERIMENT_EXPORT_SCHEMA)))
        final Validator validator = schema.newValidator()
        validator.validate(new StreamSource(new StringReader(this.writer.toString())))
    }

}
