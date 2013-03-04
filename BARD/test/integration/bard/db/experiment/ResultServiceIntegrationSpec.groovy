package bard.db.experiment

import bard.db.dictionary.Element
import bard.db.registration.AssayContext
import bard.db.registration.AssayContextItem
import bard.db.registration.AssayContextMeasure
import bard.db.registration.AttributeType
import bard.db.registration.ItemService
import bard.db.registration.Measure
import bard.db.registration.PugService
import grails.plugin.spock.IntegrationSpec
import org.apache.commons.io.IOUtils
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils
import org.junit.Before

/**
 * Created with IntelliJ IDEA.
 * User: pmontgom
 * Date: 2/25/13
 * Time: 11:01 AM
 * To change this template use File | Settings | File Templates.
 */
class ResultServiceIntegrationSpec extends IntegrationSpec {
    Substance substance;
    Experiment experiment;
    ResultsService resultsService;

    ExperimentMeasure createMeasure(String label, String statistic = null) {
        Element resultType = Element.findByLabel(label);
        Element statsModifier = null;
        if (statistic != null) {
            statsModifier = Element.findByLabel(statistic);
        }

        Measure measure = Measure.build(assay: experiment.assay, resultType: resultType, statsModifier: statsModifier)
        experiment.assay.measures.add(measure)

        ExperimentMeasure experimentMeasure = ExperimentMeasure.build(measure: measure, experiment: experiment)
        experiment.experimentMeasures.add(experimentMeasure)

        return experimentMeasure
    }

    AssayContext createListItem(String label, List values) {
        Element attribute = Element.findByLabel(label);

        AssayContext context = AssayContext.build(assay: experiment.assay);
        values.each {
            AssayContextItem contextItem = AssayContextItem.build(assayContext: context, attributeElement: attribute, attributeType: AttributeType.Free, valueNum: it)
            context.addToAssayContextItems(contextItem)
        }

        return context
    }

    AssayContext createFreeItem(String label) {
        Element attribute = Element.findByLabel(label);

        AssayContext context = AssayContext.build(assay: experiment.assay);
        AssayContextItem contextItem = AssayContextItem.build(assayContext: context, attributeElement: attribute, attributeType: AttributeType.Free)
        context.addToAssayContextItems(contextItem)

        return context
    }

    def associateContext(ExperimentMeasure experimentMeasure, AssayContext context) {
        AssayContextMeasure assayContextMeasure = AssayContextMeasure.build(assayContext: context, measure:  experimentMeasure.measure)
        experimentMeasure.measure.addToAssayContextMeasures(assayContextMeasure)
        context.addToAssayContextMeasures(assayContextMeasure)
    }

    def addChild(ExperimentMeasure parent, ExperimentMeasure child) {
        child.parentChildRelationship = HierarchyType.Derives.toString();
        child.parent = parent
        parent.addToChildMeasures(child)
    }

    @Before
    void setup() {
        GrailsApplication grailsApplication = Mock(GrailsApplication)
        grailsApplication.config >> [bard: [services: [resultService: [archivePath: "out/ResultServiceIntegrationSpec"]]]]

        SpringSecurityUtils.reauthenticate('integrationTestUser', null)
        resultsService = new ResultsService()
        resultsService.setItemService(new ItemService())
        PugService pugService = Mock(PugService)
        resultsService.setPugService(pugService)
        experiment = Experiment.build()
        substance = Substance.build()

        ArchivePathService archivePathService = new ArchivePathService()
        archivePathService.grailsApplication = grailsApplication
        resultsService.archivePathService = archivePathService

        ResultsExportService resultsExportService = new ResultsExportService()
        resultsService.resultsExportService = resultsExportService
        resultsExportService.archivePathService = archivePathService

        def ec50 = createMeasure("EC50")
        def percentEffect = createMeasure("percent effect")
        def meanPercentEffect = createMeasure("percent effect", "mean")
        def assayConcentration = createListItem("assay component concentration", [0.394,1])
        def screeningConcentration = createFreeItem("screening concentration")
        def readoutName = createFreeItem("assay readout name")

        associateContext(ec50, readoutName)
        associateContext(meanPercentEffect, readoutName)
        associateContext(percentEffect, readoutName)

        associateContext(ec50, screeningConcentration)
        associateContext(meanPercentEffect, screeningConcentration)
        associateContext(percentEffect, screeningConcentration)

        associateContext(meanPercentEffect, assayConcentration)
        associateContext(percentEffect, assayConcentration)

        addChild(ec50, meanPercentEffect)
        addChild(meanPercentEffect, percentEffect)
    }

    InputStream transformStream(InputStream inputStream, Map replacements){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        IOUtils.copy(inputStream, baos);
        String text = new String(baos.toByteArray());
        replacements.entrySet().each {
            text = text.replace(it.key, it.value.toString())
        }
        return new ByteArrayInputStream(text.getBytes())
    }

    void 'test result deposition'() {
        when:
        InputStream inputStream = ResultServiceIntegrationSpec.getClassLoader().getResourceAsStream("bard/db/experiment/result-deposition-input.txt")
        assert inputStream != null
        inputStream = transformStream(inputStream, ["<EXPERIMENT_ID>": experiment.id, "<SUBSTANCE_ID>": substance.id])
        ResultsService.ImportSummary summary = resultsService.importResults(experiment, inputStream)

        then:
        !summary.hasErrors()
        summary.resultsCreated == 11
        summary.resultAnnotations == 31

// disabled writing to DB
//        when:
//        List<Result> results = Result.findAllByExperiment(experiment)
//
//        then:
//        results.size() == 11
    }
}
