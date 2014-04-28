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

package dataexport.experiment

import bard.db.enums.ContextType
import bard.db.experiment.AssayContextExperimentMeasure
import bard.db.experiment.Experiment
import bard.db.experiment.ExperimentContext
import bard.db.experiment.ExperimentContextItem
import bard.db.experiment.ExperimentMeasure
import bard.db.registration.ExternalReference
import common.tests.XmlTestAssertions
import dataexport.registration.MediaTypesDTO
import exceptions.NotFoundException
import grails.buildtestdata.TestDataConfigurationHolder
import grails.buildtestdata.mixin.Build
import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import groovy.xml.MarkupBuilder
import org.codehaus.groovy.grails.web.mapping.LinkGenerator
import org.springframework.core.io.FileSystemResource
import org.springframework.core.io.Resource
import spock.lang.Specification
import spock.lang.Unroll

import static common.tests.XmlTestSamples.*
import bard.db.enums.HierarchyType

/**
 * Created with IntelliJ IDEA.
 * User: jasiedu
 * Date: 6/19/12
 * Time: 12:52 PM
 * To change this template use File | Settings | File Templates.
 */
@TestFor(ExperimentExportService)
@Build([Experiment, ExperimentContext, ExperimentContextItem, ExperimentMeasure, ExternalReference, AssayContextExperimentMeasure])
@Mock([Experiment, ExperimentContext, ExperimentContextItem, ExperimentMeasure, ExternalReference, AssayContextExperimentMeasure])
@Unroll
class ExperimentExportServiceUnitSpec extends Specification {
    Writer writer
    MarkupBuilder markupBuilder

    ExperimentExportService experimentExportService
    final int maxNumberOfExperimentsPerPage = 2
    Resource schemaResource = new FileSystemResource(new File("web-app/schemas/experimentSchema.xsd"))

    void setup() {
        this.experimentExportService = this.service
        LinkGenerator grailsLinkGenerator = Mock(LinkGenerator.class)

        final MediaTypesDTO mediaTypesDTO = new MediaTypesDTO()
        mediaTypesDTO.elementMediaType = "application/vnd.bard.cap+xml;type=element"
        mediaTypesDTO.externalReferenceMediaType = "application/vnd.bard.cap+xml;type=externalReference"
        mediaTypesDTO.experimentsMediaType = "application/vnd.bard.cap+xml;type=experiments"
        mediaTypesDTO.experimentMediaType = "application/vnd.bard.cap+xml;type=experiment"
        mediaTypesDTO.projectMediaType = "application/vnd.bard.cap+xml;type=project"
        mediaTypesDTO.projectsMediaType = "application/vnd.bard.cap+xml;type=projects"
        mediaTypesDTO.projectDocMediaType = "application/vnd.bard.cap+xml;type=projectDoc"
        mediaTypesDTO.resultsMediaType = "application/vnd.bard.cap+xml;type=result"
        mediaTypesDTO.assayDocMediaType = "application/vnd.bard.cap+xml;type=assayDoc"
        mediaTypesDTO.assayMediaType = "application/vnd.bard.cap+xml;type=assay"


        this.experimentExportService.mediaTypeDTO = mediaTypesDTO
        this.experimentExportService.numberRecordsPerPage = maxNumberOfExperimentsPerPage
        this.experimentExportService.grailsLinkGenerator = grailsLinkGenerator
        this.writer = new StringWriter()
        this.markupBuilder = new MarkupBuilder(writer)
        TestDataConfigurationHolder.reset()
    }

    void "generate ExperimentContext #label"() {
        given:
        ExperimentContext experimentContext = ExperimentContext.build(map)
        numItems.times { ExperimentContextItem.build(experimentContext: experimentContext) }

        when:
        this.experimentExportService.generateExperimentContext(this.markupBuilder, experimentContext)

        then:
        String actualXml = this.writer.toString()
        XmlTestAssertions.assertResults(results, actualXml)
        XmlTestAssertions.validate(schemaResource, actualXml)

        where:
        label                         | results                        | numItems | map
        "Minimal"                     | CONTEXT_MINIMAL                | 0        | [contextType: ContextType.UNCLASSIFIED]
        "Minimal with name"           | CONTEXT_MINIMAL_WITH_NAME      | 0        | [contextType: ContextType.UNCLASSIFIED, contextName: 'contextName']
        "Minimal with group"          | CONTEXT_MINIMAL_WITH_GROUP     | 0        | [contextType: ContextType.BIOLOGY]
        "Minimal with 1 contextItem"  | CONTEXT_MINIMAL_WITH_ONE_ITEM  | 1        | [contextType: ContextType.UNCLASSIFIED]
        "Minimal with 2 contextItems" | CONTEXT_MINIMAL_WITH_TWO_ITEMS | 2        | [contextType: ContextType.UNCLASSIFIED]
    }

    void "test generate ExperimentMeasure #label"() {
        given:
        ExperimentMeasure experimentMeasure = ExperimentMeasure.build(mapClosure.call())

        when: "We attempt to generate a measure in xml"
        this.experimentExportService.generateExperimentMeasure(this.markupBuilder, experimentMeasure)

        then: "A valid xml measure is generated with the expected measure attributes, result type and entry unit"
        String actualXml = this.writer.toString()
        println actualXml
        XmlTestAssertions.assertResults(results, actualXml)
        XmlTestAssertions.validate(schemaResource, actualXml)

        where:
        label                             | results                                             | mapClosure | numAssayContextMeasureRefs
        "minimal"                         | EXPERIMENT_MEASURE_MINIMAL                          | { [:] }    | 0
        "with parentExperimentMeasureRef" | EXPERIMENT_MEASURE_WITH_PARENT_REF                  | {
            [parent: ExperimentMeasure.build(), priorityElement: true]
        }                                                                                                    | 0
        "with parentExperimentMeasureRef" | EXPERIMENT_MEASURE_WITH_PARENT_REF_AND_RELATIONSHIP | {
            [parent: ExperimentMeasure.build(), parentChildRelationship: HierarchyType.SUPPORTED_BY]
        }                                                                                                    | 0
    }

    void "test generate Experiment #label"() {
        given: "An Experiment"
        Experiment experiment = Experiment.build(map)
        numExtRef.times { ExternalReference.build(experiment: experiment) }
        numExpCtx.times { ExperimentContext.build(experiment: experiment, contextType: ContextType.UNCLASSIFIED) }
        numExpMsr.times { ExperimentMeasure.build(experiment: experiment) }
        experiment.lastUpdated = new Date(0)
        when: "We attempt to generate an experiment XML document"
        this.experimentExportService.generateExperiment(this.markupBuilder, experiment)

        then: "A valid xml document is generated and is similar to the expected document"
        String actualXml = this.writer.toString()
        XmlTestAssertions.assertResultsWithOverrideAttributes(results, actualXml)
        XmlTestAssertions.validate(schemaResource, actualXml)

        where:
        label                                    | results                                            | numExtRef | numExpCtx | numExpMsr | map
        "Minimal"                                | EXPERIMENT_MINIMAL                                 | 0         | 0         | 0         | [lastUpdated: new Date(0)]
        "with optional properties"               | EXPERIMENT_WITH_OPTIONAL_PROPERTIES                | 0         | 0         | 0         | [lastUpdated: new Date(0), holdUntilDate: new Date(0), runDateFrom: new Date(0), runDateTo: new Date(0), description: 'description']

        "with 1 ExternalReference"               | EXPERIMENT_WITH_ONE_EXT_REF                        | 1         | 0         | 0         | [lastUpdated: new Date(0)]
        "with 2 ExternalReferences"              | EXPERIMENT_WITH_TWO_EXT_REF                        | 2         | 0         | 0         | [lastUpdated: new Date(0)]

        "with 1 context"                         | EXPERIMENT_WITH_ONE_CONTEXT                        | 0         | 1         | 0         | [lastUpdated: new Date(0)]
        "with 2 contexts"                        | EXPERIMENT_WITH_TWO_CONTEXT                        | 0         | 2         | 0         | [lastUpdated: new Date(0)]

        "with 1 context and 1 ExperimentMeasure" | EXPERIMENT_WITH_ONE_CONTEXT_ONE_EXPERIMENT_MEASURE | 0         | 1         | 1         | [lastUpdated: new Date(0)]

        "with 1 ExperimentMeasure"               | EXPERIMENT_WITH_ONE_EXPERIMENT_MEASURE             | 0         | 0         | 1         | [lastUpdated: new Date(0)]
        "with 2 ExperimentMeasures"              | EXPERIMENT_WITH_TWO_EXPERIMENT_MEASURE             | 0         | 0         | 2         | [lastUpdated: new Date(0)]
    }

    void "test generated Experiment modifiedBy #label"() {
        given: "An Experiment"
        Experiment experiment = Experiment.build()
        experiment.modifiedBy = modifiedBy

        when: "We attempt to generate an experiment XML document"
        this.experimentExportService.generateExperiment(this.markupBuilder, experiment)

        then:
        String actualXml = this.writer.toString()
        def resultXml = new XmlSlurper().parseText(actualXml)
        resultXml.@modifiedBy == expectedModifiedBy

        where:
        label           | modifiedBy    | expectedModifiedBy
        "without email" | 'foo'         | 'foo'
        "with email"    | 'foo@foo.com' | 'foo'

    }

    void "test Generate Experiment Not Found Exception"() {
        when: "We attempt to generate an Experiment"
        this.experimentExportService.generateExperiment(this.markupBuilder, 1)

        then: "An exception should be thrown"
        thrown(NotFoundException)
    }

}
