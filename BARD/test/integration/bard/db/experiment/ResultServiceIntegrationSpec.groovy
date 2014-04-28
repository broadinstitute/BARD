/* Copyright (c) 2014, The Broad Institute
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
    * Redistributions of source code must retain the above copyright
      notice, this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright
      notice, this list of conditions and the following disclaimer in the
      documentation and/or other materials provided with the distribution.
    * Neither the name of The Broad Institute nor the
      names of its contributors may be used to endorse or promote products
      derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL The Broad Institute BE LIABLE FOR ANY
DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package bard.db.experiment

import bard.db.dictionary.Element
import bard.db.enums.HierarchyType
import bard.db.experiment.results.ImportSummary
import bard.db.registration.*
import grails.plugin.spock.IntegrationSpec
import grails.plugins.springsecurity.SpringSecurityService
import org.apache.commons.io.IOUtils
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils
import org.junit.Before

import static bard.db.enums.ExpectedValueType.NUMERIC

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
    Map<String, Element> byName = [:]
    SpringSecurityService springSecurityService;

    Element findElementByName(String label) {
        Element element = byName[label]
        if (element == null) {
//            element = Element.findByLabel(label);
 //           if(element == null) {
                element = Element.build(label: label, expectedValueType: NUMERIC)
  //          }
            byName[label] = element
        }
        return element
    }

    ExperimentMeasure createMeasure(String label, String statistic = null) {
        Element resultType = findElementByName(label)
        Element statsModifier = null;
        if (statistic != null) {
            statsModifier = findElementByName(statistic);
        }

        ExperimentMeasure experimentMeasure = ExperimentMeasure.build(experiment: experiment, resultType: resultType, statsModifier: statsModifier)
        //experiment.assa.measures.add(measure)

        //ExperimentMeasure experimentMeasure = ExperimentMeasure.build(measure: measure, experiment: experiment)
        //experiment.experimentMeasures.add(experimentMeasure)

        return experimentMeasure
    }

    AssayContext createListItem(String label, List values) {
        Element attribute = findElementByName(label);

        AssayContext context = AssayContext.build(assay: experiment.assay);
        values.each {
            AssayContextItem contextItem = AssayContextItem.build(assayContext: context, attributeElement: attribute, attributeType: AttributeType.List, valueNum: it, qualifier:'= ', valueDisplay: 'someValue')
//            context.addToAssayContextItems(contextItem)
        }

        return context
    }

    AssayContext createFreeItem(String label) {
        Element attribute = findElementByName(label);

        AssayContext context = AssayContext.build(assay: experiment.assay);
        AssayContextItem contextItem = AssayContextItem.build(assayContext: context, attributeElement: attribute, attributeType: AttributeType.Free, valueDisplay:null)
//        context.addToAssayContextItems(contextItem)

        return context
    }

    def associateContext(ExperimentMeasure experimentMeasure, AssayContext context) {
        AssayContextExperimentMeasure assayContextMeasure = AssayContextExperimentMeasure.build(assayContext: context, experimentMeasure: experimentMeasure)
//        experimentMeasure.measure.addToAssayContextMeasures(assayContextMeasure)
//        context.addToAssayContextMeasures(assayContextMeasure)
    }

    def addChild(ExperimentMeasure parent, ExperimentMeasure child) {
        child.parentChildRelationship = HierarchyType.CALCULATED_FROM;
        child.parent = parent
        parent.addToChildMeasures(child)
    }

    @Before
    void setup() {
        GrailsApplication grailsApplication = Mock(GrailsApplication)
        grailsApplication.config >> [bard: [services: [resultService: [archivePath: "out/ResultServiceIntegrationSpec"]]]]

        SpringSecurityUtils.reauthenticate('integrationTestUser', null)
        resultsService = new ResultsService()
        assert springSecurityService != null
        resultsService.springSecurityService = springSecurityService
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

        def ec50 = createMeasure("xEC50")
        def percentEffect = createMeasure("xpercent effect")
        def meanPercentEffect = createMeasure("xpercent effect", "xmean")
        def assayConcentration = createListItem("xassay component concentration", [0.394,1])
        def screeningConcentration = createFreeItem("xscreening concentration")
        def readoutName = createFreeItem("xassay readout name")

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
        ImportSummary summary = resultsService.importResults(experiment.id, inputStream)

        then:
        !summary.hasErrors()
        summary.resultsCreated == 11
        summary.resultAnnotations == 31

        when:
        ExperimentFile file = ExperimentFile.findByExperiment(experiment)

        then:
        file != null
        file.substanceCount == 1
// disabled writing to DB
//        when:
//        List<Result> results = Result.findAllByExperiment(experiment)
//
//        then:
//        results.size() == 11
    }
}
